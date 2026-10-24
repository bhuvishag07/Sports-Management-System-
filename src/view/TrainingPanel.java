package view;

import controller.ClubController;
import model.Coach;
import model.InvalidTrainingException;
import model.Member;
import model.Sport;
import model.Training;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedList;

/**
 * Training Schedule Panel.
 * Demonstrates Array requirement (fixed slots) and LinkedList (completion history).
 * Styled with Soft Pink + Cream theme.
 */
public class TrainingPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JTextField txtTrainingId;
    private JComboBox<String> cmbSport;
    private JComboBox<String> cmbCoach;
    private JComboBox<String> cmbMember;
    private JTextField txtDate;
    private JComboBox<String> cmbSlot;
    private JTextField txtNotes;

    private JButton btnSchedule;
    private JButton btnComplete;
    private JButton btnCancel;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable trainingTable;
    private DefaultTableModel tableModel;
    private JLabel lblCount;

    public TrainingPanel(ClubController controller, MainFrame mainFrame) {
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
                "Training Schedule & Slot Management",
                "Schedule coaching sessions across fixed time slots and track execution status"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // --- FORM CARD ---
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Theme.CARD_BG);
        formCard.setBorder(Theme.createCardBorder());
        formCard.setPreferredSize(new Dimension(350, 500));

        JLabel formHeading = new JLabel("Schedule Training Session");
        formHeading.setFont(Theme.FONT_HEADING);
        formHeading.setForeground(Theme.TEXT_MAIN);
        formCard.add(formHeading, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 8, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        txtTrainingId = new JTextField();
        cmbSport = new JComboBox<>();
        cmbCoach = new JComboBox<>();
        cmbMember = new JComboBox<>();
        txtDate = new JTextField(LocalDate.now().plusDays(1).toString());
        cmbSlot = new JComboBox<>(Training.FIXED_SLOTS);
        txtNotes = new JTextField();

        Theme.styleTextField(txtTrainingId);
        Theme.styleComboBox(cmbSport);
        Theme.styleComboBox(cmbCoach);
        Theme.styleComboBox(cmbMember);
        Theme.styleTextField(txtDate);
        Theme.styleComboBox(cmbSlot);
        Theme.styleTextField(txtNotes);

        addFormField(fieldsPanel, "Training ID:*", txtTrainingId);
        addFormField(fieldsPanel, "Sport:*", cmbSport);
        addFormField(fieldsPanel, "Coach:*", cmbCoach);
        addFormField(fieldsPanel, "Member:*", cmbMember);
        addFormField(fieldsPanel, "Date (YYYY-MM-DD):*", txtDate);
        addFormField(fieldsPanel, "Fixed Slot (Array):*", cmbSlot);
        addFormField(fieldsPanel, "Notes:", txtNotes);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsGrid = new JPanel(new GridLayout(3, 2, 6, 6));
        buttonsGrid.setOpaque(false);

        btnSchedule = new JButton("📅 Schedule");
        Theme.stylePrimaryButton(btnSchedule);
        btnSchedule.addActionListener(e -> onSchedule());

        btnComplete = new JButton("✅ Completed");
        Theme.styleCustomButton(btnComplete, Theme.STATUS_ACTIVE, Color.WHITE);
        btnComplete.addActionListener(e -> onComplete());

        btnCancel = new JButton("❌ Cancel");
        Theme.styleCustomButton(btnCancel, Theme.STATUS_EXPIRING, Color.WHITE);
        btnCancel.addActionListener(e -> onCancelSession());

        btnDelete = new JButton("🗑️ Delete");
        Theme.styleCustomButton(btnDelete, Theme.STATUS_EXPIRED, Color.WHITE);
        btnDelete.addActionListener(e -> onDeleteSession());

        btnClear = new JButton("🔄 Clear");
        Theme.styleNeutralButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        JButton btnViewHistory = new JButton("📜 View History");
        Theme.styleSecondaryButton(btnViewHistory);
        btnViewHistory.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_TRAINING_HISTORY));

        buttonsGrid.add(btnSchedule);
        buttonsGrid.add(btnComplete);
        buttonsGrid.add(btnCancel);
        buttonsGrid.add(btnDelete);
        buttonsGrid.add(btnClear);
        buttonsGrid.add(btnViewHistory);

        formCard.add(buttonsGrid, BorderLayout.SOUTH);
        contentPanel.add(formCard, BorderLayout.WEST);

        // --- TABLE CARD ---
        JPanel tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel tableTitle = new JLabel("Active Training Schedule");
        tableTitle.setFont(Theme.FONT_HEADING);
        tableTitle.setForeground(Theme.TEXT_MAIN);

        lblCount = new JLabel("Scheduled Sessions: 0");
        lblCount.setFont(Theme.FONT_BOLD);
        lblCount.setForeground(Theme.TEXT_MUTED);

        tableTop.add(tableTitle, BorderLayout.WEST);
        tableTop.add(lblCount, BorderLayout.EAST);
        tableCard.add(tableTop, BorderLayout.NORTH);

        String[] columnNames = {"Training ID", "Sport", "Coach", "Member", "Date", "Fixed Slot", "Status", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        trainingTable = new JTable(tableModel);
        Theme.styleTable(trainingTable);
        trainingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        trainingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && trainingTable.getSelectedRow() != -1) {
                populateForm();
            }
        });

        // Status Renderer
        trainingTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (value != null) {
                    String status = value.toString();
                    if ("Completed".equalsIgnoreCase(status)) {
                        c.setForeground(Theme.STATUS_ACTIVE);
                        setFont(Theme.FONT_BOLD);
                    } else if ("Cancelled".equalsIgnoreCase(status)) {
                        c.setForeground(Theme.STATUS_EXPIRED);
                        setFont(Theme.FONT_BOLD);
                    } else {
                        c.setForeground(Theme.BTN_PRIMARY);
                        setFont(Theme.FONT_REGULAR);
                    }
                }
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.TABLE_ROW_ALT);
                } else {
                    c.setBackground(Theme.TABLE_SELECTION_BG);
                }
                setBorder(new EmptyBorder(0, 6, 0, 6));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(trainingTable);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        generateNextId();
    }

    private void addFormField(JPanel panel, String labelText, JComponent comp) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_MAIN);
        panel.add(lbl);
        panel.add(comp);
    }

    private void generateNextId() {
        int maxId = 0;
        for (Training t : controller.getTrainingSchedule()) {
            try {
                if (t.getTrainingId().startsWith("TR-")) {
                    int num = Integer.parseInt(t.getTrainingId().substring(3));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {
            }
        }
        for (Training t : controller.getTrainingHistory()) {
            try {
                if (t.getTrainingId().startsWith("TR-")) {
                    int num = Integer.parseInt(t.getTrainingId().substring(3));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {
            }
        }
        txtTrainingId.setText(String.format("TR-%02d", (maxId + 1)));
    }

    private void onSchedule() {
        try {
            String id = txtTrainingId.getText().trim();
            String sport = (String) cmbSport.getSelectedItem();
            String coach = (String) cmbCoach.getSelectedItem();
            String memberSelection = (String) cmbMember.getSelectedItem();
            String dateStr = txtDate.getText().trim();
            String slot = (String) cmbSlot.getSelectedItem();
            String notes = txtNotes.getText().trim();

            if (memberSelection == null || memberSelection.isEmpty()) {
                throw new InvalidTrainingException("Please select a registered member.");
            }
            String memberId = memberSelection.split(" - ")[0];
            String memberName = memberSelection.contains(" - ") ? memberSelection.split(" - ")[1] : memberSelection;

            LocalDate date;
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                throw new InvalidTrainingException("Date must be in YYYY-MM-DD format.");
            }

            Training training = new Training(id, sport, coach, memberId, memberName, date, slot, notes);
            controller.scheduleTraining(training);
            JOptionPane.showMessageDialog(this, "Training session scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        } catch (InvalidTrainingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onComplete() {
        String id = txtTrainingId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a training session to complete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controller.completeTraining(id);
            JOptionPane.showMessageDialog(this, "Training marked as Completed and moved to Training History (LinkedList)!", "Completed", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancelSession() {
        String id = txtTrainingId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a training session to cancel.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controller.cancelTraining(id);
            JOptionPane.showMessageDialog(this, "Training session cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteSession() {
        String id = txtTrainingId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a training session to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete training session " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteTraining(id);
            JOptionPane.showMessageDialog(this, "Training deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        }
    }

    private void populateForm() {
        int row = trainingTable.getSelectedRow();
        if (row != -1) {
            txtTrainingId.setText((String) tableModel.getValueAt(row, 0));
            cmbSport.setSelectedItem(tableModel.getValueAt(row, 1));
            cmbCoach.setSelectedItem(tableModel.getValueAt(row, 2));
            String memberName = (String) tableModel.getValueAt(row, 3);
            for (int i = 0; i < cmbMember.getItemCount(); i++) {
                if (cmbMember.getItemAt(i).contains(memberName)) {
                    cmbMember.setSelectedIndex(i);
                    break;
                }
            }
            txtDate.setText(tableModel.getValueAt(row, 4).toString());
            cmbSlot.setSelectedItem(tableModel.getValueAt(row, 5));
            txtNotes.setText(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "");
        }
    }

    private void clearForm() {
        txtDate.setText(LocalDate.now().plusDays(1).toString());
        txtNotes.setText("");
        if (cmbSport.getItemCount() > 0) cmbSport.setSelectedIndex(0);
        if (cmbCoach.getItemCount() > 0) cmbCoach.setSelectedIndex(0);
        if (cmbMember.getItemCount() > 0) cmbMember.setSelectedIndex(0);
        cmbSlot.setSelectedIndex(0);
        trainingTable.clearSelection();
        generateNextId();
    }

    private void renderTable(LinkedList<Training> list) {
        tableModel.setRowCount(0);
        for (Training t : list) {
            tableModel.addRow(new Object[]{
                    t.getTrainingId(),
                    t.getSportName(),
                    t.getCoachName(),
                    t.getMemberName(),
                    t.getDate(),
                    t.getTrainingSlot(),
                    t.getStatus(),
                    t.getNotes()
            });
        }
        lblCount.setText("Scheduled Sessions: " + list.size());
    }

    public void refreshData() {
        cmbSport.removeAllItems();
        for (Sport s : controller.getAllSports()) {
            cmbSport.addItem(s.getSportName());
        }

        cmbCoach.removeAllItems();
        for (Coach c : controller.getAllCoaches()) {
            cmbCoach.addItem(c.getName());
        }

        cmbMember.removeAllItems();
        for (Member m : controller.getAllMembers()) {
            cmbMember.addItem(m.getId() + " - " + m.getName());
        }

        renderTable(controller.getTrainingSchedule());
    }
}
