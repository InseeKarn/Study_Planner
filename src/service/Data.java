package service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import model.Status;
import model.Task;

public class Data {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final List<Task> taskList = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final File dataFile = new File("data/tasks.json");

    public Data() {
        loadFromJson();
    }

    // --- ดึงรายชื่อวิชาที่ไม่ซ้ำกัน (สำหรับ Filter) ---
    public List<String> getDistinctSubjects() {
        List<String> subjects = new ArrayList<>();
        for (Task t : getAllTasks()) {
            String sub = t.getSubject();
            if (sub != null && !sub.trim().isEmpty() && !subjects.contains(sub.trim())) {
                subjects.add(sub.trim());
            }
        }
        Collections.sort(subjects);
        return subjects;
    }

    // --- กรองงานตามสถานะและวิชา ---
    public List<Task> getFilteredTasks(
            String filterStatus,
            String filterSubject,
            boolean filterStarred
	) {
        List<Task> allTasks = getAllTasks();

        boolean allStatus = (filterStatus == null || filterStatus.equals("All"));
        boolean allSubject = (filterSubject == null || filterSubject.equals("All"));

        List<Task> filtered = new ArrayList<>();
        for (Task task : allTasks) {
        	
            // ตรวจสอบ Starred
        	if (filterStarred && !task.isImportant()) {
        	    continue;
        	}
        	
            // ตรวจสอบเงื่อนไขสถานะ
            if (!allStatus) {
                String taskStatus = Status.calculateStatus(task.isCompleted(), task.getDueDate());
                if (!matchesStatusFilter(taskStatus, filterStatus)) {
                    continue;
                }
            }

            // ตรวจสอบเงื่อนไขวิชา
            if (!allSubject) {
                if (task.getSubject() == null || !task.getSubject().trim().equals(filterSubject)) {
                    continue;
                }
            }

            filtered.add(task);
        }
        return filtered;
    }

    // จับคู่สถานะ filter กับสถานะจริงของงาน
    private boolean matchesStatusFilter(String taskStatus, String filterStatus) {
        switch (filterStatus) {
            case "Completed":
                return taskStatus.equals(Status.COMPLETED);
            case "In Progress":
                return !taskStatus.equals(Status.COMPLETED)
                        && !taskStatus.equals(Status.DUE_TODAY)
                        && !taskStatus.equals(Status.OVERDUE);
            case "Overdue":
                return taskStatus.equals(Status.OVERDUE);
            case "Due Today":
                return taskStatus.equals(Status.DUE_TODAY);
            case "Urgent":
                return taskStatus.equals(Status.URGENT);
            case "Warning":
                return taskStatus.equals(Status.WARNING);
            case "Normal":
                return taskStatus.equals(Status.NORMAL);
            case "No Due Date":
                return taskStatus.equals(Status.NO_DUE_DATE);
            default:
                return true;
        }
    }

    // --- ดึงงานทั้งหมด พร้อมจัดเรียงตามลำดับความด่วนและวันกำหนดส่งให้อัตโนมัติ ---

public List<Task> getAllTasks() {
    List<Task> snapshot;
    synchronized (taskList) {
        snapshot = new ArrayList<>(taskList);
    }
    snapshot.sort((t1, t2) -> {

        // 1. งานติดดาวขึ้นก่อน
        if (t1.isImportant() != t2.isImportant()) {
            return Boolean.compare(t2.isImportant(), t1.isImportant());
        }

        // 2. เรียงตามสถานะเหมือนเดิม
        int p1 = Status.getPriorityOrder(t1.isCompleted(), t1.getDueDate());
        int p2 = Status.getPriorityOrder(t2.isCompleted(), t2.getDueDate());

        if (p1 != p2) {
            return Integer.compare(p1, p2);
        }

        // 3. เรียงตามวันกำหนดส่งเหมือนเดิม
        LocalDate d1 = Task.parseDueDate(t1.getDueDate());
        LocalDate d2 = Task.parseDueDate(t2.getDueDate());

        if (d1 != null && d2 != null) {
            return d1.compareTo(d2);
        } else if (d1 != null) {
            return -1;
        } else if (d2 != null) {
            return 1;
        }

        return 0;
    });

    return snapshot;
}

    // --- ดึงรายการงานเฉพาะวันที่กำหนด (สำหรับ Schedule - Month View) ---
    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            LocalDate due = Task.parseDueDate(t.getDueDate());
            if (due != null && due.equals(date)) {
                result.add(t);
            }
        }
        return result;
    }

    // --- นับจำนวนงานในเดือนและปีที่กำหนด (สำหรับ Schedule - Year View) ---
    public int getTaskCountForMonth(int year, int month) {
        int count = 0;
        for (Task t : getAllTasks()) {
            LocalDate due = Task.parseDueDate(t.getDueDate());
            if (due != null && due.getYear() == year && due.getMonthValue() == month) {
                count++;
            }
        }
        return count;
    }

    public void addTask(Task task) {
        synchronized (taskList) {
            taskList.add(task);
        }
        saveToJson();
        pcs.firePropertyChange("taskList", null, getAllTasks());
    }

    public void deleteTask(Task task) {
        synchronized (taskList) {
            taskList.remove(task);
        }
        saveToJson();
        pcs.firePropertyChange("taskList", null, getAllTasks());
    }

    public synchronized void saveToJson() {
        Path target = dataFile.toPath();
        Path parent = target.getParent();
        Path temporaryFile = null;

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }

            temporaryFile = Files.createTempFile(
                    parent,
                    dataFile.getName(),
                    ".tmp"
            );

            try (var writer = Files.newBufferedWriter(
                    temporaryFile,
                    StandardCharsets.UTF_8
            )) {
                gson.toJson(getAllTasks(), writer);
            }

            try {
                Files.move(
                        temporaryFile,
                        target,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        temporaryFile,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to save " + target, e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException e) {
                    System.err.println("Unable to remove temporary task file: " + temporaryFile);
                }
            }
        }
    }

    private void loadFromJson() {
        if (!dataFile.exists()) {
            saveToJson();
            return;
        }

        List<Task> loadedTasks;
        try (FileReader reader = new FileReader(dataFile)) {
            Type taskListType = new TypeToken<List<Task>>() {}.getType();
            loadedTasks = gson.fromJson(reader, taskListType);
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException(
                    "Unable to read task data from " + dataFile + ". The file was left unchanged.",
                    e
            );
        }

        if (loadedTasks == null) {
            return;
        }

        boolean assignedMissingIds = false;
        for (Task task : loadedTasks) {
            if (task == null) {
                throw new IllegalStateException(
                        "Task data contains a null entry in " + dataFile + ". The file was left unchanged."
                );
            }
            if (task.getId() == null || task.getId().isBlank()) {
                task.setId(UUID.randomUUID().toString());
                assignedMissingIds = true;
            }
        }

        synchronized (taskList) {
            taskList.clear();
            taskList.addAll(loadedTasks);
        }

        if (assignedMissingIds) {
            saveToJson();
        }
    }

    public int getCompletedCount() {
        return countCompleted(getAllTasks());
    }

    public int getTotalCount() {
        return getAllTasks().size();
    }

    public int getPendingCount() {
        List<Task> snapshot = getAllTasks();
        return snapshot.size() - countCompleted(snapshot);
    }

    public int getProgressPercentage() {
        List<Task> snapshot = getAllTasks();
        int total = snapshot.size();
        if (total == 0) return 0;
        return (int) (((double) countCompleted(snapshot) / total) * 100);
    }

    private int countCompleted(List<Task> tasks) {
        int count = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
