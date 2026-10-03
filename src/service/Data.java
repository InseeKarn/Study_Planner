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

import model.Task;

public class Data {
	
    private final List<Task> taskList = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    private final File dataFile = new File("data/tasks.json");
    
    public Data() {
    	loadFromJson();
    }

    public List<Task> getAllTasks() {
        return taskList;
    }

    public void addTask(Task task) {
        taskList.add(task);
        saveToJson();
    }
    
    public void saveToJson() {

        try {

            File parentFolder = dataFile.getParentFile();

            if (!parentFolder.exists()) {
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

            Type taskListType =
                    new TypeToken<List<Task>>() {}.getType();

            List<Task> loadedTasks =
                    gson.fromJson(reader, taskListType);

            if (loadedTasks != null) {
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
    }


}