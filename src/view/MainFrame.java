package view;

import controller.ClubController;
import model.Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Application Window for the Sports Club Membership Management System.
 * Styled with Soft Pink + Cream visual theme.
 */
public class MainFrame extends JFrame {

    // View Identifiers
    public static final String VIEW_DASHBOARD = "VIEW_DASHBOARD";
    public static final String VIEW_MEMBERS = "VIEW_MEMBERS";
    public static final String VIEW_SPORTS = "VIEW_SPORTS";
    public static final String VIEW_COACHES = "VIEW_COACHES";
    public static final String VIEW_TRAINING = "VIEW_TRAINING";
    public static final String VIEW_MEMBERSHIP = "VIEW_MEMBERSHIP";
    public static final String VIEW_PAYMENTS = "VIEW_PAYMENTS";
    public static final String VIEW_ENROLLMENT = "VIEW_ENROLLMENT";
    public static final String VIEW_SEARCH_SORT = "VIEW_SEARCH_SORT";
    public static final String VIEW_TRAINING_HISTORY = "VIEW_TRAINING_HISTORY";
    public static final String VIEW_REPORT = "VIEW_REPORT";

    private final ClubController controller;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // View Panels
    private DashboardPanel dashboardPanel;
    private MemberPanel memberPanel;
    private SportsPanel sportsPanel;
    private CoachPanel coachPanel;
    private TrainingPanel trainingPanel;
    private MembershipPanel membershipPanel;
    private PaymentPanel paymentPanel;
    private EnrollmentPanel enrollmentPanel;
    private SearchSortPanel searchSortPanel;
    private TrainingHistoryPanel trainingHistoryPanel;
    private ReportPanel reportPanel;

    // Navigation buttons mapping for active state styling
    private final Map<String, JButton> navButtons = new HashMap<>();

    // Status bar labels
    private JLabel lblStatusModule;
    private JLabel lblStatusMembers;
    private JLabel lblStatusTime;

    public MainFrame(ClubController controller) {
        this.controller = controller;

        setTitle("Sports Club Membership Management System — Case Study 47");
        setSize(1220, 770);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initMenuBar();
        initComponents();

        // Show Dashboard initially
        showView(VIEW_DASHBOARD);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Theme.BG_CREAM);
        menuBar.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        menuBar.setFont(Theme.FONT_REGULAR);

        // 1. File Menu
        JMenu menuFile = createStyledMenu("File");
        JMenuItem itemSave = new JMenuItem("Save Club Data");
        JMenuItem itemLoad = new JMenuItem("Load Club Data");
        JMenuItem itemReset = new JMenuItem("Reset to Sample Data");
        JMenuItem itemExit = new JMenuItem("Exit");

        itemSave.setAccelerator(KeyStroke.getKeyStroke("control S"));
        itemSave.addActionListener(e -> onSaveData());
        itemLoad.addActionListener(e -> onLoadData());
        itemReset.addActionListener(e -> onResetData());

        itemExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Save and exit the application?", "Exit Application", JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.saveDefaultData();
                System.exit(0);
            } else if (confirm == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        });

        menuFile.add(itemSave);
        menuFile.add(itemLoad);
        menuFile.addSeparator();
        menuFile.add(itemReset);
        menuFile.addSeparator();
        menuFile.add(itemExit);

        // 2. Management Menu
        JMenu menuManage = createStyledMenu("Management");
        addMenuItem(menuManage, "Dashboard", VIEW_DASHBOARD);
        addMenuItem(menuManage, "Members Registration", VIEW_MEMBERS);
        addMenuItem(menuManage, "Sports Disciplines", VIEW_SPORTS);
        addMenuItem(menuManage, "Coaches & Trainers", VIEW_COACHES);
        addMenuItem(menuManage, "Training Schedule", VIEW_TRAINING);
        addMenuItem(menuManage, "Membership Plans", VIEW_MEMBERSHIP);
        addMenuItem(menuManage, "Payments & Fees", VIEW_PAYMENTS);

        // 3. Operations Menu
        JMenu menuOps = createStyledMenu("Operations");
        addMenuItem(menuOps, "Activity Enrollment (HashSet)", VIEW_ENROLLMENT);
        addMenuItem(menuOps, "Search & Sort Members", VIEW_SEARCH_SORT);
        addMenuItem(menuOps, "Training History (LinkedList)", VIEW_TRAINING_HISTORY);

        // 4. Reports Menu
        JMenu menuReports = createStyledMenu("Reports");
        addMenuItem(menuReports, "Official Club Report", VIEW_REPORT);
        JMenuItem itemAlerts = new JMenuItem("Membership Expiry Alerts");
        itemAlerts.addActionListener(e -> showExpiryAlertsDialog());
        menuReports.add(itemAlerts);

        // 5. Help Menu
        JMenu menuHelp = createStyledMenu("Help");
        JMenuItem itemChecklist = new JMenuItem("Academic Concept Checklist");
        JMenuItem itemAbout = new JMenuItem("About Case Study 47");

        itemChecklist.addActionListener(e -> showAcademicChecklist());
        itemAbout.addActionListener(e -> showAboutDialog());

        menuHelp.add(itemChecklist);
        menuHelp.add(itemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuManage);
        menuBar.add(menuOps);
        menuBar.add(menuReports);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(Theme.FONT_BOLD);
        menu.setForeground(Theme.TEXT_MAIN);
        return menu;
    }

    private void addMenuItem(JMenu menu, String label, String viewId) {
        JMenuItem item = new JMenuItem(label);
        item.setFont(Theme.FONT_REGULAR);
        item.setForeground(Theme.TEXT_MAIN);
        item.addActionListener(e -> showView(viewId));
        menu.add(item);
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.BG_CREAM);

        // Top App Header
        JPanel topBanner = new JPanel(new BorderLayout(10, 10));
        topBanner.setBackground(Theme.HEADER_BG);
        topBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_SOFT),
                new EmptyBorder(12, 20, 12, 20)
        ));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("🏆");
        lblLogo.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);

        JLabel lblAppName = new JLabel("SPORTS CLUB MEMBERSHIP MANAGEMENT SYSTEM");
        lblAppName.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblAppName.setForeground(Theme.TEXT_MAIN);

        JLabel lblAppSub = new JLabel("Case Study 47 • Java Swing MVC • Collections • File Persistence");
        lblAppSub.setFont(Theme.FONT_SMALL);
        lblAppSub.setForeground(Theme.TEXT_MUTED);

        titleBlock.add(lblAppName);
        titleBlock.add(lblAppSub);

        brandPanel.add(lblLogo);
        brandPanel.add(titleBlock);

        // Header Right: Date & Live Status
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerRight.setOpaque(false);

        JLabel lblDate = new JLabel("📅 " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")));
        lblDate.setFont(Theme.FONT_REGULAR);
        lblDate.setForeground(Theme.TEXT_MUTED);

        JButton btnQuickSave = new JButton("💾 Save");
        Theme.stylePrimaryButton(btnQuickSave);
        btnQuickSave.addActionListener(e -> onSaveData());

        headerRight.add(lblDate);
        headerRight.add(btnQuickSave);

        topBanner.add(brandPanel, BorderLayout.WEST);
        topBanner.add(headerRight, BorderLayout.EAST);
        rootPanel.add(topBanner, BorderLayout.NORTH);

        // Left Navigation Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 600));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER_LIGHT),
                new EmptyBorder(12, 10, 12, 10)
        ));

        sidebar.add(createNavButton("📊 Dashboard", VIEW_DASHBOARD));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("👤 Members (CRUD)", VIEW_MEMBERS));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("🏅 Sports (CRUD)", VIEW_SPORTS));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("🏋️ Coaches (CRUD)", VIEW_COACHES));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("📅 Training Schedule", VIEW_TRAINING));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("💳 Memberships", VIEW_MEMBERSHIP));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("💰 Payments", VIEW_PAYMENTS));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("📝 Enrollment (HashSet)", VIEW_ENROLLMENT));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("🔍 Search & Sort", VIEW_SEARCH_SORT));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("📜 Training History", VIEW_TRAINING_HISTORY));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(createNavButton("📈 Club Report", VIEW_REPORT));

        sidebar.add(Box.createVerticalGlue());

        rootPanel.add(sidebar, BorderLayout.WEST);

        // Center CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Theme.BG_CREAM);

        // Instantiate Views
        dashboardPanel = new DashboardPanel(controller, this);
        memberPanel = new MemberPanel(controller, this);
        sportsPanel = new SportsPanel(controller, this);
        coachPanel = new CoachPanel(controller, this);
        trainingPanel = new TrainingPanel(controller, this);
        membershipPanel = new MembershipPanel(controller, this);
        paymentPanel = new PaymentPanel(controller, this);
        enrollmentPanel = new EnrollmentPanel(controller, this);
        searchSortPanel = new SearchSortPanel(controller, this);
        trainingHistoryPanel = new TrainingHistoryPanel(controller, this);
        reportPanel = new ReportPanel(controller, this);

        cardPanel.add(dashboardPanel, VIEW_DASHBOARD);
        cardPanel.add(memberPanel, VIEW_MEMBERS);
        cardPanel.add(sportsPanel, VIEW_SPORTS);
        cardPanel.add(coachPanel, VIEW_COACHES);
        cardPanel.add(trainingPanel, VIEW_TRAINING);
        cardPanel.add(membershipPanel, VIEW_MEMBERSHIP);
        cardPanel.add(paymentPanel, VIEW_PAYMENTS);
        cardPanel.add(enrollmentPanel, VIEW_ENROLLMENT);
        cardPanel.add(searchSortPanel, VIEW_SEARCH_SORT);
        cardPanel.add(trainingHistoryPanel, VIEW_TRAINING_HISTORY);
        cardPanel.add(reportPanel, VIEW_REPORT);

        rootPanel.add(cardPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Theme.BG_CREAM);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_LIGHT),
                new EmptyBorder(6, 15, 6, 15)
        ));

        lblStatusModule = new JLabel("Module: Dashboard");
        lblStatusModule.setFont(Theme.FONT_BOLD);
        lblStatusModule.setForeground(Theme.BTN_PRIMARY);

        lblStatusMembers = new JLabel("Total Members: " + controller.getAllMembers().size());
        lblStatusMembers.setFont(Theme.FONT_SMALL);
        lblStatusMembers.setForeground(Theme.TEXT_MUTED);

        lblStatusTime = new JLabel("System Status: Ready • Local File Persistence Enabled");
        lblStatusTime.setFont(Theme.FONT_SMALL);
        lblStatusTime.setForeground(Theme.STATUS_ACTIVE);

        JPanel statusLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statusLeft.setOpaque(false);
        statusLeft.add(lblStatusModule);
        statusLeft.add(lblStatusMembers);

        statusBar.add(statusLeft, BorderLayout.WEST);
        statusBar.add(lblStatusTime, BorderLayout.EAST);
        rootPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JButton createNavButton(String text, String viewId) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(190, 36));
        btn.setPreferredSize(new Dimension(190, 36));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(Theme.FONT_REGULAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(Theme.NAV_DEFAULT_BG);
        btn.setForeground(Theme.NAV_DEFAULT_TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(240, 218, 222), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Subtle hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(Theme.NAV_SELECTED_BG)) {
                    btn.setBackground(Theme.NAV_HOVER_BG);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(Theme.NAV_SELECTED_BG)) {
                    btn.setBackground(Theme.NAV_DEFAULT_BG);
                }
            }
        });

        btn.addActionListener(e -> showView(viewId));
        navButtons.put(viewId, btn);
        return btn;
    }

    public void showView(String viewId) {
        cardLayout.show(cardPanel, viewId);

        // Update active navigation button colors
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            if (entry.getKey().equals(viewId)) {
                entry.getValue().setBackground(Theme.NAV_SELECTED_BG);
                entry.getValue().setForeground(Theme.NAV_SELECTED_TEXT);
                entry.getValue().setFont(Theme.FONT_BOLD);
                entry.getValue().setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 128, 142), 1, true),
                        new EmptyBorder(6, 12, 6, 12)
                ));
            } else {
                entry.getValue().setBackground(Theme.NAV_DEFAULT_BG);
                entry.getValue().setForeground(Theme.NAV_DEFAULT_TEXT);
                entry.getValue().setFont(Theme.FONT_REGULAR);
                entry.getValue().setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(240, 218, 222), 1, true),
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }
        }

        // Update status bar
        String moduleTitle = viewId.replace("VIEW_", "").replace("_", " ");
        lblStatusModule.setText("Module: " + moduleTitle);

        // Refresh currently visible panel
        refreshCurrentView(viewId);
    }

    private void refreshCurrentView(String viewId) {
        switch (viewId) {
            case VIEW_DASHBOARD: dashboardPanel.refreshData(); break;
            case VIEW_MEMBERS: memberPanel.refreshData(); break;
            case VIEW_SPORTS: sportsPanel.refreshData(); break;
            case VIEW_COACHES: coachPanel.refreshData(); break;
            case VIEW_TRAINING: trainingPanel.refreshData(); break;
            case VIEW_MEMBERSHIP: membershipPanel.refreshData(); break;
            case VIEW_PAYMENTS: paymentPanel.refreshData(); break;
            case VIEW_ENROLLMENT: enrollmentPanel.refreshData(); break;
            case VIEW_SEARCH_SORT: searchSortPanel.refreshData(); break;
            case VIEW_TRAINING_HISTORY: trainingHistoryPanel.refreshData(); break;
            case VIEW_REPORT: reportPanel.refreshData(); break;
        }
    }

    public void notifyDataChanged() {
        // Auto-save upon changes
        controller.saveDefaultData();

        // Refresh all panels
        dashboardPanel.refreshData();
        memberPanel.refreshData();
        sportsPanel.refreshData();
        coachPanel.refreshData();
        trainingPanel.refreshData();
        membershipPanel.refreshData();
        paymentPanel.refreshData();
        enrollmentPanel.refreshData();
        searchSortPanel.refreshData();
        trainingHistoryPanel.refreshData();
        reportPanel.refreshData();

        lblStatusMembers.setText("Total Members: " + controller.getAllMembers().size());
        lblStatusTime.setText("System Status: Auto-Saved at " + java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void onSaveData() {
        boolean saved = controller.saveToFile(new File("data/sports_club_data.dat"));
        if (saved) {
            JOptionPane.showMessageDialog(this, "Club data successfully saved to 'data/sports_club_data.dat'!", "Data Saved", JOptionPane.INFORMATION_MESSAGE);
            lblStatusTime.setText("Data Saved Successfully (" + java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save club data.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLoadData() {
        JFileChooser chooser = new JFileChooser(new File("data"));
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            boolean loaded = controller.loadFromFile(chooser.getSelectedFile());
            if (loaded) {
                notifyDataChanged();
                JOptionPane.showMessageDialog(this, "Data successfully loaded from file!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Unable to load data from selected file.", "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onResetData() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Reset all application data back to default sample records?\nThis will replace any custom changes.",
                "Confirm Data Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.resetToDefaultData();
            notifyDataChanged();
            JOptionPane.showMessageDialog(this, "Application data reset to default sample dataset.", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showExpiryAlertsDialog() {
        ArrayList<Member> expired = controller.getExpiredMembers();
        ArrayList<Member> expiring = controller.getExpiringSoonMembers();

        StringBuilder sb = new StringBuilder();
        sb.append("MEMBERSHIP EXPIRY ALERTS SUMMARY\n");
        sb.append("====================================================\n\n");

        sb.append("1. EXPIRED MEMBERSHIPS (Immediate Renewal Required):\n");
        if (expired.isEmpty()) {
            sb.append("   (None)\n");
        } else {
            for (Member m : expired) {
                sb.append(String.format("   • [%s] %-16s | Expired on: %s\n", m.getId(), m.getName(), m.getMembershipExpiryDate()));
            }
        }
        sb.append("\n");

        sb.append("2. EXPIRING SOON (Within Next 30 Days):\n");
        if (expiring.isEmpty()) {
            sb.append("   (None)\n");
        } else {
            for (Member m : expiring) {
                sb.append(String.format("   • [%s] %-16s | Expires on: %s (%d days remaining)\n",
                        m.getId(), m.getName(), m.getMembershipExpiryDate(), m.getMembership().getDaysRemaining()));
            }
        }

        JTextArea ta = new JTextArea(sb.toString(), 14, 45);
        ta.setFont(Theme.FONT_MONO);
        ta.setBackground(Theme.BG_CREAM);
        ta.setForeground(Theme.TEXT_MAIN);
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Membership Expiry Alerts", JOptionPane.WARNING_MESSAGE);
    }

    private void showAcademicChecklist() {
        String info = "CASE STUDY 47 — ACADEMIC CONCEPT MAPPING CHECKLIST\n" +
                "=================================================================\n" +
                "✓ Classes & Objects: Member, Sport, Coach, Training, Membership, Payment\n" +
                "✓ Constructors: Chained & parameterized constructors across all entities\n" +
                "✓ Array: Fixed training slots (Training.FIXED_SLOTS) & Sport.CATEGORIES\n" +
                "✓ ArrayList: Member, Coach, and Sport dynamic collections\n" +
                "✓ LinkedList: Training History (completed) & Payment History\n" +
                "✓ HashMap: O(1) Fast Member ID -> Member object lookup\n" +
                "✓ TreeMap: Members sorted chronologically by Membership Expiry Date\n" +
                "✓ HashSet: Unique sport activity enrollments preventing duplicates\n" +
                "✓ OOP Encapsulation: Private fields, valid getters/setters in all models\n" +
                "✓ OOP Inheritance: Person base class -> Member & Coach sub-classes\n" +
                "✓ OOP Polymorphism: Abstract methods getRole() & getDetails() overridden\n" +
                "✓ CRUD Operations: Complete Create, Read, Update, Delete for Members, Sports, Coaches\n" +
                "✓ Searching: Multi-criteria searching (ID, Name, Sport, Coach)\n" +
                "✓ Sorting: Java Comparator sorting by Name & Expiry date\n" +
                "✓ Exception Handling: InvalidMemberException, InvalidTrainingException\n" +
                "✓ Validation: Email format, 10-digit phone, age range, date constraints\n" +
                "✓ Java Swing GUI: Full desktop interface with Soft Pink + Cream Theme\n" +
                "✓ File Persistence: Object serialization to 'data/sports_club_data.dat'\n" +
                "✓ Dashboard: Metrics cards, alerts banner, quick actions\n" +
                "✓ Membership Alerts: Automatic detection of expired & soon-expiring members\n" +
                "✓ Reset Functionality: Clean reset to sample dataset";

        JTextArea ta = new JTextArea(info, 20, 52);
        ta.setFont(Theme.FONT_MONO);
        ta.setBackground(Theme.BG_CREAM);
        ta.setForeground(Theme.TEXT_MAIN);
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Academic Concept Verification", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String msg = "Sports Club Membership Management System\n" +
                "Academic Case Study 47 — Computer Science & Engineering\n\n" +
                "Designed and developed using standard Java (Java 8 - 26),\n" +
                "Java Swing GUI, Collections Framework, and File Persistence.\n\n" +
                "Redesigned with Soft Pink + Cream Aesthetic Theme.\n" +
                "Architecture: Model-View-Controller (MVC)";
        JOptionPane.showMessageDialog(this, msg, "About Application", JOptionPane.INFORMATION_MESSAGE);
    }
}
