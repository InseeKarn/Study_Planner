package service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;

import model.Status;
import model.Task;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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

    // --- จัดเรียงงานตามสถานะและความด่วนอัตโนมัติ ---
    public List<Task> getAllTasks() {
        taskList.sort((t1, t2) -> {
            // 1. เปรียบเทียบลำดับความด่วนของสถานะ (1 = ด่วนที่สุด -> 7 = งานที่เสร็จแล้ว)
            int p1 = Status.getPriorityOrder(t1.isCompleted(), t1.getDueDate());
            int p2 = Status.getPriorityOrder(t2.isCompleted(), t2.getDueDate());

            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }

            // 2. ถ้าสถานะเท่ากัน ให้เรียงตามวันกำหนดส่ง (วันที่ถึงก่อนขึ้นก่อน)
            LocalDate d1 = Task.parseDueDate(t1.getDueDate());
            LocalDate d2 = Task.parseDueDate(t2.getDueDate());

            if (d1 != null && d2 != null) {
                return d1.compareTo(d2);
            } else if (d1 != null) {
                return -1; // มีวันกำหนดส่งขึ้นก่อนงานที่ไม่มีวันกำหนดส่ง
            } else if (d2 != null) {
                return 1;
            }

            return 0;
        });

        return taskList;
    }

    public void addTask(Task task) {
        taskList.add(task);
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

    public void deleteTask(Task task) {
        taskList.remove(task);
        saveToJson();
        pcs.firePropertyChange("taskList", null, taskList);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}