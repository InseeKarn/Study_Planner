package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

//header import
import view.Header;
import view.Schedule;
import view.Tasks;

public class Main extends JFrame {
    private final Data dataRepository = new Data();

    // Components
    private CardLayout pageCardLayout;
    private JPanel contentCardPanel;


    public Main() {
        setTitle("Study Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // Colors
        Color bgCream = new Color(245, 240, 230);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(bgCream);
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