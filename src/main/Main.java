package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import service.Data;
import service.NotificationService;
import view.schedule.Schedule;
import view.task.Header;
import view.task.Tasks;

public class Main extends JFrame {

    private final CardLayout pageCardLayout;
    private final JPanel contentCardPanel;
    private final Data dataRepository = new Data();
    private Point dragStart;

    public Main() {
        // Start Notification Service
        NotificationService.start(dataRepository);

        // แสดง Floating Notification Carousel ที่มุมขวาล่างของ Desktop ทันทีที่เปิดโปรแกรม
        NotificationService.showDesktopNotification(dataRepository);

        setUndecorated(true);
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

        Color titleBarColor = new Color(24, 24, 24);
        Color borderColor = new Color(50, 50, 50);
        JPanel windowBar = new JPanel(new BorderLayout());
        windowBar.setBackground(titleBarColor);
        windowBar.setPreferredSize(new Dimension(1100, 36));
        windowBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controls.setOpaque(false);
        JButton minimize = createWindowButton("—", titleBarColor);
        minimize.addActionListener(e -> setState(Frame.ICONIFIED));
        JButton close = createWindowButton("✕", titleBarColor);
        close.addActionListener(e -> dispatchEvent(
                new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING)));
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                close.setBackground(new Color(196, 43, 28));
                close.setForeground(Color.WHITE);
                close.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                close.setBackground(titleBarColor);
                close.setForeground(new Color(190, 190, 190));
                close.setContentAreaFilled(false);
            }
        });
        controls.add(minimize);
        controls.add(close);
        windowBar.add(controls, BorderLayout.EAST);
        windowBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getLocationOnScreen();
            }
        });
        windowBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                Point location = getLocation();
                setLocation(location.x + current.x - dragStart.x,
                        location.y + current.y - dragStart.y);
                dragStart = current;
            }
        });

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(bgDark);
        rootPanel.setBorder(BorderFactory.createLineBorder(borderColor));
        rootPanel.add(windowBar, BorderLayout.NORTH);

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

        rootPanel.add(mainPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);
    }

    private JButton createWindowButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setForeground(new Color(190, 190, 190));
        button.setPreferredSize(new Dimension(46, 35));
        button.setBackground(background);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
