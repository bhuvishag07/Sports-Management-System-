package view;

import controller.ClubController;
import model.Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Dashboard Panel displaying key club statistics, membership alerts, and quick actions.
 * Redesigned with Soft Pink + Cream palette.
 */
public class DashboardPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    // Stat card labels
    private JLabel lblTotalMembers;
    private JLabel lblTotalSports;
    private JLabel lblTotalCoaches;
    private JLabel lblActiveMembers;
    private JLabel lblExpiringSoon;
    private JLabel lblExpiredMembers;
    private JLabel lblTotalRevenue;

    // Alert Panel
    private JPanel alertPanel;
    private DefaultListModel<String> alertListModel;
    private JList<String> alertList;

    public DashboardPanel(ClubController controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(Theme.BG_CREAM);

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Top Header
        JPanel headerPanel = Theme.createHeader(
                "Sports Club Overview Dashboard",
                "Live club statistics, membership expiry alerts, and quick management controls"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 1. Stat Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(2, 4, 14, 14));
        cardsGrid.setOpaque(false);
        cardsGrid.setMaximumSize(new Dimension(2000, 220));

        lblTotalMembers = new JLabel("0", SwingConstants.CENTER);
        lblTotalSports = new JLabel("0", SwingConstants.CENTER);
        lblTotalCoaches = new JLabel("0", SwingConstants.CENTER);
        lblActiveMembers = new JLabel("0", SwingConstants.CENTER);
        lblExpiringSoon = new JLabel("0", SwingConstants.CENTER);
        lblExpiredMembers = new JLabel("0", SwingConstants.CENTER);
        lblTotalRevenue = new JLabel("$0.00", SwingConstants.CENTER);

        cardsGrid.add(createStatCard("Total Members", lblTotalMembers, Theme.BTN_PRIMARY, "👥"));
        cardsGrid.add(createStatCard("Total Sports", lblTotalSports, Theme.STATUS_ACTIVE, "🏅"));
        cardsGrid.add(createStatCard("Total Coaches", lblTotalCoaches, Theme.BTN_ACTION, "🏋️"));
        cardsGrid.add(createStatCard("Active Plans", lblActiveMembers, Theme.STATUS_ACTIVE, "✅"));
        cardsGrid.add(createStatCard("Expiring (<30d)", lblExpiringSoon, Theme.STATUS_EXPIRING, "⚠️"));
        cardsGrid.add(createStatCard("Expired Plans", lblExpiredMembers, Theme.STATUS_EXPIRED, "❌"));
        cardsGrid.add(createStatCard("Total Revenue", lblTotalRevenue, Theme.BTN_PRIMARY, "💰"));

        // Academic info card
        JPanel academicCard = new JPanel(new BorderLayout(5, 5));
        academicCard.setBackground(Theme.CARD_BG);
        academicCard.setBorder(Theme.createCardBorder());
        JLabel lblAcaTitle = new JLabel("Case Study 47", SwingConstants.CENTER);
        lblAcaTitle.setFont(Theme.FONT_HEADING);
        lblAcaTitle.setForeground(Theme.TEXT_MAIN);
        JLabel lblAcaSub = new JLabel("<html><center>Soft Pink + Cream Theme<br>Java Collections & Swing<br>Array • List • Map • Set</center></html>", SwingConstants.CENTER);
        lblAcaSub.setFont(Theme.FONT_SMALL);
        lblAcaSub.setForeground(Theme.TEXT_MUTED);
        academicCard.add(lblAcaTitle, BorderLayout.NORTH);
        academicCard.add(lblAcaSub, BorderLayout.CENTER);
        cardsGrid.add(academicCard);

        centerPanel.add(cardsGrid);
        centerPanel.add(Box.createVerticalStrut(18));

        // 2. Expiry Alerts Panel
        alertPanel = new JPanel(new BorderLayout(10, 10));
        alertPanel.setBackground(Theme.ALERT_BG);
        alertPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.ALERT_BORDER, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        alertPanel.setMaximumSize(new Dimension(2000, 180));

        JLabel alertTitle = new JLabel("MEMBERSHIP EXPIRY ALERTS");
        alertTitle.setFont(Theme.FONT_HEADING);
        alertTitle.setForeground(Theme.TEXT_MAIN);

        alertListModel = new DefaultListModel<>();
        alertList = new JList<>(alertListModel);
        alertList.setFont(Theme.FONT_MONO);
        alertList.setBackground(Color.WHITE);
        alertList.setForeground(Theme.TEXT_MAIN);
        alertList.setVisibleRowCount(4);
        JScrollPane alertScroll = new JScrollPane(alertList);
        alertScroll.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));

        JButton btnRenewAlert = new JButton("Renew Selected Member");
        Theme.styleSecondaryButton(btnRenewAlert);
        btnRenewAlert.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_MEMBERSHIP));

        JPanel alertBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        alertBottom.setOpaque(false);
        alertBottom.add(btnRenewAlert);

        alertPanel.add(alertTitle, BorderLayout.NORTH);
        alertPanel.add(alertScroll, BorderLayout.CENTER);
        alertPanel.add(alertBottom, BorderLayout.SOUTH);

        centerPanel.add(alertPanel);
        centerPanel.add(Box.createVerticalStrut(18));

        // 3. Quick Actions
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 5, 12, 12));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setMaximumSize(new Dimension(2000, 46));

        JButton btnAddMember = createActionButton("➕ Register Member");
        btnAddMember.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_MEMBERS));

        JButton btnSchedule = createActionButton("📅 Schedule Training");
        btnSchedule.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_TRAINING));

        JButton btnPayment = createActionButton("💰 Record Payment");
        btnPayment.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_PAYMENTS));

        JButton btnEnroll = createActionButton("📝 Enroll in Sport");
        btnEnroll.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_ENROLLMENT));

        JButton btnReport = createActionButton("📈 Generate Report");
        btnReport.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_REPORT));

        quickActionsPanel.add(btnAddMember);
        quickActionsPanel.add(btnSchedule);
        quickActionsPanel.add(btnPayment);
        quickActionsPanel.add(btnEnroll);
        quickActionsPanel.add(btnReport);

        centerPanel.add(quickActionsPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor, String icon) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_BOLD);
        lblTitle.setForeground(Theme.TEXT_MUTED);

        topPanel.add(lblIcon, BorderLayout.WEST);
        topPanel.add(lblTitle, BorderLayout.CENTER);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        // Bottom accent line
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(100, 3));
        line.setBackground(accentColor);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(line, BorderLayout.SOUTH);

        return card;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        Theme.stylePrimaryButton(btn);
        return btn;
    }

    public void refreshData() {
        lblTotalMembers.setText(String.valueOf(controller.getAllMembers().size()));
        lblTotalSports.setText(String.valueOf(controller.getAllSports().size()));
        lblTotalCoaches.setText(String.valueOf(controller.getAllCoaches().size()));
        lblActiveMembers.setText(String.valueOf(controller.getActiveMembers().size()));
        lblExpiringSoon.setText(String.valueOf(controller.getExpiringSoonMembers().size()));
        lblExpiredMembers.setText(String.valueOf(controller.getExpiredMembers().size()));
        lblTotalRevenue.setText(String.format("$%.2f", controller.getTotalPaymentsAmount()));

        // Refresh Alerts list
        alertListModel.clear();
        ArrayList<Member> expired = controller.getExpiredMembers();
        for (Member m : expired) {
            alertListModel.addElement(String.format("• EXPIRED: %-15s (ID: %s) - Plan: %s expired on %s",
                    m.getName(), m.getId(), m.getMembership().getPlanName(), m.getMembershipExpiryDate()));
        }

        ArrayList<Member> expiring = controller.getExpiringSoonMembers();
        for (Member m : expiring) {
            alertListModel.addElement(String.format("• EXPIRING SOON (%dd): %-15s (ID: %s) - Plan: %s expires on %s",
                    m.getMembership().getDaysRemaining(), m.getName(), m.getId(), m.getMembership().getPlanName(), m.getMembershipExpiryDate()));
        }

        if (alertListModel.isEmpty()) {
            alertListModel.addElement("✓ All member subscriptions are currently active and healthy. No immediate alerts.");
            alertPanel.setBackground(new Color(246, 252, 248));
            alertPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(210, 235, 218), 1, true),
                    new EmptyBorder(12, 16, 12, 16)
            ));
        } else {
            alertPanel.setBackground(Theme.ALERT_BG);
            alertPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.ALERT_BORDER, 1, true),
                    new EmptyBorder(12, 16, 12, 16)
            ));
        }
    }
}
