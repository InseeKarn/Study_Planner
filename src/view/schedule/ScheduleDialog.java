package view.schedule;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.Task;

public class ScheduleDialog {

    private static final Color BG = new Color(17, 17, 17);
    private static final Color CARD = new Color(24, 24, 24);
    private static final Color FIELD = new Color(32, 32, 32);
    private static final Color BORDER = new Color(60, 60, 60);
    private static final Color TEXT = new Color(242, 242, 242);
    private static final Color MUTED = new Color(153, 153, 153);
    private static final Color GREEN = new Color(143, 214, 148);

    public static void showTaskDetails(Component parent, Task task) {
        if (task == null) return;

        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Task Details", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new LineBorder(BORDER, 1));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new EmptyBorder(16, 20, 16, 16));
        JPanel headingText = new JPanel();
        headingText.setOpaque(false);
        headingText.setLayout(new BoxLayout(headingText, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(valueOrDash(task.getTitle()));
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT);
        JLabel subject = new JLabel("Subject: " + valueOrDash(task.getSubject()));
        subject.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subject.setForeground(MUTED);
        headingText.add(title);
        headingText.add(Box.createVerticalStrut(4));
        headingText.add(subject);

        JButton close = new JButton("✕");
        close.setForeground(MUTED);
        close.setFont(new Font("SansSerif", Font.PLAIN, 16));
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.addActionListener(e -> dialog.dispose());
        header.add(headingText, BorderLayout.CENTER);
        header.add(close, BorderLayout.EAST);

        Point[] dragPoint = new Point[1];
        header.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragPoint[0] = e.getLocationOnScreen(); }
        });
        header.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                Point location = dialog.getLocation();
                dialog.setLocation(location.x + current.x - dragPoint[0].x,
                        location.y + current.y - dragPoint[0].y);
                dragPoint[0] = current;
            }
        });
        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(18, 20, 14, 20));
        addDetailRow(body, "DUE DATE", valueOrDash(task.getDueDate()));
        addDetailRow(body, "STATUS / PRIORITY", valueOrDash(task.getStatus()));
        JLabel descriptionLabel = new JLabel("DESCRIPTION");
        descriptionLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea description = new JTextArea(valueOrDash(task.getDescription()));
        description.setFont(new Font("SansSerif", Font.PLAIN, 12));
        description.setForeground(TEXT);
        description.setBackground(FIELD);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setBorder(new EmptyBorder(9, 11, 9, 11));
        JScrollPane descriptionScroll = new JScrollPane(description);
        descriptionScroll.setPreferredSize(new Dimension(350, 80));
        descriptionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descriptionScroll.setBorder(new LineBorder(BORDER, 1));
        descriptionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(descriptionLabel);
        body.add(Box.createVerticalStrut(7));
        body.add(descriptionScroll);
        root.add(body, BorderLayout.CENTER);

        JButton ok = new JButton("OK");
        ok.setFont(new Font("SansSerif", Font.BOLD, 11));
        ok.setForeground(new Color(17, 17, 17));
        ok.setBackground(GREEN);
        ok.setFocusPainted(false);
        ok.setBorderPainted(false);
        ok.setPreferredSize(new Dimension(88, 34));
        ok.addActionListener(e -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(0, 20, 16, 20));
        footer.add(ok);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(400, 280));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private static void addDetailRow(JPanel parent, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MUTED);
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("SansSerif", Font.PLAIN, 13));
        value.setForeground(TEXT);
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);
        parent.add(row);
        parent.add(Box.createVerticalStrut(11));
    }

    public static void showDayTasksSummary(Component parent, LocalDate date, List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return;

        if (tasks.size() == 1) {
            showTaskDetails(parent, tasks.get(0));
            return;
        }

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
            parent,
            sb.toString(),
            "Daily Tasks Overview",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
