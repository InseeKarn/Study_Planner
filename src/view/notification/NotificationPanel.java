package view.notification;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.Task;
import service.Data;

public class NotificationPanel extends JPanel {

    private final Data dataRepository;
    private final CardLayout cardLayout;
    private final JPanel cardsContainer;

    private final JButton btnPrev;
    private final JButton btnNext;
    private final JPanel dotsPanel;

    private int currentIndex = 0;
    private int totalPages = 1;

    private final Color cardBg = new Color(24, 24, 24);
    private final Color borderColor = new Color(45, 45, 45);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color dotInactive = new Color(70, 70, 70);
    private final Color dotActive = new Color(143, 214, 148);

    public NotificationPanel(Data dataRepository) {
        this.dataRepository = dataRepository;

        setPreferredSize(new Dimension(290, 175));
        setMaximumSize(new Dimension(290, 175));
        setLayout(new BorderLayout());
        setBackground(cardBg);
        
        // ขอบเส้นปกติ (ลบแถบสีเหลืองขอบซ้ายออกแล้ว)
        setBorder(BorderFactory.createLineBorder(borderColor, 1));

        // Center Content Cards
        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);
        cardsContainer.setOpaque(false);
        add(cardsContainer, BorderLayout.CENTER);

        // Navigation Bar (< [dots] >)
        JPanel bottomNav = new JPanel(new BorderLayout());
        bottomNav.setOpaque(false);
        bottomNav.setPreferredSize(new Dimension(290, 28));
        bottomNav.setBorder(BorderFactory.createEmptyBorder(0, 10, 4, 10));

        btnPrev = createNavButton("<");
        btnNext = createNavButton(">");

        dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 4));
        dotsPanel.setOpaque(false);

        btnPrev.addActionListener(e -> navigate(-1));
        btnNext.addActionListener(e -> navigate(1));

        bottomNav.add(btnPrev, BorderLayout.WEST);
        bottomNav.add(dotsPanel, BorderLayout.CENTER);
        bottomNav.add(btnNext, BorderLayout.EAST);

        add(bottomNav, BorderLayout.SOUTH);

        if (this.dataRepository != null) {
            this.dataRepository.addPropertyChangeListener(evt -> refreshCarousel());
        }

        refreshCarousel();
    }

    public void refreshCarousel() {
        cardsContainer.removeAll();
        dotsPanel.removeAll();
        currentIndex = 0;

        List<Task> allTasks = dataRepository != null ? dataRepository.getAllTasks() : new ArrayList<>();
        LocalDate today = LocalDate.now();

        int todayCount = 0;
        int overdueCount = 0;
        int inProgressCount = 0;
        int totalPendingCount = 0;

        Map<Task, String> taskPageMap = new LinkedHashMap<>();

        for (Task t : allTasks) {
            if (t.isCompleted()) continue;

            // งานที่ยังทำไม่เสร็จทั้งหมด
            totalPendingCount++;

            LocalDate due = Task.parseDueDate(t.getDueDate());

            if (due != null && due.equals(today)) {
                todayCount++;
                taskPageMap.putIfAbsent(t, "TODAY");
            }
            if (due != null && due.isBefore(today)) {
                overdueCount++;
                taskPageMap.putIfAbsent(t, "OVERDUE");
            }
            if (!t.isCompleted()) {
                inProgressCount++;
                taskPageMap.putIfAbsent(t, "IN PROGRESS");
            }
        }

        TaskSummary summary = new TaskSummary(todayCount, overdueCount, inProgressCount, totalPendingCount);

        // Page 1: Summary Overview
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        cardsContainer.add(new SummaryPagePanel(today.format(dtf), summary), "page_0");

        // Page 2+: Task Details ตามลำดับความสำคัญ
        int pageIndex = 1;
        for (Map.Entry<Task, String> entry : taskPageMap.entrySet()) {
            cardsContainer.add(new TaskDetailPagePanel(entry.getKey(), entry.getValue()), "page_" + pageIndex);
            pageIndex++;
        }

        totalPages = pageIndex;
        renderDots();
        updateNavButtons();

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private void renderDots() {
        dotsPanel.removeAll();
        for (int i = 0; i < totalPages; i++) {
            JLabel dot = new JLabel("•");
            dot.setFont(new Font("SansSerif", Font.BOLD, 16));
            dot.setForeground(i == currentIndex ? dotActive : dotInactive);
            dotsPanel.add(dot);
        }
        dotsPanel.revalidate();
        dotsPanel.repaint();
    }

    private void navigate(int direction) {
        int newIndex = currentIndex + direction;
        if (newIndex >= 0 && newIndex < totalPages) {
            currentIndex = newIndex;
            cardLayout.show(cardsContainer, "page_" + currentIndex);
            renderDots();
            updateNavButtons();
        }
    }

    private void updateNavButtons() {
        btnPrev.setEnabled(currentIndex > 0);
        btnNext.setEnabled(currentIndex < totalPages - 1);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(textPrimary);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(28, 20));
        return btn;
    }
}