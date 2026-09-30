package main;

import javax.swing.*;
import java.awt.*;

public class Detail extends JPanel {
    private final JLabel detailSubjectLabel;
    private final JLabel detailTitleLabel;
    private final JLabel detailDueLabel;
    private final JLabel detailDescLabel;
    private final JLabel detailStatusLabel;

    public Detail(Color cardBg, Font boldFont, Font plainFont) {
        setLayout(new GridLayout(5, 1, 2, 4));
        setPreferredSize(new Dimension(290, 160));
        setMaximumSize(new Dimension(290, 160));
        setBackground(cardBg);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 210)), "Details", 0, 0, boldFont));

        detailSubjectLabel = new JLabel("วิชา: -");
        detailTitleLabel = new JLabel("งาน: -");
        detailDueLabel = new JLabel("กำหนดส่ง: -");
        detailDescLabel = new JLabel("รายละเอียด: -");
        detailStatusLabel = new JLabel("สถานะ: -");

        detailSubjectLabel.setFont(plainFont);
        detailTitleLabel.setFont(plainFont);
        detailDueLabel.setFont(plainFont);
        detailDescLabel.setFont(plainFont);
        detailStatusLabel.setFont(plainFont);

        add(detailSubjectLabel);
        add(detailTitleLabel);
        add(detailDueLabel);
        add(detailDescLabel);
        add(detailStatusLabel);
    }

    public void updateDetails(Task task) {
        if (task == null) {
            detailSubjectLabel.setText("วิชา: -");
            detailTitleLabel.setText("งาน: -");
            detailDueLabel.setText("กำหนดส่ง: -");
            detailDescLabel.setText("รายละเอียด: -");
            detailStatusLabel.setText("สถานะ: -");
            return;
        }
        detailSubjectLabel.setText("วิชา: " + task.getSubject());
        detailTitleLabel.setText("งาน: " + task.getTitle());
        detailDueLabel.setText("กำหนดส่ง: " + task.getDueDate());
        detailDescLabel.setText("รายละเอียด: " + task.getDescription());
        detailStatusLabel.setText("สถานะ: " + task.getStatus());
    }
}