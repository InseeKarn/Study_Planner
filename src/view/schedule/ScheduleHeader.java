package view.schedule;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ScheduleHeader extends JPanel {

    private final JComboBox<String> viewModeComboBox;
    private final JLabel titleLabel;
    private final JButton btnPrev;
    private final JButton btnNext;

    private final Color cardDark = new Color(24, 24, 24);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color borderDark = new Color(45, 45, 45);

    public ScheduleHeader() {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] viewOptions = {"Month", "Year"};
        viewModeComboBox = new JComboBox<>(viewOptions);
        styleComboBox(viewModeComboBox);

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(textPrimary);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        navPanel.setOpaque(false);

        btnPrev = new JButton("<");
        btnNext = new JButton(">");
        styleNavButton(btnPrev);
        styleNavButton(btnNext);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);

        add(viewModeComboBox, BorderLayout.WEST);
        add(titleLabel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.EAST);
    }

    public void setTitle(String text) {
        titleLabel.setText(text);
    }

    public void setViewModeIndex(int index) {
        viewModeComboBox.setSelectedIndex(index);
    }

    public void addViewModeListener(ActionListener listener) {
        viewModeComboBox.addActionListener(listener);
    }

    public void addPrevListener(ActionListener listener) {
        btnPrev.addActionListener(listener);
    }

    public void addNextListener(ActionListener listener) {
        btnNext.addActionListener(listener);
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