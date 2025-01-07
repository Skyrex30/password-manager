package main;

import util.SoundEffectPlayer;

import javax.swing.*;
import java.awt.*;

public class MasterPasswordWindow {
    private static String MASTER_PASSWORD = "asd";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Enter Master Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);

        JLabel passwordLabel = new JLabel("Master Password:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setEchoChar('*');

        JButton unlockButton = new JButton("Unlock");
        unlockButton.addActionListener(e -> {
            SoundEffectPlayer.playSound("res/mouse-click.wav");
            if (new String(passwordField.getPassword()).equals(MASTER_PASSWORD)) {
                frame.dispose();
                openPasswordManager();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect password.");
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(unlockButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void openPasswordManager() {
        new PasswordManagerWindow();
    }

    public static String getMasterPassword() {
        return MASTER_PASSWORD;
    }
}
