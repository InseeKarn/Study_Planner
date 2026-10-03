package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import service.Data;
//header import
import view.Header;

//Pages import
import view.Schedule;
import view.Tasks;

public class Main extends JFrame {
	
    // Components
    private CardLayout pageCardLayout;
    private JPanel contentCardPanel;

	// Data
    private final Data dataRepository = new Data();

    public Main() {
        setTitle("Study Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // Colors
        Color bgDark = new Color(17, 17, 17);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(bgDark);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Content (CardLayout)
        pageCardLayout = new CardLayout();
        contentCardPanel = new JPanel(pageCardLayout);
        contentCardPanel.setOpaque(false);
        
        //header
        Header headerPanel = new Header(
        		pageCardLayout,
        		contentCardPanel
		);
        
        //Schedule Page
        JPanel schedulePage = new Schedule();
        
        //Tasks Page
        JPanel tasksPage = new Tasks(dataRepository);

        contentCardPanel.add(schedulePage, "SCHEDULE");
        contentCardPanel.add(tasksPage, "TASKS");
        
        pageCardLayout.show(contentCardPanel, "TASKS");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentCardPanel, BorderLayout.CENTER);

        add(mainPanel);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}