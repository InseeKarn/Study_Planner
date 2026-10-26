package view.schedule;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.function.Consumer;
import javax.swing.*;
import service.Data;

public class YearViewPanel extends JPanel {

    private final Color dayBoxBg = new Color(32, 32, 32);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);
    private final Color accentGreen = new Color(143, 214, 148);
    private final Color borderDark = new Color(45, 45, 45);

    private final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public YearViewPanel(int year, Data dataRepository, Consumer<Integer> onMonthSelected) {
        setLayout(new GridLayout(3, 4, 10, 10));
        setOpaque(false);

        int currentMonthNow = LocalDate.now().getMonthValue();
        int currentYearNow = LocalDate.now().getYear();

        for (int m = 1; m <= 12; m++) {
            JPanel monthCard = new JPanel(new BorderLayout(5, 5));
            monthCard.setBackground(dayBoxBg);
            monthCard.setBorder(BorderFactory.createLineBorder(borderDark, 1));

            if (m == currentMonthNow && year == currentYearNow) {
                monthCard.setBorder(BorderFactory.createLineBorder(accentGreen, 2));
            }

            JLabel monthNameLabel = new JLabel(MONTH_NAMES[m - 1], SwingConstants.CENTER);
            monthNameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            monthNameLabel.setForeground(textPrimary);
            monthCard.add(monthNameLabel, BorderLayout.NORTH);

            int taskCount = dataRepository != null ? dataRepository.getTaskCountForMonth(year, m) : 0;
            JLabel countLabel = new JLabel(taskCount + " Tasks", SwingConstants.CENTER);
            countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            countLabel.setForeground(taskCount > 0 ? accentGreen : textSecondary);

            monthCard.add(countLabel, BorderLayout.CENTER);

            final int selectedMonth = m;
            monthCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
            monthCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onMonthSelected != null) {
                        onMonthSelected.accept(selectedMonth);
                    }
                }
            });

            add(monthCard);
        }
    }
}