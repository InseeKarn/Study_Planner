package view.schedule;

import java.awt.Component;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import model.Task;

public class ScheduleDialog {

    public static void showTaskDetails(Component parent, Task task) {
        String details = String.format(
            "<html><body style='width: 260px; font-family: SansSerif; color: #222222;'>"
            + "<h2 style='color: #2E7D32; margin-bottom: 5px;'>%s</h2>"
            + "<hr style='border: 0.5px solid #CCCCCC;'>"
            + "<p style='margin: 4px 0;'><b>Subject:</b> %s</p>"
            + "<p style='margin: 4px 0;'><b>Due Date:</b> %s</p>"
            + "<p style='margin: 4px 0;'><b>Status / Priority:</b> %s</p>"
            + "<p style='margin: 4px 0;'><b>Description:</b></p>"
            + "<div style='background-color: #F0F0F0; color: #333333; padding: 8px; border-radius: 4px; border: 1px solid #DDDDDD;'>%s</div>"
            + "</body></html>",
            task.getTitle(),
            task.getSubject() != null ? task.getSubject() : "-",
            task.getDueDate() != null ? task.getDueDate() : "-",
            task.getStatus() != null ? task.getStatus() : "-",
            (task.getDescription() != null && !task.getDescription().trim().isEmpty()) ? task.getDescription() : "-"
        );

        JOptionPane.showMessageDialog(
            parent,
            details,
            "Task Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showDayTasksSummary(Component parent, LocalDate date, List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return;

        if (tasks.size() == 1) {
            showTaskDetails(parent, tasks.get(0));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width: 280px; font-family: SansSerif; color: #222222;'>");
        sb.append("<h2 style='color: #2E7D32; margin-bottom: 5px;'>Tasks on ").append(date.toString()).append("</h2>");
        sb.append("<hr style='border: 0.5px solid #CCCCCC;'>");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            sb.append("<div style='margin-bottom: 10px;'>");
            sb.append("<b style='color: #1565C0;'>").append(i + 1).append(". ").append(task.getTitle()).append("</b><br>");
            if (task.getSubject() != null && !task.getSubject().isEmpty()) {
                sb.append("<span style='font-size: 11px; color: #555555;'>Subject: ").append(task.getSubject()).append("</span><br>");
            }
            sb.append("<span style='font-size: 11px; color: #555555;'>Status: ").append(task.getStatus() != null ? task.getStatus() : "-").append("</span>");
            sb.append("</div>");
        }
        sb.append("</body></html>");

        JOptionPane.showMessageDialog(
            parent,
            sb.toString(),
            "Daily Tasks Overview",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}