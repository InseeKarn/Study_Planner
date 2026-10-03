package view;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;

public class Header extends JPanel{
	
	public Header(CardLayout pageCardLayout, JPanel contentCardPanel) {
		
		// Colors
        Color textPrimary = new Color(242, 242, 242);
        Color textSecondary = new Color(120, 120, 120);
        Color textInactive = new Color(130, 130, 130);
        Color headerDark = new Color(24, 24, 24);
        
        setLayout(new BorderLayout());
		
        
        // title
        JLabel titleLabel = new JLabel("Study Planner");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        
        titleLabel.setForeground(textPrimary);
        

        // Local date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH);
        String formattedDate = currentDate.format(formatter).toUpperCase();
        
        JLabel dateLabel = new JLabel(formattedDate);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        dateLabel.setForeground(new Color(130, 120, 110));
        
        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        setBackground(headerDark);
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(dateLabel);
        
        // navigator
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        navPanel.setOpaque(false);
        
        JButton navSchedule = new JButton("SCHEDULE");
        JButton navTasks = new JButton("SUBJECTS & TASKS");
        
        styleNavButton(navSchedule, new Color(150, 140, 130));
        styleNavButton(navTasks, textSecondary);

        navSchedule.addActionListener(e -> {
            pageCardLayout.show(contentCardPanel, "SCHEDULE");
            navSchedule.setForeground(textPrimary);
            navTasks.setForeground(new Color(150, 140, 130));
        });

        navTasks.addActionListener(e -> {
            pageCardLayout.show(contentCardPanel, "TASKS");
            navTasks.setForeground(textInactive);
            navSchedule.setForeground(new Color(150, 140, 130));
        });

        navPanel.add(navSchedule);
        navPanel.add(navTasks);

        add(titleBox, BorderLayout.WEST);
        add(navPanel, BorderLayout.EAST);
        
        
	}
	
    private void styleNavButton(JButton btn, Color color) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
