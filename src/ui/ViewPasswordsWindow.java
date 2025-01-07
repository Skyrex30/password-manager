package ui;

import model.PasswordEntry;
import table.PasswordTableModel;
import util.Encryptor;
import util.SoundEffectPlayer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewPasswordsWindow {
    JFrame frame;
    public ViewPasswordsWindow(List<PasswordEntry> passwordList) {
        frame = new JFrame("View All Passwords");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/res/background.png");
        backgroundPanel.setLayout(new BorderLayout());

        JTextField searchField = new JTextField(20);
        JTable passwordTable = new JTable(new PasswordTableModel(passwordList));

        // Search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String query = searchField.getText().toLowerCase();
                PasswordTableModel model = (PasswordTableModel) passwordTable.getModel();
                model.filter(query);
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        topPanel.add(searchLabel);
        topPanel.add(searchField);

        JButton saveButton = new JButton("Save passwords");
        saveButton.addActionListener(e -> {
            SoundEffectPlayer.playSound("res/mouse-click.wav");
            SavePasswords(passwordList);
        });

        JButton loadButton = new JButton("Load passwords from file");
        loadButton.addActionListener(e -> {
            SoundEffectPlayer.playSound("res/mouse-click.wav");
            List<PasswordEntry> loadedPasswords = LoadPasswords();
            updatePasswordTable(loadedPasswords, passwordTable);
        });

        JButton checkReusedButton = new JButton("Check reused passwords");
        checkReusedButton.addActionListener(e -> {
           SoundEffectPlayer.playSound("res/mouse-click.wav");
           auditReusedPasswords(passwordList);
        });
        topPanel.add(saveButton);
        topPanel.add(loadButton);
        topPanel.add(checkReusedButton);

        JScrollPane scrollPane = new JScrollPane(passwordTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(backgroundPanel);
        frame.setVisible(true);
    }

    // View reused passwords
    private void auditReusedPasswords(List<PasswordEntry> passwordList) {
        List<String> reusedPasswords = passwordList.stream()
                .collect(Collectors.groupingBy(PasswordEntry::getPassword, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!reusedPasswords.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Reused passwords detected: " + reusedPasswords);
        } else {
            JOptionPane.showMessageDialog(frame, "No reused passwords detected.");
        }
    }



    private void SavePasswords(List<PasswordEntry> passwordList) {
        String masterPassword = JOptionPane.showInputDialog(null, "Enter Master Password to Encrypt:");

        if (masterPassword == null || masterPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Master password is required to save passwords.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Encrypted Passwords");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                StringBuilder dataToEncrypt = new StringBuilder();

                for (PasswordEntry entry : passwordList) {
                    dataToEncrypt.append(entry.getWebsite()).append(",")
                            .append(entry.getPassword()).append("\n");
                }

                String encryptedData = Encryptor.encrypt(dataToEncrypt.toString().trim(), masterPassword);

                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(encryptedData);
                }

                JOptionPane.showMessageDialog(null, "Passwords saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error saving passwords: " + ex.getMessage());
            }
        }
    }


    private List<PasswordEntry> LoadPasswords() {
        String masterPassword = JOptionPane.showInputDialog(null, "Enter Master Password to Decrypt:");

        if (masterPassword == null || masterPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Master password is required to load passwords.");
            return null;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Encrypted Password File");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToLoad = fileChooser.getSelectedFile();
                StringBuilder encryptedData = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        encryptedData.append(line);
                    }
                }

                String decryptedData = Encryptor.decrypt(encryptedData.toString(), masterPassword);
                List<PasswordEntry> loadedPasswords = parseDecryptedData(decryptedData);

                JOptionPane.showMessageDialog(null, "Passwords loaded successfully!");
                return loadedPasswords;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error loading passwords: " + ex.getMessage());
            }
        }
        return null;
    }

    private List<PasswordEntry> parseDecryptedData(String decryptedData) {
        List<PasswordEntry> passwordList = new ArrayList<>();
        String[] lines = decryptedData.split("\n");

        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts.length == 2) {
                String website = parts[0].trim();
                String password = parts[1].trim();
                passwordList.add(new PasswordEntry(website, password));
            }
        }

        return passwordList;
    }


    private void updatePasswordTable(List<PasswordEntry> passwordList, JTable passwordTable) {
        PasswordTableModel model = (PasswordTableModel) passwordTable.getModel();
        model.setPasswordList(passwordList);
        model.fireTableDataChanged();
    }
}
