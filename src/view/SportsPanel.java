package view;

import controller.ClubController;
import model.Coach;
import model.Sport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Sports Management Panel implementing CRUD operations and search for sports disciplines.
 * Styled with Soft Pink + Cream theme.
 */
public class SportsPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JTextField txtSportId;
    private JTextField txtSportName;
    private JComboBox<String> cmbCategory;
    private JTextField txtDescription;
    private JComboBox<String> cmbCoach;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable sportsTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblCount;

    public SportsPanel(ClubController controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(16, 18, 16, 18));
        setBackground(Theme.BG_CREAM);

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Top Header
        JPanel headerPanel = Theme.createHeader(
                "Sports Disciplines Management",
                "Add, update, and manage club sports, facilities, and assigned coaching staff"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // --- FORM CARD ---
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Theme.CARD_BG);
        formCard.setBorder(Theme.createCardBorder());
        formCard.setPreferredSize(new Dimension(340, 450));

        JLabel formHeading = new JLabel("Sport Discipline Details");
        formHeading.setFont(Theme.FONT_HEADING);
        formHeading.setForeground(Theme.TEXT_MAIN);
        formCard.add(formHeading, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 8, 12));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        txtSportId = new JTextField();
        txtSportName = new JTextField();
        cmbCategory = new JComboBox<>(Sport.CATEGORIES);
        txtDescription = new JTextField();
        cmbCoach = new JComboBox<>();

        Theme.styleTextField(txtSportId);
        Theme.styleTextField(txtSportName);
        Theme.styleComboBox(cmbCategory);
        Theme.styleTextField(txtDescription);
        Theme.styleComboBox(cmbCoach);

        addFormField(fieldsPanel, "Sport ID:*", txtSportId);
        addFormField(fieldsPanel, "Sport Name:*", txtSportName);
        addFormField(fieldsPanel, "Category:*", cmbCategory);
        addFormField(fieldsPanel, "Description:", txtDescription);
        addFormField(fieldsPanel, "Assigned Coach:", cmbCoach);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonsGrid.setOpaque(false);

        btnAdd = new JButton("➕ Add Sport");
        Theme.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> onAddSport());

        btnUpdate = new JButton("✏️ Update");
        Theme.styleSecondaryButton(btnUpdate);
        btnUpdate.addActionListener(e -> onUpdateSport());

        btnDelete = new JButton("🗑️ Delete");
        Theme.styleCustomButton(btnDelete, Theme.STATUS_EXPIRED, Color.WHITE);
        btnDelete.addActionListener(e -> onDeleteSport());

        btnClear = new JButton("🔄 Clear Form");
        Theme.styleNeutralButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        buttonsGrid.add(btnAdd);
        buttonsGrid.add(btnUpdate);
        buttonsGrid.add(btnDelete);
        buttonsGrid.add(btnClear);
        formCard.add(buttonsGrid, BorderLayout.SOUTH);

        contentPanel.add(formCard, BorderLayout.WEST);

        // --- TABLE CARD ---
        JPanel tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        // Filter Bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 8));
        searchBar.setOpaque(false);

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchInputPanel.setOpaque(false);
        JLabel lblFilter = new JLabel("🔍 Filter Sports:");
        lblFilter.setFont(Theme.FONT_BOLD);
        lblFilter.setForeground(Theme.TEXT_MUTED);
        searchInputPanel.add(lblFilter);

        txtSearch = new JTextField(16);
        Theme.styleTextField(txtSearch);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                renderTable(controller.searchSports(txtSearch.getText().trim()));
            }
        });
        searchInputPanel.add(txtSearch);

        JButton btnSearchClear = new JButton("Clear");
        Theme.styleSecondaryButton(btnSearchClear);
        btnSearchClear.addActionListener(e -> {
            txtSearch.setText("");
            renderTable(controller.getAllSports());
        });
        searchInputPanel.add(btnSearchClear);

        lblCount = new JLabel("Total Sports: 0");
        lblCount.setFont(Theme.FONT_BOLD);
        lblCount.setForeground(Theme.TEXT_MUTED);

        searchBar.add(searchInputPanel, BorderLayout.WEST);
        searchBar.add(lblCount, BorderLayout.EAST);
        tableCard.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Sport ID", "Sport Name", "Category", "Description", "Assigned Coach"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        sportsTable = new JTable(tableModel);
        Theme.styleTable(sportsTable);
        sportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sportsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sportsTable.getSelectedRow() != -1) {
                populateForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(sportsTable);
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
        int maxId = 100;
        for (Sport s : controller.getAllSports()) {
            try {
                if (s.getSportId().startsWith("SP")) {
                    int num = Integer.parseInt(s.getSportId().substring(2));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {
            }
        }
        txtSportId.setText("SP" + (maxId + 1));
    }

    private void onAddSport() {
        try {
            String id = txtSportId.getText().trim();
            String name = txtSportName.getText().trim();
            String category = (String) cmbCategory.getSelectedItem();
            String desc = txtDescription.getText().trim();
            String coach = (String) cmbCoach.getSelectedItem();

            Sport sport = new Sport(id, name, category, desc, coach);
            controller.addSport(sport);
            JOptionPane.showMessageDialog(this, "Sport '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateSport() {
        try {
            String id = txtSportId.getText().trim();
            Sport existing = controller.getSportById(id);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Select an existing sport to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            existing.setSportName(txtSportName.getText().trim());
            existing.setCategory((String) cmbCategory.getSelectedItem());
            existing.setDescription(txtDescription.getText().trim());
            existing.setAssignedCoachName((String) cmbCoach.getSelectedItem());

            controller.updateSport(existing);
            JOptionPane.showMessageDialog(this, "Sport updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteSport() {
        String id = txtSportId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Sport to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Sport s = controller.getSportById(id);
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Sport not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete sport: " + s.getSportName() + " (" + id + ")? Enrollments will be removed and scheduled sessions cancelled.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteSport(id);
            JOptionPane.showMessageDialog(this, "Sport deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        }
    }

    private void populateForm() {
        int row = sportsTable.getSelectedRow();
        if (row != -1) {
            txtSportId.setText((String) tableModel.getValueAt(row, 0));
            txtSportName.setText((String) tableModel.getValueAt(row, 1));
            cmbCategory.setSelectedItem(tableModel.getValueAt(row, 2));
            txtDescription.setText((String) tableModel.getValueAt(row, 3));
            cmbCoach.setSelectedItem(tableModel.getValueAt(row, 4));
        }
    }

    private void clearForm() {
        txtSportName.setText("");
        txtDescription.setText("");
        cmbCategory.setSelectedIndex(0);
        if (cmbCoach.getItemCount() > 0) cmbCoach.setSelectedIndex(0);
        sportsTable.clearSelection();
        generateNextId();
    }

    private void renderTable(ArrayList<Sport> list) {
        tableModel.setRowCount(0);
        for (Sport s : list) {
            tableModel.addRow(new Object[]{
                    s.getSportId(),
                    s.getSportName(),
                    s.getCategory(),
                    s.getDescription(),
                    s.getAssignedCoachName()
            });
        }
        lblCount.setText("Total Sports: " + list.size());
    }

    public void refreshData() {
        cmbCoach.removeAllItems();
        cmbCoach.addItem("Unassigned");
        for (Coach c : controller.getAllCoaches()) {
            cmbCoach.addItem(c.getName());
        }
        renderTable(controller.getAllSports());
    }
}
