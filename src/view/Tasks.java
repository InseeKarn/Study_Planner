package view;

import java.awt.*;
import javax.swing.*;

import model.Task;
import service.Data;

public class Tasks extends JPanel{
    private final Data dataRepository;

    private Task_Table taskTablePanel;
    private Detail detailCardPanel;
    private Progress progressCardPanel;
    
    //Color
    Color bgColor = new Color(252, 250, 245);
    Color textColor = new Color(60, 50, 40);
    Color accentGreen = new Color(85, 110, 90);
	
	public Tasks(
            Data dataRepository
            ) {
		
        this.dataRepository = dataRepository;

        
        setLayout(new BorderLayout(15, 0));
        setOpaque(false);
        
        // Task Table
        taskTablePanel = new Task_Table(bgColor);
        
        
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
                    public void onDetailsClicked(Task task) {
                        detailCardPanel.updateDetails(task);
                    }
                }
            );
        
        //Right Side / Details & Progress position
        JPanel rightSidePanel =
                new JPanel(new BorderLayout(0, 10));

        rightSidePanel.setOpaque(false);
        rightSidePanel.setPreferredSize(
                new Dimension(300, 0)
        );

        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(
                new BoxLayout(
                        cardsContainer,
                        BoxLayout.Y_AXIS
                )
        );
        cardsContainer.setOpaque(false);
        
        
        //Details & Progress position
        detailCardPanel = new Detail(
        		bgColor
        );

        progressCardPanel = new Progress(
        		bgColor,
                textColor,
                accentGreen
        );

        cardsContainer.add(progressCardPanel);
        cardsContainer.add(
                Box.createVerticalStrut(10)
        );
        cardsContainer.add(detailCardPanel);
        
        // Add button
        JButton addButton = new JButton("+");

        addButton.setFont(
                new Font("SansSerif", Font.BOLD, 22)
        );
        addButton.setPreferredSize(
                new Dimension(50, 50)
        );
        addButton.setBackground(accentGreen);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        addButton.addActionListener(
                e -> showAddTaskDialog()
        );

        JPanel buttonBox =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                0
                        )
                );

        buttonBox.setOpaque(false);
        buttonBox.add(addButton);

        rightSidePanel.add(
                cardsContainer,
                BorderLayout.NORTH
        );

        rightSidePanel.add(
                buttonBox,
                BorderLayout.SOUTH
        );
        
        //Add to Tasks panel
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
            "Due date:", txtDue,
            "Description:", txtDesc
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add new work", JOptionPane.OK_CANCEL_OPTION);
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
                JOptionPane.showMessageDialog(this,
                		"Please enter the subject and assignment name.",
                		"Notification",
                		JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
