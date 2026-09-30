package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Schedule extends JPanel{
	public Schedule() {
		Color BG = new Color(252, 250, 245);
		
        setLayout(new GridBagLayout());
        setBackground(BG);
        
        JLabel scheduleLabel = new JLabel("--- หน้า Schedule (ปฏิทิน) ---");
        scheduleLabel.setFont(new Font("Leelawadee UI", Font.BOLD, 18));
        scheduleLabel.setForeground(Color.GRAY);
        
        add(scheduleLabel);
	}
}
