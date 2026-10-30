package view.task;

import java.awt.*;
import javax.swing.*;
import model.Task;

public class Detail extends JPanel {
    private final JLabel detailSubjectLabel;
    private final JLabel detailTitleLabel;
    private final JLabel detailDueLabel;
    private final JLabel detailDescLabel;
    private final JLabel detailStatusLabel;

    private final Font font = new Font("SansSerif", Font.PLAIN, 12);
    private final Color textPrimary = new Color(242, 242, 242);

    public Detail(Color cardBg) {
        setLayout(new GridLayout(5, 1, 0, 8));
        setPreferredSize(new Dimension(290, 160));
        setMaximumSize(new Dimension(290, 160));
        setBackground(cardBg);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                "Details",
                0, 0,
                new Font("SansSerif", Font.BOLD, 14),
                textPrimary
            ),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        detailSubjectLabel = new JLabel("Subject: -");
        detailTitleLabel = new JLabel("Assignment: -");
        detailDueLabel = new JLabel("Due Date: -");
        detailDescLabel = new JLabel("Details: -");
        detailStatusLabel = new JLabel("Status: -");

        JLabel[] labels = {detailSubjectLabel, detailTitleLabel, detailDueLabel, detailDescLabel, detailStatusLabel};
        for (JLabel lbl : labels) {
            lbl.setFont(font);
            lbl.setForeground(textPrimary);
            add(lbl);
        }
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
        detailSubjectLabel.setText("Subject: " + (task.getSubject() != null ? task.getSubject() : "-"));
        detailTitleLabel.setText("Assignment: " + (task.getTitle() != null ? task.getTitle() : "-"));
        detailDueLabel.setText("Due Date: " + (task.getDueDate() != null ? task.getDueDate() : "-"));
        detailDescLabel.setText("Details: " + (task.getDescription() != null && !task.getDescription().isEmpty() ? task.getDescription() : "-"));
        detailStatusLabel.setText("Status: " + (task.getStatus() != null ? task.getStatus() : "-"));
    }
}