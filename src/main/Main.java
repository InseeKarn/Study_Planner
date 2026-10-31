package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import service.Data;
import service.NotificationService;
import view.schedule.Schedule;
import view.task.Header;
import view.task.Tasks;

public class Main extends JFrame {

    private CardLayout pageCardLayout;
    private JPanel contentCardPanel;
    private final Data dataRepository = new Data();

    public Main() {
        // Start Notification Service
        NotificationService.start(dataRepository);

        // แสดง Floating Notification Carousel ที่มุมขวาล่างของ Desktop ทันทีที่เปิดโปรแกรม
        NotificationService.showDesktopNotification(dataRepository);

        setTitle("Study Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // ปิด Background Thread เมื่อปิดหน้าต่าง
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                NotificationService.stop();
            }
        });

        Color bgDark = new Color(17, 17, 17);
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(bgDark);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        pageCardLayout = new CardLayout();
        contentCardPanel = new JPanel(pageCardLayout);
        contentCardPanel.setOpaque(false);

        Header headerPanel = new Header(pageCardLayout, contentCardPanel);
        Schedule schedule = new Schedule(dataRepository);
        JPanel tasksPage = new Tasks(dataRepository);

        contentCardPanel.add(schedule, "SCHEDULE");
        contentCardPanel.add(tasksPage, "TASKS");
        pageCardLayout.show(contentCardPanel, "TASKS");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentCardPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}