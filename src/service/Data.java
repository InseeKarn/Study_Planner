package service;

import java.util.ArrayList;
import java.util.List;

import view.Task;

public class Data {
    private final List<Task> taskList = new ArrayList<>();

    public Data() {
        initDefaultData();
    }

    public List<Task> getAllTasks() {
        return taskList;
    }

    public void addTask(Task task) {
        taskList.add(task);
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

    private void initDefaultData() {
        taskList.add(new Task("Calculus", "Calculus II Homework", "12/01", "Overdue", "Chapter 5 Exercise 1-10"));
        taskList.add(new Task("History", "Chapter 3 Homework", "14/01", "In Progress", "Summary World War I"));
        taskList.add(new Task("Physics", "Chapter 3 Homework", "14/01", "In Progress", "Newton's laws problems"));
        taskList.add(new Task("Literature", "Essay on Modernism", "14/01", "In Progress", "1500 words essay"));
        taskList.add(new Task("Philosophy", "Ethics Response Paper", "12/01", "In Progress", "Read Chapter 2 first"));
        taskList.add(new Task("Calculus", "Problem Set 7", "14/01", "In Progress", "Calculus Derivatives"));
        taskList.add(new Task("History", "Bismarck Essay Draft", "14/01", "In Progress", "Drafting 5 pages"));
        taskList.add(new Task("Physics", "Lab Report - Optics", "14/01", "Completed", "Optics Experiment PDF"));
        taskList.add(new Task("Literature", "Virginia Woolf Annotation", "14/01", "In Progress", "Annotate Mrs. Dalloway"));
        taskList.add(new Task("Philosophy", "Kant Groundwork Reading", "14/01", "Completed", "Read pages 40-80"));
    }
}