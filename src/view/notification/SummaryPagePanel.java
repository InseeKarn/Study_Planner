package view.notification;

import java.awt.*;
import javax.swing.*;

public class SummaryPagePanel extends NotificationPage {

    private final Color cardBg = new Color(24, 24, 24);
    private final Color boxBg = new Color(32, 32, 32);
    private final Color borderColor = new Color(45, 45, 45);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);

    private final Color goldColor = new Color(220, 160, 50);
    private final Color redColor = new Color(210, 70, 70);
    private final Color greenColor = new Color(143, 214, 148);
    private final Color blueColor = new Color(100, 180, 246);

    public SummaryPagePanel(String dateStr, TaskSummary summary) {
        setLayout(new BorderLayout(0, 6));
        setBackground(cardBg);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        // Header Date Label
        JPanel headerBox = new JPanel(new GridLayout(2, 1, 0, 1));
        headerBox.setOpaque(false);

        JLabel appTitle = new JLabel("STUDYPLANNER");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        appTitle.setForeground(textSecondary);

        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Serif", Font.BOLD, 20));
        dateLabel.setForeground(textPrimary);

        headerBox.add(appTitle);
        headerBox.add(dateLabel);
        add(headerBox, BorderLayout.NORTH);

        // 4 Stat Cards Grid (TODAY, OVERDUE, IN PROGRESS, TOTAL)
        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setOpaque(false);

        grid.add(createStatBox(String.valueOf(summary.getTodayCount()), "TODAY", goldColor));
        grid.add(createStatBox(String.valueOf(summary.getOverdueCount()), "OVERDUE", redColor));
        grid.add(createStatBox(String.valueOf(summary.getInProgressCount()), "IN PROGRESS", greenColor));
        grid.add(createStatBox(String.valueOf(summary.getTotalPendingCount()), "TOTAL", blueColor));

        add(grid, BorderLayout.CENTER);
    }

    private JPanel createStatBox(String count, String title, Color numberColor) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(boxBg);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleLbl.setForeground(textSecondary);

        JLabel numLbl = new JLabel(count);
        numLbl.setFont(new Font("Serif", Font.BOLD, 20));
        numLbl.setForeground(numberColor);
        numLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        box.add(titleLbl, BorderLayout.WEST);
        box.add(numLbl, BorderLayout.EAST);
        return box;
    }

    @Override
    public String getPageTitle() {
        return "SUMMARY";
    }
}