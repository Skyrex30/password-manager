package ui;

import main.PasswordManagerWindow;
import util.SoundEffectPlayer;

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;

public class AddPasswordWindow {
    private PasswordManagerWindow managerWindow;

    public AddPasswordWindow(PasswordManagerWindow managerWindow) {
        this.managerWindow = managerWindow;

        JFrame frame = new JFrame("Add New Password");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JLabel websiteLabel = new JLabel("Website:");
        JTextField websiteField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        // Label for displaying password strength
        JLabel strengthLabel = new JLabel("Password Strength: ");
        strengthLabel.setForeground(Color.BLUE);

        JButton generateButton = new JButton("Generate Password");
        JButton saveButton = new JButton("Save");

        // Generate Button
        generateButton.addActionListener(e -> {
            SoundEffectPlayer.playSound("res/mouse-click.wav");
            String generatedPassword = generateSecurePassword(12); // Generates a 12 character password
            passwordField.setText(generatedPassword);
            strengthLabel.setText("Password Strength: " + evaluatePasswordStrength(generatedPassword));
        });

        // Document listener for displaying the password strength
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateStrengthLabel();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateStrengthLabel();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateStrengthLabel();
            }

            private void updateStrengthLabel() {
                String password = new String(passwordField.getPassword());
                strengthLabel.setText("Password Strength: " + evaluatePasswordStrength(password));
            }
        });

        // Save Button
        saveButton.addActionListener(e -> {
            SoundEffectPlayer.playSound("res/mouse-click.wav");
            String website = websiteField.getText();
            String password = new String(passwordField.getPassword());
            if (!website.isEmpty() && !password.isEmpty()) {
                managerWindow.addPassword(website, password);
                JOptionPane.showMessageDialog(frame, "Password saved.");
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Please fill in both fields.");
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.add(websiteLabel);
        panel.add(websiteField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(strengthLabel);
        panel.add(generateButton);
        panel.add(new JLabel());
        panel.add(saveButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private String generateSecurePassword(int length) {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }

    private String evaluatePasswordStrength(String password) {
        int length = password.length();
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if ("!@#$%^&*()-_=+<>?".indexOf(ch) >= 0) {
                hasSpecial = true;
            }
        }

        int score = 0;
        if (length >= 8) score++;
        if (hasUppercase) score++;
        if (hasLowercase) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;

        switch (score) {
            case 5:
                return "Strong";
            case 4:
                return "Good";
            case 3:
                return "Moderate";
            default:
                return "Weak";
        }
    }
}
