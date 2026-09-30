package model;

public class Task {
    private String subject;
    private String title;
    private String dueDate;
    private String status;
    private String description;

    public Task(String subject, String title, String dueDate, String status, String description) {
        this.subject = subject;
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.description = description;
    }

    // Getters & Setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }

    public void toggleStatus() {
        if (isCompleted()) {
            this.status = "In Progress";
        } else {
            this.status = "Completed";
        }
    }
}