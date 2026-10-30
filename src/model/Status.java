package model;

import java.awt.Color;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Status {

    // ค่าสถานะ String Constants
    public static final String COMPLETED   = "Completed";
    public static final String IN_PROGRESS = "In Progress";
    public static final String OVERDUE     = "Overdue";
    public static final String DUE_TODAY   = "Due Today";
    public static final String URGENT      = "Urgent";
    public static final String WARNING     = "Warning";
    public static final String NORMAL      = "Normal";
    public static final String NO_DUE_DATE = "No Due Date";

    // กำหนดสีประจำสถานะ
    private static final Color COLOR_COMPLETED = new Color(46, 125, 80);  // Green
    private static final Color COLOR_OVERDUE   = new Color(190, 55, 75);   // Red
    private static final Color COLOR_URGENT    = new Color(125, 75, 165);  // Purple
    private static final Color COLOR_WARNING   = new Color(190, 135, 35);  // Gold
    private static final Color COLOR_NORMAL    = new Color(48, 82, 160);   // Blue

    // คำนวณชื่อสถานะ
    public static String calculateStatus(boolean isCompleted, String dueDateStr) {
        if (isCompleted) {
            return COMPLETED;
        }

        LocalDate due = Task.parseDueDate(dueDateStr);
        if (due == null) {
            return NO_DUE_DATE;
        }

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), due);

        if (daysLeft < 0) {
            return OVERDUE;
        } else if (daysLeft == 0) {
            return DUE_TODAY;
        } else if (daysLeft <= 5) {
            return URGENT;
        } else if (daysLeft <= 7) {
            return WARNING;
        } else {
            return NORMAL;
        }
    }

    // ดึงสีตามสถานะที่คำนวณได้
    public static Color getColor(boolean isCompleted, String dueDateStr) {
        String status = calculateStatus(isCompleted, dueDateStr);
        
        switch (status) {
            case COMPLETED:
                return COLOR_COMPLETED;
            case OVERDUE:
                return COLOR_OVERDUE;
            case DUE_TODAY:
            case URGENT:
                return COLOR_URGENT;
            case WARNING:
                return COLOR_WARNING;
            default:
                return COLOR_NORMAL;
        }
    }

    // ดึงข้อความสำหรับแสดงบน Badge ในตาราง
    public static String getDisplayText(boolean isCompleted, String dueDateStr) {
        if (isCompleted) {
            return COMPLETED;
        }

        LocalDate due = Task.parseDueDate(dueDateStr);
        if (due == null) {
            return NO_DUE_DATE;
        }

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), due);

        if (daysLeft < 0) {
            return OVERDUE;
        } else if (daysLeft == 0) {
            return DUE_TODAY;
        } else {
            return "Days Left (" + daysLeft + "d)";
        }
    }

    // สำหรับใช้จัดเรียงลำดับความสำคัญของงาน (1 = ด่วนที่สุด)
    public static int getPriorityOrder(boolean isCompleted, String dueDateStr) {
        String status = calculateStatus(isCompleted, dueDateStr);
        
        switch (status) {
            case OVERDUE:     return 1;
            case DUE_TODAY:   return 2;
            case URGENT:      return 3;
            case WARNING:     return 4;
            case NORMAL:      return 5;
            case NO_DUE_DATE: return 6;
            case COMPLETED:   return 7;
            default:          return 8;
        }
    }
}