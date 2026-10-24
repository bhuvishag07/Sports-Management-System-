package view;

import controller.ClubController;
import model.InvalidMemberException;
import model.Member;
import model.Membership;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Member Management Panel implementing CRUD operations, validation, and table display.
 * Styled with Soft Pink + Cream theme.
 */
public class MemberPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    // Form inputs
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtAge;
    private JComboBox<String> cmbGender;
    private JTextField txtContact;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JComboBox<String> cmbPlan;
    private JTextField txtStartDate;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Table
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblCount;

    public MemberPanel(ClubController controller, MainFrame mainFrame) {
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
                "Member Registration & Management",
                "Create, view, update, delete, and manage sports club members"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Split: Left = Form, Right = Table & Search
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // --- LEFT FORM CARD ---
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Theme.CARD_BG);
        formCard.setBorder(Theme.createCardBorder());
        formCard.setPreferredSize(new Dimension(340, 500));

        JLabel formHeading = new JLabel("Member Details");
        formHeading.setFont(Theme.FONT_HEADING);
        formHeading.setForeground(Theme.TEXT_MAIN);
        formCard.add(formHeading, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(9, 2, 8, 8));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        txtId = new JTextField();
        txtName = new JTextField();
        txtAge = new JTextField();
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        txtContact = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();
        cmbPlan = new JComboBox<>(Membership.AVAILABLE_PLANS);
        txtStartDate = new JTextField(LocalDate.now().toString());

        Theme.styleTextField(txtId);
        Theme.styleTextField(txtName);
        Theme.styleTextField(txtAge);
        Theme.styleComboBox(cmbGender);
        Theme.styleTextField(txtContact);
        Theme.styleTextField(txtEmail);
        Theme.styleTextField(txtAddress);
        Theme.styleComboBox(cmbPlan);
        Theme.styleTextField(txtStartDate);

        addFormField(fieldsPanel, "Member ID:*", txtId);
        addFormField(fieldsPanel, "Full Name:*", txtName);
        addFormField(fieldsPanel, "Age:*", txtAge);
        addFormField(fieldsPanel, "Gender:", cmbGender);
        addFormField(fieldsPanel, "Contact:*", txtContact);
        addFormField(fieldsPanel, "Email:*", txtEmail);
        addFormField(fieldsPanel, "Address:", txtAddress);
        addFormField(fieldsPanel, "Plan:*", cmbPlan);
        addFormField(fieldsPanel, "Start Date:", txtStartDate);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Grid
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonsGrid.setOpaque(false);

        btnAdd = new JButton("➕ Add Member");
        Theme.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> onAddMember());

        btnUpdate = new JButton("✏️ Update");
        Theme.styleSecondaryButton(btnUpdate);
        btnUpdate.addActionListener(e -> onUpdateMember());

        btnDelete = new JButton("🗑️ Delete");
        Theme.styleCustomButton(btnDelete, Theme.STATUS_EXPIRED, Color.WHITE);
        btnDelete.addActionListener(e -> onDeleteMember());

        btnClear = new JButton("🔄 Clear / Reset");
        Theme.styleNeutralButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        buttonsGrid.add(btnAdd);
        buttonsGrid.add(btnUpdate);
        buttonsGrid.add(btnDelete);
        buttonsGrid.add(btnClear);

        formCard.add(buttonsGrid, BorderLayout.SOUTH);
        contentPanel.add(formCard, BorderLayout.WEST);

        // --- RIGHT TABLE CARD ---
        JPanel tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        // Quick filter row
        JPanel searchBar = new JPanel(new BorderLayout(8, 8));
        searchBar.setOpaque(false);

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchInputPanel.setOpaque(false);
        JLabel lblFilter = new JLabel("🔍 Filter Members:");
        lblFilter.setFont(Theme.FONT_BOLD);
        lblFilter.setForeground(Theme.TEXT_MUTED);
        searchInputPanel.add(lblFilter);

        txtSearch = new JTextField(18);
        Theme.styleTextField(txtSearch);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterTable(txtSearch.getText().trim());
            }
        });
        searchInputPanel.add(txtSearch);

        JButton btnSearchClear = new JButton("Clear");
        Theme.styleSecondaryButton(btnSearchClear);
        btnSearchClear.addActionListener(e -> {
            txtSearch.setText("");
            refreshData();
        });
        searchInputPanel.add(btnSearchClear);

        lblCount = new JLabel("Total Members: 0");
        lblCount.setFont(Theme.FONT_BOLD);
        lblCount.setForeground(Theme.TEXT_MUTED);

        searchBar.add(searchInputPanel, BorderLayout.WEST);
        searchBar.add(lblCount, BorderLayout.EAST);
        tableCard.add(searchBar, BorderLayout.NORTH);

        // JTable
        String[] columnNames = {"ID", "Name", "Age", "Gender", "Contact", "Email", "Plan", "Expiry Date", "Status", "Enrolled Sports"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(tableModel);
        Theme.styleTable(memberTable);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && memberTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });

        // Soft Color renderer for status
        memberTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String str = value.toString();
                    if (str.startsWith("Expired")) {
                        c.setForeground(Theme.STATUS_EXPIRED);
                        setFont(Theme.FONT_BOLD);
                    } else if (str.startsWith("Expiring")) {
                        c.setForeground(Theme.STATUS_EXPIRING);
                        setFont(Theme.FONT_BOLD);
                    } else {
                        c.setForeground(Theme.STATUS_ACTIVE);
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

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        generateNextId();
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_MAIN);
        panel.add(lbl);
        panel.add(field);
    }

    private void generateNextId() {
        int maxId = 1000;
        for (Member m : controller.getAllMembers()) {
            try {
                if (m.getId().startsWith("M")) {
                    int num = Integer.parseInt(m.getId().substring(1));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {
            }
        }
        txtId.setText("M" + (maxId + 1));
    }

    private void onAddMember() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String ageStr = txtAge.getText().trim();
            String gender = (String) cmbGender.getSelectedItem();
            String contact = txtContact.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();
            String plan = (String) cmbPlan.getSelectedItem();
            String startStr = txtStartDate.getText().trim();

            if (ageStr.isEmpty()) throw new InvalidMemberException("Age is required.");
            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                throw new InvalidMemberException("Age must be a valid integer number.");
            }

            LocalDate startDate;
            try {
                startDate = LocalDate.parse(startStr);
            } catch (Exception e) {
                throw new InvalidMemberException("Start Date must be in YYYY-MM-DD format.");
            }

            Membership membership = new Membership("MS-" + id, plan, startDate);
            Member member = new Member(id, name, age, gender, contact, email, address, membership);

            controller.addMember(member);
            JOptionPane.showMessageDialog(this, "Member '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        } catch (InvalidMemberException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateMember() {
        try {
            String id = txtId.getText().trim();
            Member existing = controller.getMemberById(id);
            if (existing == null) {
                throw new InvalidMemberException("Please select an existing member to update.");
            }

            String name = txtName.getText().trim();
            String ageStr = txtAge.getText().trim();
            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                throw new InvalidMemberException("Age must be an integer.");
            }

            String gender = (String) cmbGender.getSelectedItem();
            String contact = txtContact.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();
            String plan = (String) cmbPlan.getSelectedItem();

            LocalDate startDate;
            try {
                startDate = LocalDate.parse(txtStartDate.getText().trim());
            } catch (Exception e) {
                throw new InvalidMemberException("Start Date must be in YYYY-MM-DD format.");
            }

            Membership membership = existing.getMembership();
            if (membership == null
                    || !membership.getPlanName().equalsIgnoreCase(plan)
                    || membership.getStartDate() == null
                    || !membership.getStartDate().equals(startDate)) {
                membership = new Membership("MS-" + existing.getId(), plan, startDate);
            }

            Member updatedMember = new Member(existing.getId(), name, age, gender, contact, email, address, membership);
            updatedMember.setEnrolledSports(existing.getEnrolledSports() == null
                    ? new HashSet<>()
                    : new HashSet<>(existing.getEnrolledSports()));
            controller.updateMember(updatedMember);
            JOptionPane.showMessageDialog(this, "Member updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
        } catch (InvalidMemberException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteMember() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter or select a Member ID to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Member m = controller.getMemberById(id);
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Member with ID '" + id + "' not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete member: " + m.getName() + " (" + id + ")? Scheduled training sessions will be cancelled; payment and training history will be kept.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = controller.deleteMember(id);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.notifyDataChanged();
                clearForm();
            }
        }
    }

    private void populateFormFromSelectedRow() {
        int row = memberTable.getSelectedRow();
        if (row != -1) {
            txtId.setText((String) tableModel.getValueAt(row, 0));
            txtId.setEditable(false);
            txtName.setText((String) tableModel.getValueAt(row, 1));
            txtAge.setText(tableModel.getValueAt(row, 2).toString());
            cmbGender.setSelectedItem(tableModel.getValueAt(row, 3));
            txtContact.setText((String) tableModel.getValueAt(row, 4));
            txtEmail.setText((String) tableModel.getValueAt(row, 5));
            cmbPlan.setSelectedItem(tableModel.getValueAt(row, 6));

            Member m = controller.getMemberById(txtId.getText().trim());
            if (m != null) {
                txtAddress.setText(m.getAddress() != null ? m.getAddress() : "");
                if (m.getMembership() != null && m.getMembership().getStartDate() != null) {
                    txtStartDate.setText(m.getMembership().getStartDate().toString());
                }
            }
        }
    }

    private void clearForm() {
        txtId.setEditable(true);
        txtName.setText("");
        txtAge.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtStartDate.setText(LocalDate.now().toString());
        cmbGender.setSelectedIndex(0);
        cmbPlan.setSelectedIndex(0);
        memberTable.clearSelection();
        generateNextId();
    }

    private void filterTable(String query) {
        ArrayList<Member> filtered = controller.searchMembers(query, "All");
        renderTableRows(filtered);
    }

    private void renderTableRows(ArrayList<Member> list) {
        tableModel.setRowCount(0);
        for (Member m : list) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getAge(),
                    m.getGender(),
                    m.getContact(),
                    m.getEmail(),
                    m.getMembership() != null ? m.getMembership().getPlanName() : "None",
                    m.getMembershipExpiryDate(),
                    m.getMembershipStatus(),
                    m.getEnrolledSportsString()
            });
        }
        lblCount.setText("Showing: " + list.size() + " of " + controller.getAllMembers().size() + " members");
    }

    public void refreshData() {
        renderTableRows(controller.getAllMembers());
    }
}
