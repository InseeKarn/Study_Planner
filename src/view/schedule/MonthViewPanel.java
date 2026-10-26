package view.schedule;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.swing.*;
import service.Data;

public class MonthViewPanel extends JPanel {

    private final Color textSecondary = new Color(153, 153, 153);

    public MonthViewPanel(YearMonth yearMonth, Data dataRepository) {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        JPanel weekHeaderPanel = new JPanel(new GridLayout(1, 7, 5, 0));
        weekHeaderPanel.setOpaque(false);
        String[] daysOfWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

        for (String day : daysOfWeek) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            lbl.setForeground(textSecondary);
            weekHeaderPanel.add(lbl);
        }

        JPanel daysGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        daysGridPanel.setOpaque(false);

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();
        int startOffset = dayOfWeekValue % 7;

        int lengthOfMonth = yearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < startOffset; i++) {
            JPanel emptyBox = new JPanel();
            emptyBox.setOpaque(false);
            daysGridPanel.add(emptyBox);
        }

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            CalendarDayBox dayBox = new CalendarDayBox(
                date,
                today,
                dataRepository != null ? dataRepository.getTasksForDate(date) : null
            );
            daysGridPanel.add(dayBox);
        }

        add(weekHeaderPanel, BorderLayout.NORTH);
        add(daysGridPanel, BorderLayout.CENTER);
    }
}