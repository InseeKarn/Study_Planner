package view;

import java.awt.*;
import javax.swing.*;
import model.Task;
import service.Data;

public class Tasks extends JPanel {
    private final Data dataRepository;

    private Task_Table taskTablePanel;
    private Detail detailCardPanel;
    private Progress progressCardPanel;
    
    // Color
    private final Color bgDark = new Color(17, 17, 17);
    private final Color cardDark = new Color(24, 24, 24);
    private final Color elementDark = new Color(32, 32, 32);
    private final Color borderDark = new Color(42, 42, 42);

    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);

    private final Color accentGreen = new Color(143, 214, 148);
        
    public Tasks(Data dataRepository) {
        this.dataRepository = dataRepository;

        setLayout(new BorderLayout(15, 0));
        setBackground(bgDark);
        setOpaque(true);
        
        // Task Table
        taskTablePanel = new Task_Table(cardDark);
        
        taskTablePanel.setTaskTableListener(
            new Task_Table.TaskTableListener() {

                @Override
                public void onSubmitClicked(Task task) {
                    task.toggleStatus();
                    dataRepository.saveToJson();
                    refreshAllData();
                    detailCardPanel.updateDetails(task);
                }

                @Override
                public void onDeleteClicked(Task task) {
                    int option = JOptionPane.showConfirmDialog(
                        Tasks.this,
                        "Are you sure you want to delete this assignment?",
                        "Delete Assignment",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        dataRepository.deleteTask(task);
                        refreshAllData();
                        detailCardPanel.updateDetails(null);
                    }
                }

                @Override
                public void onTaskSelected(Task task) {
                    detailCardPanel.updateDetails(task);
                }
            }
        );
        
        // Right Side / Details & Progress position
        JPanel rightSidePanel = new JPanel(new BorderLayout(0, 10));
        rightSidePanel.setOpaque(false);
        rightSidePanel.setPreferredSize(new Dimension(300, 0));

        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);
        
        // Details & Progress position
        detailCardPanel = new Detail(cardDark);
        progressCardPanel = new Progress(cardDark, textPrimary, accentGreen);

        cardsContainer.add(progressCardPanel);
        cardsContainer.add(Box.createVerticalStrut(10));
        cardsContainer.add(detailCardPanel);
        
        // Add button
        JButton addButton = new JButton("+");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        addButton.setPreferredSize(new Dimension(50, 50));
        addButton.setBackground(accentGreen);
        addButton.setForeground(new Color(17, 17, 17));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addButton.addActionListener(e -> showAddTaskDialog());

        JPanel buttonBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonBox.setOpaque(false);
        buttonBox.add(addButton);

        rightSidePanel.add(cardsContainer, BorderLayout.NORTH);
        rightSidePanel.add(buttonBox, BorderLayout.SOUTH);
        
        // Add to Tasks panel
        add(taskTablePanel, BorderLayout.CENTER);
        add(rightSidePanel, BorderLayout.EAST);

        // Initial data
        refreshAllData();
    }
        
    private void refreshAllData() {
        taskTablePanel.updateTable(dataRepository.getAllTasks());
        progressCardPanel.updateProgress(
            dataRepository.getProgressPercentage(),
            dataRepository.getCompletedCount(),
            dataRepository.getPendingCount()
        );
    }
    
    private void showAddTaskDialog() {
        JTextField txtSubject = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtDue = new JTextField();
        JTextField txtDesc = new JTextField();

        Object[] message = {
            "Subject:", txtSubject,
            "Work:", txtTitle,
            "Due date (DD-MM-YYYY):", txtDue, // เปลี่ยนตรงนี้
            "Description:", txtDesc
        };

        int option = JOptionPane.showConfirmDialog(
            this, 
            message, 
            "Add new work", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (option == JOptionPane.OK_OPTION) {
            String sub = txtSubject.getText().trim();
            String title = txtTitle.getText().trim();
            String due = txtDue.getText().trim();
            String desc = txtDesc.getText().trim();

            if (!sub.isEmpty() && !title.isEmpty()) {
                Task newTask = new Task(
                    sub,
                    title,
                    due.isEmpty() ? "Not specified" : due,
                    "In Progress",
                    desc.isEmpty() ? "-" : desc
                );
                
                dataRepository.addTask(newTask);
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter the subject and assignment name.",
                    "Notification",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
}