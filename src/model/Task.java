package model;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class Task {
    private volatile String id;
    private volatile String subject;
    private volatile String title;
    private volatile String dueDate;
    private volatile String status;
    private volatile String description;
    private volatile boolean isImportant = false;

    public Task(String subject, String title, String dueDate, String status, String description) {
        this.id = UUID.randomUUID().toString();
        this.subject = subject;
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.description = description;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
    
    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean important) { this.isImportant = important; }

    // ตรวจสอบการเสร็จสิ้นผ่าน Status.COMPLETED
    public boolean isCompleted() {
        return Status.COMPLETED.equalsIgnoreCase(status);
    }

    // สลับสถานะระหว่าง In Progress กับ Completed
    public void toggleStatus() {
        if (isCompleted()) {
            this.status = Status.IN_PROGRESS;
        } else {
            this.status = Status.COMPLETED;
        }
    }

    // ดึงสีโดยเรียกผ่าน Status.java
    public Color getStatusColor() {
        return Status.getColor(isCompleted(), dueDate);
    }

    // ดึงข้อความ Badge โดยเรียกผ่าน Status.java
    public String getStatusText() {
        return Status.getDisplayText(isCompleted(), dueDate);
    }

    private static final DateTimeFormatter DUE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d-M-uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    // ตัวช่วยแปลง String วันที่ รองรับ d-M-yyyy
    public static LocalDate parseDueDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr.trim(), DUE_DATE_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
