package telemechan.dev.settings;

import telemechan.dev.Main;
import telemechan.dev.media.MediaFile;
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
        setSize(700, 400);
        setLocationRelativeTo(Main.getMainFrame());
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setResizable(false);

        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        layout.setHgap(5);

        setLayout(layout);

        add(getFileComponent());
        add(getServerAddressComponent());
        add(cancelButton());
        add(confirmButton());
    }

    private JPanel getFileComponent(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select file to be displayed: "));

        JButton button = new JButton("Select file");

        button.addActionListener(_ -> {
            JFileChooser fileChooser = getFileChooser();

            int r = fileChooser.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                Main.updateMainFrame(new MediaFile(file, MediaHandler.getType(file)));
            }
        });

        panel.add(button);

        return panel;
    }
    private JPanel getServerAddressComponent(){
        JPanel panel = new JPanel();

        panel.add(new JLabel("Server address: "));

        JTextField addressInput = new JTextField(Main.getSettings().getServerAddress(), 20);
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
            Main.getSettings().saveData("serverAddress", settingsValues.get("serverAddress"));
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

