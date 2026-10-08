package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import model.Task;

public class StatusRenderer extends JPanel implements TableCellRenderer {
    private final JLabel label;

    public StatusRenderer() {
        setLayout(new GridBagLayout());
        setOpaque(true);

        label = new JLabel();
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        add(label);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setBackground(isSelected ? new Color(45, 45, 45) : table.getBackground());

        if (value instanceof Task) {
            Task task = (Task) value;
            Color statusColor = task.getStatusColor();
            String statusText = task.getStatusText();

            label.setText(" " + statusText + " ");
            label.setBackground(statusColor);
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        } else {
            label.setText(value == null ? "" : value.toString());
            label.setBackground(new Color(230, 160, 0));
            label.setForeground(Color.WHITE);
        }

        return this;
    }
}