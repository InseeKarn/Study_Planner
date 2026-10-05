package model;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

    // --- คำนวณสีประจำสถานะ ---
    public Color getStatusColor() {
        if (isCompleted()) {
            return new Color(46, 125, 50); // 🟢 สีเขียว (Completed)
        }

        try {
            LocalDate due = parseDueDate(dueDate);
            if (due != null) {
                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), due);
                if (daysLeft <= 3) {
                    return new Color(198, 40, 40); // 🔴 สีแดง (เหลือ <= 3 วัน หรือเลยกำหนด)
                }
            }
        } catch (Exception ignored) {}

        return new Color(230, 160, 0); // 🟡 สีเหลือง (In Progress > 3 วัน)
    }

    // --- คำนวณข้อความประจำสถานะ ---
    public String getStatusText() {
        if (isCompleted()) {
            return "Completed";
        }

        try {
            LocalDate due = parseDueDate(dueDate);
            if (due != null) {
                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), due);
                if (daysLeft <= 3) {
                    return daysLeft < 0 ? "Overdue" : "Urgent (" + daysLeft + "d)";
                }
            }
        } catch (Exception ignored) {}

        return "In Progress";
    }

    // ตัวช่วยแปลง String วันที่ รองรับ DD-MM-YYYY, DD/MM/YYYY และ YYYY-MM-DD
    private LocalDate parseDueDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        dateStr = dateStr.trim();
        try {
            if (dateStr.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("d-M-yyyy"));
            } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("d/M/yyyy"));
            } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                return LocalDate.parse(dateStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    
}