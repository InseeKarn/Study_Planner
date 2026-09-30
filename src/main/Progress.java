package main;

import javax.swing.*;
import java.awt.*;

public class Progress extends JPanel {
    private final JLabel percentLabel;
    private final JLabel statusCountLabel;

    public Progress(Color cardBg, Color textDark, Color accentGreen, Font boldFont, Font plainFont) {
        setLayout(new GridLayout(3, 1, 5, 2));
        setPreferredSize(new Dimension(290, 130));
        setMaximumSize(new Dimension(290, 130));
        setBackground(cardBg);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 210)), "Weekly Progress", 0, 0, boldFont));

        JLabel progressTitle = new JLabel("Weekly Progress", SwingConstants.CENTER);
        progressTitle.setFont(new Font("Serif", Font.BOLD, 15));
        progressTitle.setForeground(textDark);

        percentLabel = new JLabel("0% Completed", SwingConstants.CENTER);
        percentLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        percentLabel.setForeground(accentGreen);

        statusCountLabel = new JLabel("Completed: 0 | Pending: 0", SwingConstants.CENTER);
        statusCountLabel.setFont(plainFont);
        statusCountLabel.setForeground(Color.GRAY);

        add(progressTitle);
        add(percentLabel);
        add(statusCountLabel);
    }

    public void updateProgress(int percent, int completed, int pending) {
        percentLabel.setText(percent + "% Completed");
        statusCountLabel.setText("Completed: " + completed + " | Pending: " + pending);
    }
}