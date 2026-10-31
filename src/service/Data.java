package service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        for (Task t : taskList) {
            String sub = t.getSubject();
            if (sub != null && !sub.trim().isEmpty() && !subjects.contains(sub.trim())) {
                subjects.add(sub.trim());
            }
        }
        Collections.sort(subjects);
        return subjects;
    }

    // --- กรองงานตามสถานะและวิชา ---
    public List<Task> getFilteredTasks(String filterStatus, String filterSubject) {
        List<Task> allTasks = getAllTasks();

        boolean allStatus = (filterStatus == null || filterStatus.equals("All"));
        boolean allSubject = (filterSubject == null || filterSubject.equals("All"));

        if (allStatus && allSubject) {
            return allTasks;
        }

        List<Task> filtered = new ArrayList<>();
        for (Task task : allTasks) {
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
                return !taskStatus.equals(Status.COMPLETED);
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
    taskList.sort((t1, t2) -> {

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

    return taskList;
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
        taskList.add(task);
        saveToJson();
        pcs.firePropertyChange("taskList", null, taskList);
    }

    public void deleteTask(Task task) {
        taskList.remove(task);
        saveToJson();
        pcs.firePropertyChange("taskList", null, taskList);
    }

    public void saveToJson() {
        try {
            File parentFolder = dataFile.getParentFile();

            if (parentFolder != null && !parentFolder.exists()) {
                parentFolder.mkdirs();
            }

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(taskList, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromJson() {
        if (!dataFile.exists()) {
            saveToJson();
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type taskListType = new TypeToken<List<Task>>() {}.getType();
            List<Task> loadedTasks = gson.fromJson(reader, taskListType);

            if (loadedTasks != null) {
                taskList.clear();
                taskList.addAll(loadedTasks);
            }

        } catch (IOException e) {
            e.printStackTrace();
            taskList.clear();
        }
    }

    public int getCompletedCount() {
        int count = 0;
        for (Task task : taskList) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalCount() {
        return taskList.size();
    }

    public int getPendingCount() {
        return getTotalCount() - getCompletedCount();
    }

    public int getProgressPercentage() {
        int total = getTotalCount();
        if (total == 0) return 0;
        return (int) (((double) getCompletedCount() / total) * 100);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
