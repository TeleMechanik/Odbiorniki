package telemechan.dev;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class SettingsPanel extends JDialog {

    public SettingsPanel() {
        setSize(300, 200);
        setLocationRelativeTo(Main.getMainFrame());
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setLayout(new FlowLayout());

        JButton button = new JButton("test");

        button.addActionListener(a -> {
            JFileChooser fileChooser = getFileChooser();

            int r = fileChooser.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                String name = file.getName();
                int dot = name.lastIndexOf('.');
                String ext = (dot == -1) ? "" : name.substring(dot + 1);

                MediaType type;
                switch (ext.toLowerCase()) {
                    case  "jpg", "jpeg", "png" -> type = MediaType.IMAGE;
                    case  "gif" -> type = MediaType.GIF;
                    case  "mp4", "mov" -> type = MediaType.VIDEO;
                    default -> type = MediaType.UNKNOWN;
                }

                Main.updateMainFrame(new MediaFile(file, type));
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

