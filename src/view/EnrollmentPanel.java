package view;

import controller.ClubController;
import model.InvalidMemberException;
import model.Member;
import model.Sport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Member Sports Activity Enrollment Panel.
 * Demonstrates HashSet uniqueness requirement.
 * Styled with Soft Pink + Cream theme.
 */
public class EnrollmentPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JComboBox<String> cmbMember;
    private JComboBox<String> cmbSport;
    private JLabel lblSelectedMemberDetails;
    private JList<String> listEnrolledSports;
    private DefaultListModel<String> enrolledListModel;

    private JButton btnEnroll;
    private JButton btnUnenroll;

    private JTable enrollmentTable;
    private DefaultTableModel tableModel;

    public EnrollmentPanel(ClubController controller, MainFrame mainFrame) {
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
                "Member Sports Activity Enrollment (HashSet)",
                "Enroll members into sports activities — utilizes Java HashSet to strictly prevent duplicate enrollments"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // --- LEFT CARD ---
        JPanel formCard = new JPanel(new BorderLayout(10, 10));
        formCard.setBackground(Theme.CARD_BG);
        formCard.setBorder(Theme.createCardBorder());
        formCard.setPreferredSize(new Dimension(360, 480));

        JLabel formHeading = new JLabel("Enroll Member in Activity");
        formHeading.setFont(Theme.FONT_HEADING);
        formHeading.setForeground(Theme.TEXT_MAIN);
        formCard.add(formHeading, BorderLayout.NORTH);

        JPanel centerForm = new JPanel(new BorderLayout(10, 10));
        centerForm.setOpaque(false);

        JPanel inputsPanel = new JPanel(new GridLayout(2, 2, 8, 10));
        inputsPanel.setOpaque(false);

        cmbMember = new JComboBox<>();
        cmbSport = new JComboBox<>();

        Theme.styleComboBox(cmbMember);
        Theme.styleComboBox(cmbSport);

        cmbMember.addActionListener(e -> updateSelectedMemberView());

        addFormField(inputsPanel, "Select Member:*", cmbMember);
        addFormField(inputsPanel, "Select Sport:*", cmbSport);

        centerForm.add(inputsPanel, BorderLayout.NORTH);

        // Member's enrolled sports box
        JPanel sportsBox = new JPanel(new BorderLayout(6, 6));
        sportsBox.setOpaque(false);
        sportsBox.setBorder(new EmptyBorder(10, 0, 5, 0));

        lblSelectedMemberDetails = new JLabel("Currently Enrolled Sports (HashSet):");
        lblSelectedMemberDetails.setFont(Theme.FONT_BOLD);
        lblSelectedMemberDetails.setForeground(Theme.BTN_PRIMARY);

        enrolledListModel = new DefaultListModel<>();
        listEnrolledSports = new JList<>(enrolledListModel);
        listEnrolledSports.setFont(Theme.FONT_REGULAR);
        listEnrolledSports.setBackground(Theme.SUBTLE_FILL);
        listEnrolledSports.setForeground(Theme.TEXT_MAIN);
        listEnrolledSports.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollSports = new JScrollPane(listEnrolledSports);
        scrollSports.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));

        sportsBox.add(lblSelectedMemberDetails, BorderLayout.NORTH);
        sportsBox.add(scrollSports, BorderLayout.CENTER);

        centerForm.add(sportsBox, BorderLayout.CENTER);
        formCard.add(centerForm, BorderLayout.CENTER);

        // Buttons
        JPanel buttonsGrid = new JPanel(new GridLayout(1, 2, 8, 8));
        buttonsGrid.setOpaque(false);

        btnEnroll = new JButton("➕ Enroll (HashSet)");
        Theme.stylePrimaryButton(btnEnroll);
        btnEnroll.addActionListener(e -> onEnroll());

        btnUnenroll = new JButton("❌ Unenroll");
        Theme.styleCustomButton(btnUnenroll, Theme.STATUS_EXPIRED, Color.WHITE);
        btnUnenroll.addActionListener(e -> onUnenroll());

        buttonsGrid.add(btnEnroll);
        buttonsGrid.add(btnUnenroll);
        formCard.add(buttonsGrid, BorderLayout.SOUTH);

        contentPanel.add(formCard, BorderLayout.WEST);

        // --- RIGHT TABLE CARD ---
        JPanel tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        JLabel tableTitle = new JLabel("Members and Enrolled Sports Overview");
        tableTitle.setFont(Theme.FONT_HEADING);
        tableTitle.setForeground(Theme.TEXT_MAIN);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columnNames = {"Member ID", "Member Name", "Plan", "Unique Enrolled Sports (HashSet count)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        enrollmentTable = new JTable(tableModel);
        Theme.styleTable(enrollmentTable);
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && enrollmentTable.getSelectedRow() != -1) {
                String memberId = (String) tableModel.getValueAt(enrollmentTable.getSelectedRow(), 0);
                for (int i = 0; i < cmbMember.getItemCount(); i++) {
                    if (cmbMember.getItemAt(i).startsWith(memberId)) {
                        cmbMember.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, String labelText, JComponent comp) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_MAIN);
        panel.add(lbl);
        panel.add(comp);
    }

    private String getSelectedMemberId() {
        String sel = (String) cmbMember.getSelectedItem();
        if (sel == null || sel.isEmpty()) return null;
        return sel.split(" - ")[0];
    }

    private void updateSelectedMemberView() {
        enrolledListModel.clear();
        String memberId = getSelectedMemberId();
        if (memberId != null) {
            Member m = controller.getMemberById(memberId);
            if (m != null && m.getEnrolledSports() != null) {
                for (String sport : m.getEnrolledSports()) {
                    enrolledListModel.addElement(sport);
                }
            }
        }
    }

    private void onEnroll() {
        String memberId = getSelectedMemberId();
        if (memberId == null) {
            JOptionPane.showMessageDialog(this, "Please select a member.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sportName = (String) cmbSport.getSelectedItem();
        if (sportName == null || sportName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a sport discipline.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controller.enrollMemberInSport(memberId, sportName);
            JOptionPane.showMessageDialog(this, 
                    "Successfully enrolled in '" + sportName + "'!\nHashSet verified no duplicates exist.", 
                    "Enrolled", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            updateSelectedMemberView();
        } catch (InvalidMemberException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate Enrollment Prevented (HashSet)", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onUnenroll() {
        String memberId = getSelectedMemberId();
        String sportToUnenroll = listEnrolledSports.getSelectedValue();

        if (memberId == null || sportToUnenroll == null) {
            JOptionPane.showMessageDialog(this, "Please select an enrolled sport from the list to unenroll.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean removed = controller.unenrollMemberFromSport(memberId, sportToUnenroll);
        if (removed) {
            JOptionPane.showMessageDialog(this, "Removed enrollment from " + sportToUnenroll, "Unenrolled", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            updateSelectedMemberView();
        }
    }

    public void refreshData() {
        String prevMember = (String) cmbMember.getSelectedItem();

        cmbMember.removeAllItems();
        for (Member m : controller.getAllMembers()) {
            cmbMember.addItem(m.getId() + " - " + m.getName());
        }
        if (prevMember != null) {
            cmbMember.setSelectedItem(prevMember);
        }

        cmbSport.removeAllItems();
        for (Sport s : controller.getAllSports()) {
            cmbSport.addItem(s.getSportName());
        }

        tableModel.setRowCount(0);
        for (Member m : controller.getAllMembers()) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getMembership() != null ? m.getMembership().getPlanName() : "None",
                    m.getEnrolledSportsString() + " (" + (m.getEnrolledSports() != null ? m.getEnrolledSports().size() : 0) + ")"
            });
        }

        updateSelectedMemberView();
    }
}
