package view;

import controller.ClubController;
import model.Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Member Search and Sorting Panel.
 * Demonstrates:
 * - Searching by Member ID, Member Name, Sport, and Coach (Case-insensitive).
 * - Sorting by Name using Java Comparator.
 * - Sorting by Membership Expiry using Java Comparator and TreeMap structure.
 * Styled with Soft Pink + Cream theme.
 */
public class SearchSortPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JTextField txtSearchQuery;
    private JComboBox<String> cmbSearchCriteria;
    private JButton btnSearch;
    private JButton btnReset;

    private JButton btnSortNameAsc;
    private JButton btnSortNameDesc;
    private JButton btnSortExpiryAsc;
    private JButton btnSortExpiryDesc;
    private JButton btnTreeMapDetails;

    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    public SearchSortPanel(ClubController controller, MainFrame mainFrame) {
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
                "Member Search & Sorting Engine",
                "Fast multi-attribute search (ID, Name, Sport, Coach) and sorting via Comparators & TreeMap"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);

        // Control Panel
        JPanel controlsCard = new JPanel(new GridLayout(2, 1, 10, 10));
        controlsCard.setBackground(Theme.CARD_BG);
        controlsCard.setBorder(Theme.createCardBorder());

        // 1. Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);

        JLabel lblSearch = new JLabel("Search Query:");
        lblSearch.setFont(Theme.FONT_BOLD);
        lblSearch.setForeground(Theme.TEXT_MAIN);

        txtSearchQuery = new JTextField(20);
        Theme.styleTextField(txtSearchQuery);
        txtSearchQuery.addActionListener(e -> onSearch());

        cmbSearchCriteria = new JComboBox<>(new String[]{"All", "ID", "Name", "Sport", "Coach"});
        Theme.styleComboBox(cmbSearchCriteria);

        btnSearch = new JButton("🔍 Search");
        Theme.stylePrimaryButton(btnSearch);
        btnSearch.addActionListener(e -> onSearch());

        btnReset = new JButton("🔄 Reset / All");
        Theme.styleNeutralButton(btnReset);
        btnReset.addActionListener(e -> {
            txtSearchQuery.setText("");
            cmbSearchCriteria.setSelectedIndex(0);
            refreshData();
        });

        searchBar.add(lblSearch);
        searchBar.add(txtSearchQuery);
        searchBar.add(new JLabel("By:"));
        searchBar.add(cmbSearchCriteria);
        searchBar.add(btnSearch);
        searchBar.add(btnReset);

        // 2. Sorting Bar
        JPanel sortBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sortBar.setOpaque(false);

        JLabel lblSort = new JLabel("Sorting Options:");
        lblSort.setFont(Theme.FONT_BOLD);
        lblSort.setForeground(Theme.TEXT_MAIN);

        btnSortNameAsc = new JButton("Name (A → Z)");
        Theme.styleSecondaryButton(btnSortNameAsc);
        btnSortNameAsc.addActionListener(e -> onSortName(true));

        btnSortNameDesc = new JButton("Name (Z → A)");
        Theme.styleSecondaryButton(btnSortNameDesc);
        btnSortNameDesc.addActionListener(e -> onSortName(false));

        btnSortExpiryAsc = new JButton("Expiry (Soonest)");
        Theme.styleSecondaryButton(btnSortExpiryAsc);
        btnSortExpiryAsc.addActionListener(e -> onSortExpiry(true));

        btnSortExpiryDesc = new JButton("Expiry (Furthest)");
        Theme.styleSecondaryButton(btnSortExpiryDesc);
        btnSortExpiryDesc.addActionListener(e -> onSortExpiry(false));

        btnTreeMapDetails = new JButton("🌳 View TreeMap Hierarchy");
        Theme.styleCustomButton(btnTreeMapDetails, Theme.BTN_ACTION, Color.WHITE);
        btnTreeMapDetails.addActionListener(e -> showTreeMapDialog());

        sortBar.add(lblSort);
        sortBar.add(btnSortNameAsc);
        sortBar.add(btnSortNameDesc);
        sortBar.add(btnSortExpiryAsc);
        sortBar.add(btnSortExpiryDesc);
        sortBar.add(btnTreeMapDetails);

        controlsCard.add(searchBar);
        controlsCard.add(sortBar);

        centerPanel.add(controlsCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = new JPanel(new BorderLayout(8, 8));
        tableCard.setBackground(Theme.CARD_BG);
        tableCard.setBorder(Theme.createCardBorder());

        lblStatus = new JLabel("Displaying: 0 members");
        lblStatus.setFont(Theme.FONT_BOLD);
        lblStatus.setForeground(Theme.TEXT_MUTED);
        tableCard.add(lblStatus, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Age", "Gender", "Contact", "Email", "Plan", "Expiry Date", "Status", "Enrolled Sports"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        Theme.styleTable(resultsTable);

        // Soft color renderer for status
        resultsTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
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

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void onSearch() {
        String query = txtSearchQuery.getText().trim();
        String criteria = (String) cmbSearchCriteria.getSelectedItem();
        ArrayList<Member> results = controller.searchMembers(query, criteria);
        renderTable(results);
        lblStatus.setText(String.format("Found %d matching member(s) for query '%s' [Field: %s]", results.size(), query, criteria));
    }

    private void onSortName(boolean ascending) {
        ArrayList<Member> sorted = controller.sortMembersByName(ascending);
        renderTable(sorted);
        lblStatus.setText("Members sorted by Name " + (ascending ? "(A → Z)" : "(Z → A)"));
    }

    private void onSortExpiry(boolean ascending) {
        ArrayList<Member> sorted = controller.sortMembersByExpiry(ascending);
        renderTable(sorted);
        lblStatus.setText("Members sorted by Membership Expiry " + (ascending ? "(Soonest First)" : "(Furthest First)"));
    }

    private void showTreeMapDialog() {
        TreeMap<LocalDate, ArrayList<Member>> treeMap = controller.getMembersByExpiryTreeMap();
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("   TREEMAP DEMONSTRATION: MEMBERS SORTED BY EXPIRY DATE  \n");
        sb.append("   Underlying Collection: TreeMap<LocalDate, List<Member>> \n");
        sb.append("=========================================================\n\n");

        if (treeMap.isEmpty()) {
            sb.append("No member records found in TreeMap.\n");
        } else {
            for (Map.Entry<LocalDate, ArrayList<Member>> entry : treeMap.entrySet()) {
                sb.append(String.format("📅 KEY (Expiry Date): %s\n", entry.getKey()));
                for (Member m : entry.getValue()) {
                    sb.append(String.format("   ↳ [%s] %-18s | Plan: %-12s | Status: %s\n",
                            m.getId(), m.getName(), m.getMembership().getPlanName(), m.getMembershipStatus()));
                }
                sb.append("---------------------------------------------------------\n");
            }
        }

        JTextArea textArea = new JTextArea(sb.toString(), 16, 50);
        textArea.setFont(Theme.FONT_MONO);
        textArea.setBackground(Theme.BG_CREAM);
        textArea.setForeground(Theme.TEXT_MAIN);
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));

        JOptionPane.showMessageDialog(this, scroll, "TreeMap Structure: Expiry Hierarchy", JOptionPane.PLAIN_MESSAGE);
    }

    private void renderTable(ArrayList<Member> list) {
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
    }

    public void refreshData() {
        ArrayList<Member> all = controller.getAllMembers();
        renderTable(all);
        lblStatus.setText("Displaying all " + all.size() + " members (Ready to search & sort)");
    }
}
