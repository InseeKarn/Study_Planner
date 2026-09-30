package view;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;

public class Header extends JPanel{
	public Header(CardLayout pageCardLayout, JPanel contentCardPanel) {
		
        Color textDark = new Color(60, 50, 40);
        Color accentNavy = new Color(44, 76, 89);
        
        setLayout(new BorderLayout());
		
        
        // title
        JLabel titleLabel = new JLabel("Study Planner");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(textDark);
        

        // Local date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH);
        String formattedDate = currentDate.format(formatter).toUpperCase();
        
        JLabel dateLabel = new JLabel(formattedDate);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        dateLabel.setForeground(new Color(130, 120, 110));
        
        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(dateLabel);
        
        // navigator
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        navPanel.setOpaque(false);
        
        JButton navSchedule = new JButton("SCHEDULE");
        JButton navTasks = new JButton("SUBJECTS & TASKS");
        
        styleNavButton(navSchedule, new Color(150, 140, 130));
        styleNavButton(navTasks, accentNavy);

        navSchedule.addActionListener(e -> {
            pageCardLayout.show(contentCardPanel, "SCHEDULE");
            navSchedule.setForeground(accentNavy);
            navTasks.setForeground(new Color(150, 140, 130));
        });

        navTasks.addActionListener(e -> {
            pageCardLayout.show(contentCardPanel, "TASKS");
            navTasks.setForeground(accentNavy);
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
