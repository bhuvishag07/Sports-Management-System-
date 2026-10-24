import controller.ClubController;
import view.MainFrame;
import view.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Main Application Entry Point for Case Study 47.
 * Launches the Sports Club Membership Management System with Soft Pink + Cream theme.
 */
public class Main {
    public static void main(String[] args) {
        // Configure Clean Modern Look & Feel (Nimbus or System)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Apply theme color hints to dialogs
        try {
            UIManager.put("OptionPane.background", Theme.BG_CREAM);
            UIManager.put("Panel.background", Theme.BG_CREAM);
            UIManager.put("OptionPane.messageForeground", Theme.TEXT_MAIN);
        } catch (Exception ignored) {
        }

        // Initialize Controller and Launch GUI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            ClubController controller = new ClubController();
            MainFrame mainFrame = new MainFrame(controller);
            mainFrame.setVisible(true);
        });
    }
}
