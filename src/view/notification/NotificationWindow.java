package view.notification;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class NotificationWindow extends JWindow {

    private static NotificationWindow currentInstance = null;

    public static synchronized void showNotification(JPanel notificationContentPanel) {
        if (currentInstance != null) {
            currentInstance.dispose();
        }
        currentInstance = new NotificationWindow(notificationContentPanel);
        currentInstance.setVisible(true);
    }

    private NotificationWindow(JPanel notificationContentPanel) {
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());

        JPanel outerContainer = new JPanel(new BorderLayout());
        outerContainer.setBackground(new Color(24, 24, 24));
        outerContainer.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

        // Top Bar with Close [X] Button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        topBar.setBackground(new Color(24, 24, 24));

        JLabel closeBtn = new JLabel("✕");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        closeBtn.setForeground(new Color(153, 153, 153));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setForeground(new Color(210, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setForeground(new Color(153, 153, 153));
            }
        });

        topBar.add(closeBtn);
        outerContainer.add(topBar, BorderLayout.NORTH);
        outerContainer.add(notificationContentPanel, BorderLayout.CENTER);

        add(outerContainer);
        pack();

        positionAtBottomRight();
    }

    private void positionAtBottomRight() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle screenBounds = defaultScreen.getDefaultConfiguration().getBounds();

        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());

        int x = screenBounds.width - screenInsets.right - getWidth() - 15;
        int y = screenBounds.height - screenInsets.bottom - getHeight() - 15;

        setLocation(x, y);
    }
}