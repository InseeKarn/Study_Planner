package main;

import javax.swing.*;
import java.awt.*;

public class Detail extends JPanel {
    private final JLabel detailSubjectLabel;
    private final JLabel detailTitleLabel;
    private final JLabel detailDueLabel;
    private final JLabel detailDescLabel;
    private final JLabel detailStatusLabel;
    
    //Font
    private Font SansSerif = new Font("SansSerif", Font.BOLD, 12);

    public Detail(Color cardBg) {
        setLayout(new GridLayout(5, 1, 2, 4));
        setPreferredSize(new Dimension(290, 160));
        setMaximumSize(new Dimension(290, 160));
        setBackground(cardBg);
        
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 210)),
                "Details",
                0,
                0,
                SansSerif
        ));

        detailSubjectLabel = new JLabel("Subject: -");
        detailTitleLabel = new JLabel("Assignment: -");
        detailDueLabel = new JLabel("Due Date: -");
        detailDescLabel = new JLabel("Details: -");
        detailStatusLabel = new JLabel("Status: -");

        detailSubjectLabel.setFont(SansSerif);
        detailTitleLabel.setFont(SansSerif);
        detailDueLabel.setFont(SansSerif);
        detailDescLabel.setFont(SansSerif);
        detailStatusLabel.setFont(SansSerif);

        add(detailSubjectLabel);
        add(detailTitleLabel);
        add(detailDueLabel);
        add(detailDescLabel);
        add(detailStatusLabel);
    }

    public void updateDetails(Task task) {
        if (task == null) {
            detailSubjectLabel.setText("Subject: -");
            detailTitleLabel.setText("Assignment: -");
            detailDueLabel.setText("Due Date: -");
            detailDescLabel.setText("Details: -");
            detailStatusLabel.setText("Status: -");
            return;
        }
        detailSubjectLabel.setText("Subject: " + task.getSubject());
        detailTitleLabel.setText("Assignment: " + task.getTitle());
        detailDueLabel.setText("Due Date: " + task.getDueDate());
        detailDescLabel.setText("Details: " + task.getDescription());
        detailStatusLabel.setText("Status: " + task.getStatus());
    }
}