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
 * Coach Management Panel implementing CRUD operations and search for club coaches and trainers.
 * Styled with Soft Pink + Cream theme.
 */
public class CoachPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JTextField txtCoachId;
    private JTextField txtCoachName;
    private JTextField txtContact;
    private JTextField txtEmail;
    private JTextField txtSpecialization;
    private JComboBox<String> cmbAssignedSport;
    private JTextField txtExperience;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable coachTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblCount;

    public CoachPanel(ClubController controller, MainFrame mainFrame) {
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
                "Coach & Trainer Management",
                "Manage professional coaching staff, specializations, credentials, and assigned sports"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // --- FORM CARD ---
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Theme.CARD_BG);
        formCard.setBorder(Theme.createCardBorder());
        formCard.setPreferredSize(new Dimension(340, 480));

        JLabel formHeading = new JLabel("Coach Profile Details");
        formHeading.setFont(Theme.FONT_HEADING);
        formHeading.setForeground(Theme.TEXT_MAIN);
        formCard.add(formHeading, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 8, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        txtCoachId = new JTextField();
        txtCoachName = new JTextField();
        txtContact = new JTextField();
        txtEmail = new JTextField();
        txtSpecialization = new JTextField();
        cmbAssignedSport = new JComboBox<>();
        txtExperience = new JTextField();

        Theme.styleTextField(txtCoachId);
        Theme.styleTextField(txtCoachName);
        Theme.styleTextField(txtContact);
        Theme.styleTextField(txtEmail);
        Theme.styleTextField(txtSpecialization);
        Theme.styleComboBox(cmbAssignedSport);
        Theme.styleTextField(txtExperience);

        addFormField(fieldsPanel, "Coach ID:*", txtCoachId);
        addFormField(fieldsPanel, "Coach Name:*", txtCoachName);
        addFormField(fieldsPanel, "Contact:*", txtContact);
        addFormField(fieldsPanel, "Email:", txtEmail);
        addFormField(fieldsPanel, "Specialization:", txtSpecialization);
        addFormField(fieldsPanel, "Sport:*", cmbAssignedSport);
        addFormField(fieldsPanel, "Experience (Yrs):", txtExperience);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonsGrid.setOpaque(false);

        btnAdd = new JButton("➕ Add Coach");
        Theme.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> onAddCoach());

        btnUpdate = new JButton("✏️ Update");
        Theme.styleSecondaryButton(btnUpdate);
        btnUpdate.addActionListener(e -> onUpdateCoach());

        btnDelete = new JButton("🗑️ Delete");
        Theme.styleCustomButton(btnDelete, Theme.STATUS_EXPIRED, Color.WHITE);
        btnDelete.addActionListener(e -> onDeleteCoach());

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
        JLabel lblFilter = new JLabel("🔍 Filter Coaches:");
        lblFilter.setFont(Theme.FONT_BOLD);
        lblFilter.setForeground(Theme.TEXT_MUTED);
        searchInputPanel.add(lblFilter);

        txtSearch = new JTextField(16);
        Theme.styleTextField(txtSearch);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                renderTable(controller.searchCoaches(txtSearch.getText().trim()));
            }
        });
        searchInputPanel.add(txtSearch);

        JButton btnSearchClear = new JButton("Clear");
        Theme.styleSecondaryButton(btnSearchClear);
        btnSearchClear.addActionListener(e -> {
            txtSearch.setText("");
            renderTable(controller.getAllCoaches());
        });
        searchInputPanel.add(btnSearchClear);

        lblCount = new JLabel("Total Coaches: 0");
        lblCount.setFont(Theme.FONT_BOLD);
        lblCount.setForeground(Theme.TEXT_MUTED);

        searchBar.add(searchInputPanel, BorderLayout.WEST);
        searchBar.add(lblCount, BorderLayout.EAST);
        tableCard.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Coach ID", "Coach Name", "Contact", "Email", "Specialization", "Assigned Sport", "Experience"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        coachTable = new JTable(tableModel);
        Theme.styleTable(coachTable);
        coachTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        coachTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && coachTable.getSelectedRow() != -1) {
                populateForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(coachTable);
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
        for (Coach c : controller.getAllCoaches()) {
            try {
                if (c.getId().startsWith("CH")) {
                    int num = Integer.parseInt(c.getId().substring(2));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {
            }
        }
        txtCoachId.setText(String.format("CH%02d", (maxId + 1)));
    }

    private void onAddCoach() {
        try {
            String id = txtCoachId.getText().trim();
            String name = txtCoachName.getText().trim();
            String contact = txtContact.getText().trim();
            String email = txtEmail.getText().trim();
            String spec = txtSpecialization.getText().trim();
            String sport = (String) cmbAssignedSport.getSelectedItem();
            String expStr = txtExperience.getText().trim();

            int exp = 0;
            if (!expStr.isEmpty()) {
                try {
                    exp = Integer.parseInt(expStr);
                } catch (NumberFormatException e) {
                    throw new Exception("Experience must be an integer number of years.");
                }
            }

            Coach coach = new Coach(id, name, contact, email, spec, sport, exp);
            controller.addCoach(coach);
            JOptionPane.showMessageDialog(this, "Coach '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateCoach() {
        try {
            String id = txtCoachId.getText().trim();
            Coach existing = controller.getCoachById(id);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Select an existing coach to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            existing.setName(txtCoachName.getText().trim());
            existing.setContact(txtContact.getText().trim());
            existing.setEmail(txtEmail.getText().trim());
            existing.setSpecialization(txtSpecialization.getText().trim());
            existing.setAssignedSport((String) cmbAssignedSport.getSelectedItem());

            String expStr = txtExperience.getText().trim();
            if (!expStr.isEmpty()) {
                try {
                    existing.setExperienceYears(Integer.parseInt(expStr));
                } catch (NumberFormatException ignored) {
                }
            }

            controller.updateCoach(existing);
            JOptionPane.showMessageDialog(this, "Coach updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteCoach() {
        String id = txtCoachId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Coach to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Coach c = controller.getCoachById(id);
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Coach not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete coach: " + c.getName() + " (" + id + ")? Assigned sports will be unassigned and scheduled sessions cancelled.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteCoach(id);
            JOptionPane.showMessageDialog(this, "Coach deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        }
    }

    private void populateForm() {
        int row = coachTable.getSelectedRow();
        if (row != -1) {
            txtCoachId.setText((String) tableModel.getValueAt(row, 0));
            txtCoachName.setText((String) tableModel.getValueAt(row, 1));
            txtContact.setText((String) tableModel.getValueAt(row, 2));
            txtEmail.setText((String) tableModel.getValueAt(row, 3));
            txtSpecialization.setText((String) tableModel.getValueAt(row, 4));
            cmbAssignedSport.setSelectedItem(tableModel.getValueAt(row, 5));
            txtExperience.setText(tableModel.getValueAt(row, 6).toString().replace(" yrs", ""));
        }
    }

    private void clearForm() {
        txtCoachName.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        txtSpecialization.setText("");
        txtExperience.setText("");
        if (cmbAssignedSport.getItemCount() > 0) cmbAssignedSport.setSelectedIndex(0);
        coachTable.clearSelection();
        generateNextId();
    }

    private void renderTable(ArrayList<Coach> list) {
        tableModel.setRowCount(0);
        for (Coach c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getContact(),
                    c.getEmail(),
                    c.getSpecialization(),
                    c.getAssignedSport(),
                    c.getExperienceYears() + " yrs"
            });
        }
        lblCount.setText("Total Coaches: " + list.size());
    }

    public void refreshData() {
        cmbAssignedSport.removeAllItems();
        for (Sport s : controller.getAllSports()) {
            cmbAssignedSport.addItem(s.getSportName());
        }
        renderTable(controller.getAllCoaches());
    }
}
