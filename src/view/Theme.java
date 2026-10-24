package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Centralized Design System & Theme for the Soft Pink + Cream visual redesign.
 * Defines cohesive color constants, typography, borders, and component stylers.
 */
public class Theme {

    // ==========================================
    // COLOR PALETTE: SOFT PINK + CREAM
    // ==========================================

    // Backgrounds
    public static final Color BG_CREAM = new Color(253, 250, 246);        // Warm cream / ivory main background
    public static final Color CARD_BG = new Color(255, 255, 255);         // Crisp warm white
    public static final Color CARD_WARM = new Color(255, 253, 250);       // Subtle light cream for cards
    public static final Color SIDEBAR_BG = new Color(248, 228, 232);      // Soft pastel blush pink
    public static final Color HEADER_BG = new Color(242, 214, 220);       // Soft dusty rose header
    public static final Color SUBTLE_FILL = new Color(250, 242, 244);     // Very light pastel wash

    // Borders
    public static final Color BORDER_LIGHT = new Color(236, 220, 224);    // Very light pink/beige border
    public static final Color BORDER_SOFT = new Color(224, 202, 208);     // Defined soft pink border

    // Buttons
    public static final Color BTN_PRIMARY = new Color(209, 133, 146);     // Muted dusty pink
    public static final Color BTN_PRIMARY_TEXT = Color.WHITE;
    public static final Color BTN_SECONDARY = new Color(242, 216, 222);   // Light blush pink
    public static final Color BTN_SECONDARY_TEXT = new Color(74, 52, 57);
    public static final Color BTN_ACTION = new Color(196, 120, 134);      // Slightly deeper dusty pink
    public static final Color BTN_NEUTRAL = new Color(228, 212, 215);     // Muted warm neutral
    public static final Color BTN_NEUTRAL_TEXT = new Color(85, 65, 70);

    // Sidebar Navigation
    public static final Color NAV_DEFAULT_BG = new Color(248, 228, 232);
    public static final Color NAV_DEFAULT_TEXT = new Color(80, 56, 62);
    public static final Color NAV_SELECTED_BG = new Color(218, 148, 161); // Slightly darker pastel pink
    public static final Color NAV_SELECTED_TEXT = Color.WHITE;
    public static final Color NAV_HOVER_BG = new Color(236, 196, 204);

    // Tables
    public static final Color TABLE_HEADER_BG = new Color(238, 208, 215); // Soft dusty pink table header
    public static final Color TABLE_HEADER_TEXT = new Color(68, 46, 52);  // Dark warm brown
    public static final Color TABLE_ROW_ALT = new Color(254, 250, 251);   // Lightest alternating row
    public static final Color TABLE_SELECTION_BG = new Color(242, 202, 210); // Soft pink selection
    public static final Color TABLE_SELECTION_TEXT = new Color(55, 35, 40);
    public static final Color TABLE_GRID = new Color(240, 228, 231);

    // Typography Colors
    public static final Color TEXT_MAIN = new Color(64, 46, 50);          // Dark brown / charcoal
    public static final Color TEXT_MUTED = new Color(125, 105, 110);      // Muted dusty brown/grey
    public static final Color TEXT_SUBTLE = new Color(165, 145, 150);     // Very soft brown/grey

    // Soft Status Accents (Non-saturated, gentle colors)
    public static final Color STATUS_ACTIVE = new Color(90, 135, 102);    // Muted sage green
    public static final Color STATUS_EXPIRING = new Color(196, 122, 75);  // Warm soft terracotta
    public static final Color STATUS_EXPIRED = new Color(188, 88, 100);   // Muted dusty crimson
    public static final Color STAT_CARD_ACCENT = new Color(198, 125, 138);

    // Alerts
    public static final Color ALERT_BG = new Color(254, 246, 242);        // Warm ivory blush
    public static final Color ALERT_BORDER = new Color(240, 212, 202);

    // ==========================================
    // TYPOGRAPHY FONTS
    // ==========================================
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);

    // ==========================================
    // BORDER FACTORIES
    // ==========================================
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(14, 15, 14, 15)
        );
    }

    public static Border createLineBorder() {
        return new LineBorder(BORDER_LIGHT, 1, true);
    }

    // ==========================================
    // COMPONENT STYLING HELPERS
    // ==========================================

    /**
     * Styles a primary button (Muted dusty pink).
     */
    public static void stylePrimaryButton(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(BTN_PRIMARY);
        btn.setForeground(BTN_PRIMARY_TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(195, 120, 133), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(btn, BTN_PRIMARY, new Color(196, 118, 132));
    }

    /**
     * Styles a secondary button (Light blush pink).
     */
    public static void styleSecondaryButton(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(BTN_SECONDARY);
        btn.setForeground(BTN_SECONDARY_TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SOFT, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(btn, BTN_SECONDARY, new Color(234, 204, 211));
    }

    /**
     * Styles a neutral button (Muted warm neutral/beige).
     */
    public static void styleNeutralButton(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(BTN_NEUTRAL);
        btn.setForeground(BTN_NEUTRAL_TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(btn, BTN_NEUTRAL, new Color(218, 200, 204));
    }

    /**
     * Styles a button with custom soft colors and hover effect.
     */
    public static void styleCustomButton(JButton btn, Color normalBg, Color textFg) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(normalBg);
        btn.setForeground(textFg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SOFT, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color hoverBg = new Color(
                Math.max(0, normalBg.getRed() - 15),
                Math.max(0, normalBg.getGreen() - 15),
                Math.max(0, normalBg.getBlue() - 15)
        );
        addHoverEffect(btn, normalBg, hoverBg);
    }

    private static void addHoverEffect(JButton btn, Color normalBg, Color hoverBg) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(normalBg);
                }
            }
        });
    }

    /**
     * Styles standard text fields with clean cream/white background and soft pink/beige border.
     */
    public static void styleTextField(JTextField tf) {
        tf.setFont(FONT_REGULAR);
        tf.setBackground(Color.WHITE);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(TEXT_MAIN);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
    }

    /**
     * Styles standard combo boxes.
     */
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_REGULAR);
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_MAIN);
    }

    /**
     * Styles JTable components to match the Soft Pink + Cream theme.
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(25);
        table.setFont(FONT_REGULAR);
        table.setForeground(TEXT_MAIN);
        table.setBackground(Color.WHITE);
        table.setGridColor(TABLE_GRID);
        table.setShowGrid(true);
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setSelectionForeground(TABLE_SELECTION_TEXT);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_TEXT);
        header.setBorder(new LineBorder(BORDER_SOFT, 1));
        header.setPreferredSize(new Dimension(header.getWidth(), 28));

        // Alternating row renderer with theme colors
        DefaultTableCellRenderer baseRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT);
                    c.setForeground(TEXT_MAIN);
                } else {
                    c.setBackground(TABLE_SELECTION_BG);
                    c.setForeground(TABLE_SELECTION_TEXT);
                }
                setBorder(new EmptyBorder(0, 6, 0, 6));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(baseRenderer);
        }
    }

    /**
     * Creates a standard panel header block with title and subtitle in theme typography.
     */
    public static JPanel createHeader(String title, String subtitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(FONT_SUBTITLE);
        lblSubtitle.setForeground(TEXT_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        return headerPanel;
    }
}
