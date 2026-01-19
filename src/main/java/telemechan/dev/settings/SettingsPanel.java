package telemechan.dev.settings;

import telemechan.dev.Main;
import telemechan.dev.media.MediaContainer;
import telemechan.dev.media.MediaHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SettingsPanel extends JDialog {
    Map<String, String> settingsValues = new HashMap<>();

    public SettingsPanel() {
        setTitle("Ustawienia");
        setLocationRelativeTo(Main.getMainFrame());
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setResizable(false);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        add(getFileComponent());
        add(getServerAddressComponent());
        add(getTokenComponent());
        add(getButtonsPanel());

        pack();
        setLocationRelativeTo(Main.getMainFrame());
    }

    private JPanel getFileComponent(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Select file to be displayed: "));

        JButton button = new JButton("Select file");

        button.addActionListener(_ -> {
            JFileChooser fileChooser = getFileChooser();

            int r = fileChooser.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                Main.updateMainFrame(new MediaContainer(file, MediaHandler.getType(file), -1));
            }
        });

        panel.add(button);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) panel.getPreferredSize().getHeight()));

        return panel;
    }
    private JPanel getServerAddressComponent(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Server address: "));

        JTextField addressInput = new JTextField(Main.getSettings().getServerAddress(), 20);
        settingsValues.put("serverAddress", Main.getSettings().getServerAddress());
        addressInput.setHorizontalAlignment(SwingConstants.RIGHT);

        addressInput.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateAddress();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateAddress();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateAddress();
            }

            private void updateAddress() {
                settingsValues.put("serverAddress", addressInput.getText());
            }
        });

        panel.add(addressInput);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) panel.getPreferredSize().getHeight()));

        return panel;
    }

    private JPanel getTokenComponent(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Owner Token: "));

        JTextField tokenInput = new JTextField(Main.getSettings().getToken(), 20);
        settingsValues.put("token", Main.getSettings().getToken());
        tokenInput.setHorizontalAlignment(SwingConstants.RIGHT);

        tokenInput.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateAddress();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateAddress();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateAddress();
            }

            private void updateAddress() {
                settingsValues.put("token", tokenInput.getText());
            }
        });

        panel.add(tokenInput);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) panel.getPreferredSize().getHeight()));

        return panel;
    }

    private JPanel getButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(cancelButton());
        panel.add(confirmButton());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) panel.getPreferredSize().getHeight()));
        return panel;
    }

    private JButton cancelButton(){
        JButton button = new JButton("Cancel");
        button.addActionListener(_ -> this.setVisible(false));
        return button;
    }

    private JButton confirmButton(){
        JButton button = new JButton("Confirm");
        button.addActionListener(_ -> {
            Main.getSettings()
                    .saveData("serverAddress", settingsValues.get("serverAddress"))
                    .saveData("token", settingsValues.get("token"))
                    .reloadConfig();

            Main.reconnectToServer();
            this.setVisible(false);
        });
        return button;
    }

    private JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"
        );
        fileChooser.setFileFilter(imageFilter);

        FileNameExtensionFilter videoFilter = new FileNameExtensionFilter(
                "Video files", "mp4", "avi", "mkv"
        );

        fileChooser.addChoosableFileFilter(videoFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        return fileChooser;
    }
}

