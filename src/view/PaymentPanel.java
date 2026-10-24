package view;

import controller.ClubController;
import model.Member;
import model.Membership;
import model.Payment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedList;

/**
 * Payment Management Panel.
 * Demonstrates LinkedList payment transaction history.
 * Styled with Soft Pink + Cream theme.
 */
public class PaymentPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JTextField txtPaymentId;
    private JComboBox<String> cmbMember;
    private JComboBox<String> cmbPlan;
    private JTextField txtAmount;
    private JTextField txtDate;
    private JComboBox<String> cmbMethod;
    private JComboBox<String> cmbStatus;

    private JButton btnRecord;
    private JButton btnClear;

    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalRevenue;
    private JLabel lblCount;
    private JTextField txtFilter;

    public PaymentPanel(ClubController controller, MainFrame mainFrame) {
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
                "Payment Management & Transaction History",
                "Record subscription fee transactions and inspect payment history maintained via LinkedList"
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

        JLabel formHeading = new JLabel("Record New Payment");
        formHeading.setFont(Theme.FONT_HEADING);
        formHeading.setForeground(Theme.TEXT_MAIN);
        formCard.add(formHeading, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 8, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        txtPaymentId = new JTextField();
        cmbMember = new JComboBox<>();
        cmbPlan = new JComboBox<>(Membership.AVAILABLE_PLANS);
        txtAmount = new JTextField("50.00");
        txtDate = new JTextField(LocalDate.now().toString());
        cmbMethod = new JComboBox<>(Payment.PAYMENT_METHODS);
        cmbStatus = new JComboBox<>(new String[]{"Paid", "Refunded"});

        Theme.styleTextField(txtPaymentId);
        Theme.styleComboBox(cmbMember);
        Theme.styleComboBox(cmbPlan);
        Theme.styleTextField(txtAmount);
        Theme.styleTextField(txtDate);
        Theme.styleComboBox(cmbMethod);
        Theme.styleComboBox(cmbStatus);

        cmbPlan.addActionListener(e -> {
            String plan = (String) cmbPlan.getSelectedItem();
            txtAmount.setText(String.format("%.2f", Membership.getDefaultPriceForPlan(plan)));
        });

        addFormField(fieldsPanel, "Payment ID:*", txtPaymentId);
        addFormField(fieldsPanel, "Member:*", cmbMember);
        addFormField(fieldsPanel, "Plan:*", cmbPlan);
        addFormField(fieldsPanel, "Amount ($):*", txtAmount);
        addFormField(fieldsPanel, "Date (YYYY-MM-DD):*", txtDate);
        addFormField(fieldsPanel, "Payment Method:", cmbMethod);
        addFormField(fieldsPanel, "Status:", cmbStatus);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        buttonsPanel.setOpaque(false);

        btnRecord = new JButton("💰 Record Payment");
        Theme.stylePrimaryButton(btnRecord);
        btnRecord.addActionListener(e -> onRecordPayment());

        btnClear = new JButton("🔄 Clear Form");
        Theme.styleNeutralButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        buttonsPanel.add(btnRecord);
        buttonsPanel.add(btnClear);
        formCard.add(buttonsPanel, BorderLayout.SOUTH);

        contentPanel.add(formCard, BorderLayout.WEST);

        // --- TABLE CARD ---
        JPanel tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        // Top info bar
        JPanel topInfo = new JPanel(new BorderLayout(8, 8));
        topInfo.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Filter History:");
        lblSearch.setFont(Theme.FONT_BOLD);
        lblSearch.setForeground(Theme.TEXT_MUTED);
        searchPanel.add(lblSearch);

        txtFilter = new JTextField(15);
        Theme.styleTextField(txtFilter);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterPayments(txtFilter.getText().trim());
            }
        });
        searchPanel.add(txtFilter);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        lblCount = new JLabel("Transactions: 0");
        lblCount.setFont(Theme.FONT_REGULAR);
        lblCount.setForeground(Theme.TEXT_MUTED);

        lblTotalRevenue = new JLabel("Total Collected: $0.00");
        lblTotalRevenue.setFont(Theme.FONT_BOLD);
        lblTotalRevenue.setForeground(Theme.BTN_PRIMARY);

        summaryPanel.add(lblCount);
        summaryPanel.add(lblTotalRevenue);

        topInfo.add(searchPanel, BorderLayout.WEST);
        topInfo.add(summaryPanel, BorderLayout.EAST);
        tableCard.add(topInfo, BorderLayout.NORTH);

        String[] columnNames = {"Payment ID", "Member ID", "Member Name", "Plan", "Amount", "Date", "Payment Method", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        Theme.styleTable(paymentTable);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
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
        int maxId = 1000;
        for (Payment p : controller.getPaymentHistory()) {
            try {
                if (p.getPaymentId().startsWith("PAY-")) {
                    int num = Integer.parseInt(p.getPaymentId().substring(4));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {
            }
        }
        txtPaymentId.setText("PAY-" + (maxId + 1));
    }

    private void onRecordPayment() {
        try {
            String payId = txtPaymentId.getText().trim();
            String memberSel = (String) cmbMember.getSelectedItem();
            if (memberSel == null || memberSel.isEmpty()) {
                throw new Exception("Please select a member.");
            }
            String memberId = memberSel.split(" - ")[0];
            String memberName = memberSel.contains(" - ") ? memberSel.split(" - ")[1] : memberSel;
            String plan = (String) cmbPlan.getSelectedItem();
            double amount = Double.parseDouble(txtAmount.getText().trim());
            LocalDate date = LocalDate.parse(txtDate.getText().trim());
            String method = (String) cmbMethod.getSelectedItem();
            String status = (String) cmbStatus.getSelectedItem();

            Payment payment = new Payment(payId, memberId, memberName, amount, date, plan, method, status);
            controller.recordPayment(payment);
            JOptionPane.showMessageDialog(this, "Payment recorded successfully in Payment History (LinkedList)!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.notifyDataChanged();
            clearForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtAmount.setText("50.00");
        txtDate.setText(LocalDate.now().toString());
        if (cmbMember.getItemCount() > 0) cmbMember.setSelectedIndex(0);
        cmbPlan.setSelectedIndex(0);
        cmbMethod.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        generateNextId();
    }

    private void filterPayments(String query) {
        LinkedList<Payment> all = controller.getPaymentHistory();
        if (query.isEmpty()) {
            renderTable(all);
            return;
        }
        String q = query.toLowerCase();
        LinkedList<Payment> filtered = new LinkedList<>();
        for (Payment p : all) {
            if (p.getPaymentId().toLowerCase().contains(q) ||
                p.getMemberId().toLowerCase().contains(q) ||
                p.getMemberName().toLowerCase().contains(q) ||
                p.getPaymentMethod().toLowerCase().contains(q) ||
                p.getMembershipPlan().toLowerCase().contains(q)) {
                filtered.add(p);
            }
        }
        renderTable(filtered);
    }

    private void renderTable(LinkedList<Payment> list) {
        tableModel.setRowCount(0);
        for (Payment p : list) {
            tableModel.addRow(new Object[]{
                    p.getPaymentId(),
                    p.getMemberId(),
                    p.getMemberName(),
                    p.getMembershipPlan(),
                    String.format("$%.2f", p.getAmount()),
                    p.getPaymentDate(),
                    p.getPaymentMethod(),
                    p.getPaymentStatus()
            });
        }
        lblCount.setText("Transactions: " + list.size());
        lblTotalRevenue.setText(String.format("Total Collected: $%.2f", controller.getTotalPaymentsAmount()));
    }

    public void refreshData() {
        cmbMember.removeAllItems();
        for (Member m : controller.getAllMembers()) {
            cmbMember.addItem(m.getId() + " - " + m.getName());
        }
        renderTable(controller.getPaymentHistory());
    }
}
