package view.task;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import service.Data;

public class FilterPanel extends JPanel {

    public interface FilterListener {
        void onFilterChanged(String status, String subject);
    }

    // Colors
    private final Color cardBg = new Color(24, 24, 24);
    private final Color elementDark = new Color(32, 32, 32);
    private final Color borderColor = new Color(60, 60, 60);
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);
    private final Color accentColor = new Color(100, 160, 255);

    private final JComboBox<String> statusCombo;
    private final JComboBox<String> subjectCombo;
    private final Data dataRepository;
    private FilterListener filterListener;

    // ตัวเลือกสถานะทั้งหมด
    private static final String[] STATUS_OPTIONS = {
        "All", "In Progress", "Completed", "Overdue", "Due Today", "Urgent", "Warning", "Normal", "No Due Date"
    };

    public FilterPanel(Data dataRepository) {
        this.dataRepository = dataRepository;

        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));
        setBackground(cardBg);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        // --- Filter icon / label ---
        JLabel filterIcon = new JLabel("⚙");
        filterIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        filterIcon.setForeground(textSecondary);
        add(filterIcon);

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        filterLabel.setForeground(textPrimary);
        add(filterLabel);

        // --- Status ComboBox ---
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(textSecondary);
        add(statusLabel);

        statusCombo = new JComboBox<>(STATUS_OPTIONS);
        styleComboBox(statusCombo);
        add(statusCombo);

        // --- Separator ---
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 20));
        sep.setForeground(borderColor);
        add(sep);

        // --- Subject ComboBox ---
        JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subjectLabel.setForeground(textSecondary);
        add(subjectLabel);

        subjectCombo = new JComboBox<>();
        styleComboBox(subjectCombo);
        refreshSubjects();
        add(subjectCombo);

        // --- Clear Button ---
        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        clearBtn.setForeground(accentColor);
        clearBtn.setBackground(elementDark);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(65, 26));
        clearBtn.addActionListener(e -> clearFilters());
        add(clearBtn);

        // --- Listeners ---
        statusCombo.addActionListener(e -> fireFilterChanged());
        subjectCombo.addActionListener(e -> fireFilterChanged());
    }

    // สไตล์ ComboBox ให้เข้ากับธีมสีเข้ม
    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setBackground(elementDark);
        combo.setForeground(textPrimary);
        combo.setPreferredSize(new Dimension(140, 26));
        combo.setBorder(BorderFactory.createLineBorder(borderColor));
    }

    // อัปเดตรายการวิชาใน ComboBox
    public void refreshSubjects() {
        String currentSelection = (String) subjectCombo.getSelectedItem();

        subjectCombo.removeAllItems();
        subjectCombo.addItem("All");

        List<String> subjects = dataRepository.getDistinctSubjects();
        for (String subject : subjects) {
            subjectCombo.addItem(subject);
        }

        // คืนค่าตัวเลือกเดิม ถ้ายังมีอยู่
        if (currentSelection != null) {
            for (int i = 0; i < subjectCombo.getItemCount(); i++) {
                if (subjectCombo.getItemAt(i).equals(currentSelection)) {
                    subjectCombo.setSelectedIndex(i);
                    return;
                }
            }
        }
        subjectCombo.setSelectedIndex(0);
    }

    // ล้าง filter ทั้งหมดกลับเป็น "All"
    private void clearFilters() {
        statusCombo.setSelectedIndex(0);
        subjectCombo.setSelectedIndex(0);
    }

    // แจ้ง listener เมื่อ filter เปลี่ยน
    private void fireFilterChanged() {
        if (filterListener != null) {
            String status = (String) statusCombo.getSelectedItem();
            String subject = (String) subjectCombo.getSelectedItem();
            filterListener.onFilterChanged(status, subject);
        }
    }

    // ดึงค่า filter ปัจจุบัน
    public String getSelectedStatus() {
        return (String) statusCombo.getSelectedItem();
    }

    public String getSelectedSubject() {
        return (String) subjectCombo.getSelectedItem();
    }

    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }
}
