package main;

import model.PasswordEntry;
import ui.AddPasswordWindow;
import ui.BackgroundPanel;
import ui.ViewPasswordsWindow;
import util.SoundEffectPlayer;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PasswordManagerWindow {
    private static final int TIMEOUT_MS = 120000; // 2 min
    private Timer inactivityTimer;

    private Clip backgroundMusic;

    private List<PasswordEntry> passwordList = new ArrayList<>();
    private JFrame frame;

    public PasswordManagerWindow() {
        frame = new JFrame("Password Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/res/background.png");
        backgroundPanel.setLayout(new BorderLayout());

        JButton addButton = new JButton("Add New Password");
        JButton viewButton = new JButton("View All Passwords");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        backgroundPanel.add(buttonPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> {
            SoundEffectPlayer.playSound("res/mouse-click.wav");
            openAddPasswordWindow();
        });
        viewButton.addActionListener(e -> {
            SoundEffectPlayer.playSound("res/mouse-click.wav");
            openViewPasswordsWindow();
        });

        setupInactivityTimer(frame);

        frame.add(backgroundPanel);
        frame.setVisible(true);

        startBackgroundMusic("res/music.wav");
    }

    private void startBackgroundMusic(String musicFilePath) {
        try {
            File musicFile = new File(musicFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);

            // Volume control
            FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = -20.0f; // 20 decibel
            volumeControl.setValue(volume);

            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }

    private void openAddPasswordWindow() {
        new AddPasswordWindow(this);
    }

    private void openViewPasswordsWindow() {
        new ViewPasswordsWindow(this.passwordList);
    }

    public void addPassword(String website, String password) {
        passwordList.add(new PasswordEntry(website, password));
    }

    private void setupInactivityTimer(JFrame frame) {
        inactivityTimer = new Timer(TIMEOUT_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lockScreen(frame);
            }
        });
        inactivityTimer.setRepeats(false);

        // Add listeners to reset the timer on activity
        MouseAdapter activityListener = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                resetTimer();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                resetTimer();
            }
        };

        frame.addMouseMotionListener(activityListener);
        frame.addMouseListener(activityListener);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                resetTimer();
            }
        });

        resetTimer();
    }

    private void resetTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.restart();
        }
    }

    private void lockScreen(JFrame frame) {
        stopBackgroundMusic();
        JOptionPane.showMessageDialog(frame, "Session timed out due to inactivity.");
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                window.dispose();
            }
        }
        //SwingUtilities.invokeLater(MasterPasswordWindow::new);
    }
}