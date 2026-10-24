package view;

import controller.ClubController;
import model.Training;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedList;

/**
 * Training History Panel.
 * Demonstrates LinkedList history archive requirement.
 * Styled with Soft Pink + Cream theme.
 */
public class TrainingHistoryPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalCompleted;
    private JTextField txtSearch;

    public TrainingHistoryPanel(ClubController controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(16, 18, 16, 18));
        setBackground(Theme.BG_CREAM);

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = Theme.createHeader(
                "Training History Archive (LinkedList)",
                "Completed athletic training sessions chronological archive managed via Java LinkedList"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        // Top Controls
        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Filter History:");
        lblSearch.setFont(Theme.FONT_BOLD);
        lblSearch.setForeground(Theme.TEXT_MUTED);
        searchPanel.add(lblSearch);

        txtSearch = new JTextField(18);
        Theme.styleTextField(txtSearch);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterHistory(txtSearch.getText().trim());
            }
        });
        searchPanel.add(txtSearch);

        JButton btnClear = new JButton("Clear");
        Theme.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            renderTable(controller.getTrainingHistory());
        });
        searchPanel.add(btnClear);

        lblTotalCompleted = new JLabel("Completed Sessions in History: 0");
        lblTotalCompleted.setFont(Theme.FONT_BOLD);
        lblTotalCompleted.setForeground(Theme.BTN_PRIMARY);

        topControls.add(searchPanel, BorderLayout.WEST);
        topControls.add(lblTotalCompleted, BorderLayout.EAST);
        tableCard.add(topControls, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Session ID", "Sport", "Coach", "Member ID", "Member Name", "Date", "Fixed Slot", "Status", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        Theme.styleTable(historyTable);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void filterHistory(String query) {
        LinkedList<Training> all = controller.getTrainingHistory();
        if (query.isEmpty()) {
            renderTable(all);
            return;
        }
        String q = query.toLowerCase();
        LinkedList<Training> filtered = new LinkedList<>();
        for (Training t : all) {
            if (t.getTrainingId().toLowerCase().contains(q) ||
                t.getSportName().toLowerCase().contains(q) ||
                t.getCoachName().toLowerCase().contains(q) ||
                t.getMemberName().toLowerCase().contains(q) ||
                t.getTrainingSlot().toLowerCase().contains(q)) {
                filtered.add(t);
            }
        }
        renderTable(filtered);
    }

    private void renderTable(LinkedList<Training> list) {
        tableModel.setRowCount(0);
        for (Training t : list) {
            tableModel.addRow(new Object[]{
                    t.getTrainingId(),
                    t.getSportName(),
                    t.getCoachName(),
                    t.getMemberId(),
                    t.getMemberName(),
                    t.getDate(),
                    t.getTrainingSlot(),
                    t.getStatus(),
                    t.getNotes()
            });
        }
        lblTotalCompleted.setText("Completed Sessions in History (LinkedList): " + list.size());
    }

    public void refreshData() {
        renderTable(controller.getTrainingHistory());
    }
}
