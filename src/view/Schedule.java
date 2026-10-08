package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.Task;
import service.Data;

public class Schedule extends JPanel {

    private final Data dataRepository;
    private YearMonth currentYearMonth;
    private int currentYear;

    private String currentViewMode = "MONTH";

    private final JComboBox<String> viewModeComboBox;
    private final JLabel titleLabel;
    private final JPanel mainContentPanel;

    // โทนสี UI
    private final Color bgDark = new Color(17, 17, 17);
    private final Color cardDark = new Color(24, 24, 24);
    private final Color dayBoxBg = new Color(32, 32, 32);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);
    private final Color accentGreen = new Color(143, 214, 148);
    private final Color borderDark = new Color(45, 45, 45);

    private final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public Schedule(Data dataRepository) {
        this.dataRepository = dataRepository;
        this.currentYearMonth = YearMonth.now();
        this.currentYear = LocalDate.now().getYear();

        // ดักฟังเหตุการณ์การเปลี่ยนแปลงข้อมูลจาก Data เพื่ออัปเดตปฏิทินแบบ Real-time
        if (this.dataRepository != null) {
            this.dataRepository.addPropertyChangeListener(evt -> renderView());
        }

        setLayout(new BorderLayout(0, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. แถบควบคุมด้านบน (Header Panel) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        String[] viewOptions = {"Month", "Year"};
        viewModeComboBox = new JComboBox<>(viewOptions);
        styleComboBox(viewModeComboBox);

        viewModeComboBox.addActionListener(e -> {
            int selectedIndex = viewModeComboBox.getSelectedIndex();
            if (selectedIndex == 0) {
                currentViewMode = "MONTH";
            } else {
                currentViewMode = "YEAR";
            }
            renderView();
        });

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(textPrimary);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        navPanel.setOpaque(false);

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        styleNavButton(btnPrev);
        styleNavButton(btnNext);

        btnPrev.addActionListener(e -> {
            if ("MONTH".equals(currentViewMode)) {
                currentYearMonth = currentYearMonth.minusMonths(1);
            } else {
                currentYear--;
            }
            renderView();
        });

        btnNext.addActionListener(e -> {
            if ("MONTH".equals(currentViewMode)) {
                currentYearMonth = currentYearMonth.plusMonths(1);
            } else {
                currentYear++;
            }
            renderView();
        });

        navPanel.add(btnPrev);
        navPanel.add(btnNext);

        headerPanel.add(viewModeComboBox, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(navPanel, BorderLayout.EAST);

        // --- 2. ส่วนแสดงเนื้อหาตาราง ---
        mainContentPanel = new JPanel(new BorderLayout(0, 10));
        mainContentPanel.setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);

        renderView();
    }

    public void renderView() {
        mainContentPanel.removeAll();

        if ("MONTH".equals(currentViewMode)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            titleLabel.setText(currentYearMonth.format(formatter).toUpperCase());
            mainContentPanel.add(renderMonthView(), BorderLayout.CENTER);
        } else {
            titleLabel.setText("YEAR " + currentYear);
            mainContentPanel.add(renderYearView(), BorderLayout.CENTER);
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // --- วาดมุมมองรายเดือน (Month View) ---
    private JPanel renderMonthView() {
        JPanel monthContainer = new JPanel(new BorderLayout(0, 10));
        monthContainer.setOpaque(false);

        JPanel weekHeaderPanel = new JPanel(new GridLayout(1, 7, 5, 0));
        weekHeaderPanel.setOpaque(false);
        String[] daysOfWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

        for (String day : daysOfWeek) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            lbl.setForeground(textSecondary); // ปรับทุกวันเป็นสีเทาตามปกติ[cite: 8]
            weekHeaderPanel.add(lbl);
        }

        JPanel daysGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        daysGridPanel.setOpaque(false);

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();
        int startOffset = dayOfWeekValue % 7;

        int lengthOfMonth = currentYearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < startOffset; i++) {
            JPanel emptyBox = new JPanel();
            emptyBox.setOpaque(false);
            daysGridPanel.add(emptyBox);
        }

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            JPanel dayBox = createDayBox(date, today);
            daysGridPanel.add(dayBox);
        }

        monthContainer.add(weekHeaderPanel, BorderLayout.NORTH);
        monthContainer.add(daysGridPanel, BorderLayout.CENTER);

        return monthContainer;
    }

    // --- วาดมุมมองรายปี (Year View) ---
    private JPanel renderYearView() {
        JPanel yearGridPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        yearGridPanel.setOpaque(false);

        int currentMonthNow = LocalDate.now().getMonthValue();
        int currentYearNow = LocalDate.now().getYear();

        for (int m = 1; m <= 12; m++) {
            JPanel monthCard = new JPanel(new BorderLayout(5, 5));
            monthCard.setBackground(dayBoxBg);
            monthCard.setBorder(BorderFactory.createLineBorder(borderDark, 1));

            if (m == currentMonthNow && currentYear == currentYearNow) {
                monthCard.setBorder(BorderFactory.createLineBorder(accentGreen, 2));
            }

            JLabel monthNameLabel = new JLabel(MONTH_NAMES[m - 1], SwingConstants.CENTER);
            monthNameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            monthNameLabel.setForeground(textPrimary);
            monthCard.add(monthNameLabel, BorderLayout.NORTH);

            int taskCount = getTaskCountForMonth(currentYear, m);
            JLabel countLabel = new JLabel(taskCount + " Tasks", SwingConstants.CENTER);
            countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            countLabel.setForeground(taskCount > 0 ? accentGreen : textSecondary);

            monthCard.add(countLabel, BorderLayout.CENTER);

            final int selectedMonth = m;
            monthCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
            monthCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    currentYearMonth = YearMonth.of(currentYear, selectedMonth);
                    currentViewMode = "MONTH";
                    viewModeComboBox.setSelectedIndex(0);
                    renderView();
                }
            });

            yearGridPanel.add(monthCard);
        }

        return yearGridPanel;
    }

    // --- สร้างกล่องแสดงวันในปฏิทิน ---
    private JPanel createDayBox(LocalDate date, LocalDate today) {
        JPanel box = new JPanel(new BorderLayout(3, 3));
        box.setBackground(dayBoxBg);
        box.setBorder(BorderFactory.createLineBorder(borderDark, 1));

        boolean isToday = date.equals(today);
        if (isToday) {
            box.setBorder(BorderFactory.createLineBorder(accentGreen, 2));
        }

        JLabel dayNumLabel = new JLabel(" " + date.getDayOfMonth());
        dayNumLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        dayNumLabel.setForeground(isToday ? accentGreen : textPrimary);

        box.add(dayNumLabel, BorderLayout.NORTH);

        List<Task> tasksOnDate = getTasksForDate(date);

        if (!tasksOnDate.isEmpty()) {
            JPanel taskListPanel = new JPanel();
            taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
            taskListPanel.setOpaque(false);

            for (Task task : tasksOnDate) {
                JLabel taskLabel = new JLabel("• " + task.getTitle());
                taskLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                taskLabel.setForeground(task.getStatusColor());
                taskLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // กดที่ชื่อรายการงานเพื่อแสดงรายละเอียด
                taskLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showTaskDetailsDialog(task);
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

            box.add(scrollPane, BorderLayout.CENTER);

            // กดที่กล่องวันเพื่อดูสรุปงานทั้งหมดของวันนั้น
            box.setCursor(new Cursor(Cursor.HAND_CURSOR));
            box.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showDayTasksSummaryDialog(date, tasksOnDate);
                }
            });
        }

        return box;
    }

    // --- หน้าต่างแสดงรายละเอียดงาน (ปรับสีให้อ่านได้ชัดเจน) ---
    private void showTaskDetailsDialog(Task task) {
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
            this,
            details,
            "Task Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // --- แสดงหน้าต่างรายละเอียดงานของวันนั้นๆ (เอา Dropdown ป๊อบอัปออกแล้ว) ---
    private void showDayTasksSummaryDialog(LocalDate date, List<Task> tasks) {
        if (tasks.isEmpty()) return;

        // หากมีงานเดียว เปิดดูรายละเอียดงานนั้นทันที[cite: 8]
        if (tasks.size() == 1) {
            showTaskDetailsDialog(tasks.get(0));
            return;
        }

        // หากมีหลายงาน สร้างสรุปรายละเอียดทุกงานในวันนั้นรวมไว้ในหน้าเดียว
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
            this,
            sb.toString(),
            "Daily Tasks Overview",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private List<Task> getTasksForDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        if (dataRepository == null) return result;

        for (Task t : dataRepository.getAllTasks()) {
            LocalDate due = Task.parseDueDate(t.getDueDate());
            if (due != null && due.equals(date)) {
                result.add(t);
            }
        }
        return result;
    }

    private int getTaskCountForMonth(int year, int month) {
        if (dataRepository == null) return 0;
        int count = 0;
        for (Task t : dataRepository.getAllTasks()) {
            LocalDate due = Task.parseDueDate(t.getDueDate());
            if (due != null && due.getYear() == year && due.getMonthValue() == month) {
                count++;
            }
        }
        return count;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setForeground(textPrimary);
        combo.setBackground(cardDark);
        combo.setPreferredSize(new Dimension(130, 32));
        combo.setFocusable(false);
    }

    private void styleNavButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(textPrimary);
        btn.setBackground(cardDark);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(borderDark, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(38, 32));
    }
}