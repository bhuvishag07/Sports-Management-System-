package view;

import controller.ClubController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Club Report Panel.
 * Generates an executive summary of members, sports, coaches, revenue, expiries, and training sessions.
 * Styled with Soft Pink + Cream theme.
 */
public class ReportPanel extends JPanel {

    private final ClubController controller;
    private final MainFrame mainFrame;

    private JTextArea txtReport;

    public ReportPanel(ClubController controller, MainFrame mainFrame) {
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
                "Official Club Summary Report",
                "Consolidated academic audit report: metrics, TreeMap expiry breakdown, and financial records"
        );
        add(headerPanel, BorderLayout.NORTH);

        // Center Text Card
        JPanel reportCard = new JPanel(new BorderLayout(10, 10));
        reportCard.setBackground(Theme.CARD_BG);
        reportCard.setBorder(Theme.createCardBorder());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnRefresh = new JButton("🔄 Refresh Report");
        Theme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> refreshData());

        JButton btnCopy = new JButton("📋 Copy to Clipboard");
        Theme.styleSecondaryButton(btnCopy);
        btnCopy.addActionListener(e -> copyToClipboard());

        JButton btnExport = new JButton("💾 Export to File");
        Theme.styleCustomButton(btnExport, Theme.STATUS_ACTIVE, Color.WHITE);
        btnExport.addActionListener(e -> exportReport());

        toolbar.add(btnRefresh);
        toolbar.add(btnCopy);
        toolbar.add(btnExport);
        reportCard.add(toolbar, BorderLayout.NORTH);

        txtReport = new JTextArea();
        txtReport.setFont(Theme.FONT_MONO);
        txtReport.setEditable(false);
        txtReport.setBackground(Theme.CARD_WARM);
        txtReport.setForeground(Theme.TEXT_MAIN);
        txtReport.setCaretColor(Theme.TEXT_MAIN);
        txtReport.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(txtReport);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_LIGHT, 1));
        reportCard.add(scrollPane, BorderLayout.CENTER);

        add(reportCard, BorderLayout.CENTER);
    }

    private void copyToClipboard() {
        String report = txtReport.getText();
        StringSelection selection = new StringSelection(report);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        JOptionPane.showMessageDialog(this, "Report text copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Club_Report_" + System.currentTimeMillis() + ".txt"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(txtReport.getText());
                JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + file.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshData() {
        txtReport.setText(controller.generateClubReport());
        txtReport.setCaretPosition(0);
    }
}
