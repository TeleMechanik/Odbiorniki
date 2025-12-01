package telemechan.dev.settings;

import telemechan.dev.Main;
import telemechan.dev.media.MediaFile;
import telemechan.dev.media.MediaHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class SettingsPanel extends JDialog {

    public SettingsPanel() {
        setTitle("Ustawienia");
        setSize(300, 200);
        setLocationRelativeTo(Main.getMainFrame());
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setLayout(new FlowLayout());

        JButton button = new JButton("test");

        button.addActionListener(_ -> {
            JFileChooser fileChooser = getFileChooser();

            int r = fileChooser.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                Main.updateMainFrame(new MediaFile(file, MediaHandler.getType(file)));
            }
        });

        add(button);
    }

    private static JFileChooser getFileChooser() {
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

