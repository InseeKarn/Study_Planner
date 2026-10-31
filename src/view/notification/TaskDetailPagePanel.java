package view.notification;

import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import model.Task;

public class TaskDetailPagePanel extends NotificationPage {

    private final Color cardBg = new Color(24, 24, 24);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);

    public TaskDetailPagePanel(Task task, String categoryTag) {
        setLayout(new BorderLayout(0, 6));
        setBackground(cardBg);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        // 1. Top Row: Status Tag & Dynamic Due Text (คำนวณจำนวนวันที่เหลือ)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel tagLabel = new JLabel(categoryTag.toUpperCase());
        tagLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        tagLabel.setForeground(task.getStatusColor());

        String dueText = calculateDueText(task);
        JLabel dueLabel = new JLabel(dueText);
        dueLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        dueLabel.setForeground(textSecondary);

        topPanel.add(tagLabel, BorderLayout.WEST);
        topPanel.add(dueLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Content: เรียงตามลำดับ วิชา ➔ งาน ➔ รายละเอียดงาน
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // (1) ชื่อวิชา (Subject)
        JLabel subLabel = new JLabel("• " + (task.getSubject() != null && !task.getSubject().trim().isEmpty() ? task.getSubject() : "-"));
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLabel.setForeground(textSecondary);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // (2) ชื่องาน (Title / Assignment)
        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // (3) รายละเอียดงาน (Description)
        JTextArea descArea = new JTextArea(task.getDescription() != null ? task.getDescription() : "");
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descArea.setForeground(new Color(180, 180, 180));
        descArea.setBackground(cardBg);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(subLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(descArea);

        add(contentPanel, BorderLayout.CENTER);
    }

    private String calculateDueText(Task task) {
        LocalDate due = Task.parseDueDate(task.getDueDate());
        if (due == null) {
            return "NO DUE DATE";
        }

        LocalDate today = LocalDate.now();

        if (due.isBefore(today)) {
            return "OVERDUE";
        } else if (due.equals(today)) {
            return "DUE TODAY";
        } else {
            long daysLeft = ChronoUnit.DAYS.between(today, due);
            return "DAYS LEFT (" + daysLeft + ")";
        }
    }

    @Override
    public String getPageTitle() {
        return "DETAIL";
    }
}