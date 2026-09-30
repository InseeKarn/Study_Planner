package main;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main extends JFrame {

    private final Data dataRepository = new Data();

    // Fonts
    private final Font fontThaiBold = new Font("Leelawadee UI", Font.BOLD, 12);
    private final Font fontThaiPlain = new Font("Leelawadee UI", Font.PLAIN, 12);

    // Components
    private CardLayout pageCardLayout;
    private JPanel contentCardPanel;

    private Task_Table taskTablePanel;
    private Detail detailCardPanel;
    private Progress progressCardPanel;

    public Main() {
        setTitle("Study Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // Colors
        Color bgCream = new Color(245, 240, 230);
        Color cardBg = new Color(252, 250, 245);
        Color textDark = new Color(60, 50, 40);
        Color accentNavy = new Color(44, 76, 89);
        Color accentGreen = new Color(85, 110, 90);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(bgCream);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Header
        JPanel headerPanel = createHeaderPanel(textDark, accentNavy);

        // Content (CardLayout)
        pageCardLayout = new CardLayout();
        contentCardPanel = new JPanel(pageCardLayout);
        contentCardPanel.setOpaque(false);

        JPanel schedulePage = createSchedulePage(cardBg);
        JPanel tasksPage = createTasksPage(cardBg, textDark, accentGreen);

        contentCardPanel.add(schedulePage, "SCHEDULE");
        contentCardPanel.add(tasksPage, "TASKS");
        pageCardLayout.show(contentCardPanel, "TASKS");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentCardPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Initial Refresh
        refreshAllData();
    }

    private JPanel createHeaderPanel(Color textDark, Color accentNavy) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Study Planner");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(textDark);

        // Local date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH);
        String formattedDate = currentDate.format(formatter).toUpperCase();

        JLabel dateLabel = new JLabel(formattedDate);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        dateLabel.setForeground(new Color(130, 120, 110));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(dateLabel);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        navPanel.setOpaque(false);

        JButton navSchedule = new JButton("SCHEDULE");
        JButton navTasks = new JButton("SUBJECTS & TASKS");

        styleNavButton(navSchedule, new Color(150, 140, 130));
        styleNavButton(navTasks, accentNavy);

        navSchedule.addActionListener(e -> {
            pageCardLayout.show(contentCardPanel, "SCHEDULE");
            navSchedule.setForeground(accentNavy);
            navTasks.setForeground(new Color(150, 140, 130));
        });

        navTasks.addActionListener(e -> {
            pageCardLayout.show(contentCardPanel, "TASKS");
            navTasks.setForeground(accentNavy);
            navSchedule.setForeground(new Color(150, 140, 130));
        });

        navPanel.add(navSchedule);
        navPanel.add(navTasks);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSchedulePage(Color cardBg) {
        JPanel schedulePage = new JPanel(new GridBagLayout());
        schedulePage.setBackground(cardBg);
        JLabel scheduleLabel = new JLabel("--- หน้า Schedule (ปฏิทิน) ---");
        scheduleLabel.setFont(new Font("Leelawadee UI", Font.BOLD, 18));
        scheduleLabel.setForeground(Color.GRAY);
        schedulePage.add(scheduleLabel);
        return schedulePage;
    }

    private JPanel createTasksPage(Color cardBg, Color textDark, Color accentGreen) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        //Task_Table
        taskTablePanel = new Task_Table(cardBg, fontThaiBold, fontThaiPlain);
        taskTablePanel.setTaskTableListener(new Task_Table.TaskTableListener() {
            @Override
            public void onSubmitClicked(Task task) {
                task.toggleStatus();
                refreshAllData();
                detailCardPanel.updateDetails(task);
            }

            @Override
            public void onDetailsClicked(Task task) {
                detailCardPanel.updateDetails(task);
            }
        });

        // Progress, Details, +
        JPanel rightSidePanel = new JPanel(new BorderLayout(0, 10));
        rightSidePanel.setOpaque(false);
        rightSidePanel.setPreferredSize(new Dimension(300, 0));

        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        detailCardPanel = new Detail(cardBg, fontThaiBold, fontThaiPlain);
        progressCardPanel = new Progress(cardBg, textDark, accentGreen, fontThaiBold, fontThaiPlain);

        // Details & Progress position
        cardsContainer.add(progressCardPanel);
        cardsContainer.add(Box.createVerticalStrut(10));
        cardsContainer.add(detailCardPanel);
        // ----------------------------------------------------

        // ปุ่ม (+) มุมขวาล่าง
        JButton addButton = new JButton("+");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        addButton.setPreferredSize(new Dimension(50, 50));
        addButton.setBackground(accentGreen);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddTaskDialog());

        JPanel buttonBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonBox.setOpaque(false);
        buttonBox.add(addButton);

        rightSidePanel.add(cardsContainer, BorderLayout.NORTH);
        rightSidePanel.add(buttonBox, BorderLayout.SOUTH);

        panel.add(taskTablePanel, BorderLayout.CENTER);
        panel.add(rightSidePanel, BorderLayout.EAST);

        return panel;
    }

    private void refreshAllData() {
        taskTablePanel.updateTable(dataRepository.getAllTasks());
        progressCardPanel.updateProgress(
                dataRepository.getProgressPercentage(),
                dataRepository.getCompletedCount(),
                dataRepository.getPendingCount()
        );
    }

    private void showAddTaskDialog() {
        JTextField txtSubject = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtDue = new JTextField();
        JTextField txtDesc = new JTextField();

        Object[] message = {
            "ชื่อวิชา:", txtSubject,
            "ชื่องาน/การบ้าน:", txtTitle,
            "วันกำหนดส่ง (เช่น 15/10):", txtDue,
            "คำอธิบายงานเพิ่มเติม:", txtDesc
        };

        int option = JOptionPane.showConfirmDialog(this, message, "เพิ่มรายการงานใหม่", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sub = txtSubject.getText().trim();
            String title = txtTitle.getText().trim();
            String due = txtDue.getText().trim();
            String desc = txtDesc.getText().trim();

            if (!sub.isEmpty() && !title.isEmpty()) {
                Task newTask = new Task(sub, title, due.isEmpty() ? "ไม่ระบุ" : due, "In Progress", desc.isEmpty() ? "-" : desc);
                dataRepository.addTask(newTask);
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อวิชาและชื่องาน", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void styleNavButton(JButton btn, Color color) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}