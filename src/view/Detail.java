package view;

import javax.swing.*;

import model.Task;

import java.awt.*;

public class Detail extends JPanel {
    private final JLabel detailSubjectLabel;
    private final JLabel detailTitleLabel;
    private final JLabel detailDueLabel;
    private final JLabel detailDescLabel;
    private final JLabel detailStatusLabel;
    
    //Font
    private Font SansSerif = new Font("SansSerif", Font.BOLD, 12);

    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);
    
    
    public Detail(Color cardBg) {
        setLayout(new GridLayout(5, 1, 2, 4));
        setPreferredSize(new Dimension(290, 160));
        setMaximumSize(new Dimension(290, 160));
        setBackground(cardBg);
        
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Details",
                0,
                0,
                SansSerif,
                textPrimary
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
        
        detailSubjectLabel.setForeground(textSecondary);
        detailTitleLabel.setForeground(textSecondary);
        detailDueLabel.setForeground(textSecondary);
        detailDescLabel.setForeground(textSecondary);
        detailStatusLabel.setForeground(textSecondary);

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