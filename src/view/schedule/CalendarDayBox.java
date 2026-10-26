package view.schedule;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import model.Task;

public class CalendarDayBox extends JPanel {

    private final Color dayBoxBg = new Color(32, 32, 32);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color accentGreen = new Color(143, 214, 148);
    private final Color borderDark = new Color(45, 45, 45);

    public CalendarDayBox(LocalDate date, LocalDate today, List<Task> tasksOnDate) {
        setLayout(new BorderLayout(3, 3));
        setBackground(dayBoxBg);
        setBorder(BorderFactory.createLineBorder(borderDark, 1));

        boolean isToday = date.equals(today);
        if (isToday) {
            setBorder(BorderFactory.createLineBorder(accentGreen, 2));
        }

        JLabel dayNumLabel = new JLabel(" " + date.getDayOfMonth());
        dayNumLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        dayNumLabel.setForeground(isToday ? accentGreen : textPrimary);
        add(dayNumLabel, BorderLayout.NORTH);

        if (tasksOnDate != null && !tasksOnDate.isEmpty()) {
            JPanel taskListPanel = new JPanel();
            taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
            taskListPanel.setOpaque(false);

            for (Task task : tasksOnDate) {
                JLabel taskLabel = new JLabel("• " + task.getTitle());
                taskLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                taskLabel.setForeground(task.getStatusColor());
                taskLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                taskLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ScheduleDialog.showTaskDetails(CalendarDayBox.this, task);
                        e.consume();
                    }
                });

                taskListPanel.add(taskLabel);
            }

            JScrollPane scrollPane = new JScrollPane(taskListPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            add(scrollPane, BorderLayout.CENTER);

            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ScheduleDialog.showDayTasksSummary(CalendarDayBox.this, date, tasksOnDate);
                }
            });
        }
    }
}