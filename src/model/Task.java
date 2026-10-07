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
    
    // Status Colors
    private static final Color COLOR_COMPLETED = new Color(46, 125, 80);    // Green
    private static final Color COLOR_OVERDUE = new Color(190, 55, 75);         // Red
    private static final Color COLOR_URGENT = new Color(125, 75, 165);      // Purple
    private static final Color COLOR_WARNING = new Color(190, 135, 35);     // Gold
    private static final Color COLOR_NORMAL = new Color(48, 82, 160);       // Blue

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

    // Status Color
    public Color getStatusColor() {

        if (isCompleted()) {
            return COLOR_COMPLETED;
        }

        try {
            LocalDate due = parseDueDate(dueDate);

            if (due != null) {
                long daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    due
                );

                // Overdue
                if (daysLeft < 0) {
                    return COLOR_OVERDUE;
                }

                // เหลือ 1-5 วัน || Due Today - เหลือ 0 วัน
                if (daysLeft <= 5 || daysLeft == 0) {
                    return COLOR_URGENT;
                }

                // เหลือ 6-7 วัน
                if (daysLeft <= 7) {
                    return COLOR_WARNING;
                }

                // เหลือมากกว่า 7 วัน
                return COLOR_NORMAL;
            }

        } catch (Exception ignored) {}

        return COLOR_NORMAL;
    }

    // --- คำนวณข้อความประจำสถานะ ---
    public String getStatusText() {

        if (isCompleted()) {
            return "Completed";
        }

        try {
            LocalDate due = parseDueDate(dueDate);

            if (due != null) {
                long daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    due
                );

                if (daysLeft < 0) {
                    return "Overdue";

                } else if (daysLeft == 0) {
                    return "Due Today";

                } else {
                    return "Days Left (" + daysLeft + "d)";
                }
            }

        } catch (Exception ignored) {}

        return "No Due Date";
    }

    // ตัวช่วยแปลง String วันที่ รองรับ DD-MM-YYYY
    public static LocalDate parseDueDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("d-M-yyyy");

            return LocalDate.parse(dateStr.trim(), formatter);

        } catch (Exception ignored) {
            return null;
        }
    }
}