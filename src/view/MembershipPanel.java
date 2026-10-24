package view;

import controller.ClubController;
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

/**
 * Membership Management Panel.
 * Demonstrates plans, expiry calculations, and renewal workflows.
 * Styled with Soft Pink + Cream theme.
 */
public class MembershipPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    // Controls
    private JComboBox<String> cmbMember;
    private JComboBox<String> cmbPlan;
    private JTextField txtStartDate;
    private JLabel lblCalculatedExpiry;
    private JLabel lblPlanPrice;

    private JRadioButton rbAll;
    private JRadioButton rbActive;
    private JRadioButton rbExpiring;
    private JRadioButton rbExpired;

    private JTable membershipTable;
    private DefaultTableModel tableModel;
    private JLabel lblCount;

    public MembershipPanel(ClubController controller, MainFrame mainFrame) {
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
                "Membership Plans & Subscription Management",
                "Manage tiers (Monthly, Quarterly, Half-Yearly, Yearly), compute expiries, and process renewals"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // --- LEFT CARD ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(360, 500));

        // 1. Available Plans Info Card
        JPanel plansInfoCard = new JPanel(new GridLayout(2, 2, 8, 8));
        plansInfoCard.setBackground(Theme.CARD_BG);
        plansInfoCard.setBorder(Theme.createCardBorder());
        plansInfoCard.setPreferredSize(new Dimension(360, 130));

        plansInfoCard.add(createMiniPlanBadge("Monthly Plan", "1 Month", "$50.00", Theme.BTN_PRIMARY));
        plansInfoCard.add(createMiniPlanBadge("Quarterly Plan", "3 Months", "$135.00", Theme.STATUS_ACTIVE));
        plansInfoCard.add(createMiniPlanBadge("Half-Yearly", "6 Months", "$250.00", Theme.BTN_ACTION));
        plansInfoCard.add(createMiniPlanBadge("Yearly VIP", "12 Months", "$450.00", Theme.STATUS_EXPIRING));

        leftPanel.add(plansInfoCard, BorderLayout.NORTH);

        // 2. Renewal Form Card
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Theme.CARD_BG);
        formCard.setBorder(Theme.createCardBorder());

        JLabel formHeading = new JLabel("Renew / Assign Subscription");
        formHeading.setFont(Theme.FONT_HEADING);
        formHeading.setForeground(Theme.TEXT_MAIN);
        formCard.add(formHeading, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 8, 12));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        cmbMember = new JComboBox<>();
        cmbPlan = new JComboBox<>(Membership.AVAILABLE_PLANS);
        txtStartDate = new JTextField(LocalDate.now().toString());
        lblCalculatedExpiry = new JLabel(LocalDate.now().plusMonths(1).toString());
        lblCalculatedExpiry.setFont(Theme.FONT_BOLD);
        lblCalculatedExpiry.setForeground(Theme.STATUS_ACTIVE);

        lblPlanPrice = new JLabel("$50.00");
        lblPlanPrice.setFont(Theme.FONT_BOLD);
        lblPlanPrice.setForeground(Theme.BTN_PRIMARY);

        Theme.styleComboBox(cmbMember);
        Theme.styleComboBox(cmbPlan);
        Theme.styleTextField(txtStartDate);

        cmbPlan.addActionListener(e -> updateCalculatedPreview());
        txtStartDate.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateCalculatedPreview();
            }
        });

        addFormField(fieldsPanel, "Member:*", cmbMember);
        addFormField(fieldsPanel, "Subscription Plan:*", cmbPlan);
        addFormField(fieldsPanel, "Start Date:", txtStartDate);
        addFormField(fieldsPanel, "Expected Expiry:", lblCalculatedExpiry);
        addFormField(fieldsPanel, "Total Fee:", lblPlanPrice);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        JButton btnRenew = new JButton("💳 Renew & Generate Invoice");
        Theme.stylePrimaryButton(btnRenew);
        btnRenew.addActionListener(e -> onRenew());
        formCard.add(btnRenew, BorderLayout.SOUTH);

        leftPanel.add(formCard, BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.WEST);

        // --- RIGHT TABLE CARD ---
        JPanel tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        // Filter Radio Buttons
        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setOpaque(false);

        JPanel radiosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radiosPanel.setOpaque(false);
        JLabel lblFilter = new JLabel("Filter By Status:");
        lblFilter.setFont(Theme.FONT_BOLD);
        lblFilter.setForeground(Theme.TEXT_MUTED);
        radiosPanel.add(lblFilter);

        ButtonGroup bg = new ButtonGroup();
        rbAll = createStyledRadio("All Members", true);
        rbActive = createStyledRadio("Active", false);
        rbExpiring = createStyledRadio("Expiring (<30d)", false);
        rbExpired = createStyledRadio("Expired", false);

        bg.add(rbAll);
        bg.add(rbActive);
        bg.add(rbExpiring);
        bg.add(rbExpired);

        java.awt.event.ActionListener filterAction = e -> applyFilter();
        rbAll.addActionListener(filterAction);
        rbActive.addActionListener(filterAction);
        rbExpiring.addActionListener(filterAction);
        rbExpired.addActionListener(filterAction);

        radiosPanel.add(rbAll);
        radiosPanel.add(rbActive);
        radiosPanel.add(rbExpiring);
        radiosPanel.add(rbExpired);

        lblCount = new JLabel("Records: 0");
        lblCount.setFont(Theme.FONT_BOLD);
        lblCount.setForeground(Theme.TEXT_MUTED);

        filterBar.add(radiosPanel, BorderLayout.WEST);
        filterBar.add(lblCount, BorderLayout.EAST);
        tableCard.add(filterBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Member ID", "Member Name", "Plan", "Price", "Start Date", "Expiry Date", "Days Left", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        membershipTable = new JTable(tableModel);
        Theme.styleTable(membershipTable);
        membershipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        membershipTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && membershipTable.getSelectedRow() != -1) {
                String memberId = (String) tableModel.getValueAt(membershipTable.getSelectedRow(), 0);
                for (int i = 0; i < cmbMember.getItemCount(); i++) {
                    if (cmbMember.getItemAt(i).startsWith(memberId)) {
                        cmbMember.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        // Status Renderer
        membershipTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
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

        JScrollPane scrollPane = new JScrollPane(membershipTable);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JRadioButton createStyledRadio(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setOpaque(false);
        rb.setFont(Theme.FONT_REGULAR);
        rb.setForeground(Theme.TEXT_MAIN);
        return rb;
    }

    private void addFormField(JPanel panel, String labelText, JComponent comp) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_MAIN);
        panel.add(lbl);
        panel.add(comp);
    }

    private JPanel createMiniPlanBadge(String title, String duration, String price, Color color) {
        JPanel badge = new JPanel(new GridLayout(3, 1, 2, 2));
        badge.setBackground(Theme.SUBTLE_FILL);
        badge.setBorder(new LineBorder(Theme.BORDER_SOFT, 1, true));

        JLabel l1 = new JLabel(title, SwingConstants.CENTER);
        l1.setFont(Theme.FONT_BOLD);
        l1.setForeground(color);

        JLabel l2 = new JLabel(duration, SwingConstants.CENTER);
        l2.setFont(Theme.FONT_SMALL);
        l2.setForeground(Theme.TEXT_MUTED);

        JLabel l3 = new JLabel(price, SwingConstants.CENTER);
        l3.setFont(Theme.FONT_BOLD);
        l3.setForeground(Theme.TEXT_MAIN);

        badge.add(l1);
        badge.add(l2);
        badge.add(l3);
        return badge;
    }

    private void updateCalculatedPreview() {
        try {
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());
            String plan = (String) cmbPlan.getSelectedItem();
            LocalDate expiry;
            switch (plan) {
                case Membership.PLAN_QUARTERLY: expiry = start.plusMonths(3); break;
                case Membership.PLAN_HALF_YEARLY: expiry = start.plusMonths(6); break;
                case Membership.PLAN_YEARLY: expiry = start.plusYears(1); break;
                case Membership.PLAN_MONTHLY:
                default: expiry = start.plusMonths(1); break;
            }
            lblCalculatedExpiry.setText(expiry.toString());
            lblPlanPrice.setText(String.format("$%.2f", Membership.getDefaultPriceForPlan(plan)));
        } catch (Exception e) {
            lblCalculatedExpiry.setText("Invalid Date");
        }
    }

    private void onRenew() {
        try {
            String memberSelection = (String) cmbMember.getSelectedItem();
            if (memberSelection == null || memberSelection.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a member to renew.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String memberId = memberSelection.split(" - ")[0];
            String plan = (String) cmbPlan.getSelectedItem();
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim());

            controller.renewMembership(memberId, plan, start);
            JOptionPane.showMessageDialog(this, 
                    "Membership renewed successfully for " + memberSelection + "!\nPayment recorded in payment history (LinkedList).", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error renewing membership: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        ArrayList<Member> list;
        if (rbActive.isSelected()) {
            list = controller.getActiveMembers();
        } else if (rbExpiring.isSelected()) {
            list = controller.getExpiringSoonMembers();
        } else if (rbExpired.isSelected()) {
            list = controller.getExpiredMembers();
        } else {
            list = controller.getAllMembers();
        }
        renderTable(list);
    }

    private void renderTable(ArrayList<Member> list) {
        tableModel.setRowCount(0);
        for (Member m : list) {
            Membership ms = m.getMembership();
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    ms != null ? ms.getPlanName() : "None",
                    ms != null ? String.format("$%.2f", ms.getPrice()) : "$0.00",
                    ms != null && ms.getStartDate() != null ? ms.getStartDate() : "N/A",
                    ms != null && ms.getExpiryDate() != null ? ms.getExpiryDate() : "N/A",
                    ms != null ? ms.getDaysRemaining() + " days" : "0",
                    ms != null ? ms.getStatus() : "No Plan"
            });
        }
        lblCount.setText("Showing: " + list.size() + " records");
    }

    public void refreshData() {
        cmbMember.removeAllItems();
        for (Member m : controller.getAllMembers()) {
            cmbMember.addItem(m.getId() + " - " + m.getName());
        }
        updateCalculatedPreview();
        applyFilter();
    }
}
