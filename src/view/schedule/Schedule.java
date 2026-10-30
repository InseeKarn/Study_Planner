package view.schedule;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import service.Data;

public class Schedule extends JPanel {

    private final Data dataRepository;
    private YearMonth currentYearMonth;
    private int currentYear;
    private String currentViewMode = "MONTH";

    private final ScheduleHeader headerPanel;
    private final JPanel mainContentPanel;

    private final Color bgDark = new Color(17, 17, 17);

    public Schedule(Data dataRepository) {
        this.dataRepository = dataRepository;
        this.currentYearMonth = YearMonth.now();
        this.currentYear = LocalDate.now().getYear();

        if (this.dataRepository != null) {
            this.dataRepository.addPropertyChangeListener(evt -> renderView());
        }

        setLayout(new BorderLayout(0, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Header
        headerPanel = new ScheduleHeader();

        headerPanel.addViewModeListener(e -> {
            JComboBox<?> combo = (JComboBox<?>) e.getSource();
            currentViewMode = combo.getSelectedIndex() == 0 ? "MONTH" : "YEAR";
            renderView();
        });

        headerPanel.addPrevListener(e -> {
            if ("MONTH".equals(currentViewMode)) {
                currentYearMonth = currentYearMonth.minusMonths(1);
            } else {
                currentYear--;
            }
            renderView();
        });

        headerPanel.addNextListener(e -> {
            if ("MONTH".equals(currentViewMode)) {
                currentYearMonth = currentYearMonth.plusMonths(1);
            } else {
                currentYear++;
            }
            renderView();
        });

        // 2. Main Content
        mainContentPanel = new JPanel(new BorderLayout(0, 10));
        mainContentPanel.setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);

        renderView();
    }

    public void renderView() {
        mainContentPanel.removeAll();

        if ("MONTH".equals(currentViewMode)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
            headerPanel.setTitle(currentYearMonth.format(formatter).toUpperCase());
            mainContentPanel.add(new MonthViewPanel(currentYearMonth, dataRepository), BorderLayout.CENTER);
        } else {
            headerPanel.setTitle("YEAR " + currentYear);
            mainContentPanel.add(
                new YearViewPanel(currentYear, dataRepository, selectedMonth -> {
                    currentYearMonth = YearMonth.of(currentYear, selectedMonth);
                    currentViewMode = "MONTH";
                    headerPanel.setViewModeIndex(0);
                    renderView();
                }),
                BorderLayout.CENTER
            );
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}