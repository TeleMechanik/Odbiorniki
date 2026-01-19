package telemechan.dev.media;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MediaHandler {
    private final MediaContainer mediaContainer;

    public static HashMap<String, MediaContainer> preloadedMedia = new HashMap<>();

    public MediaHandler(MediaContainer file){
        this.mediaContainer = file;
    }

    public JComponent getMediaComponent(int width, int height) {
        switch (mediaContainer.getType()) {
            case IMAGE:
                try {
                    return new JLabel(new ImageIcon(ImageIO.read(new File(mediaContainer.getFile().getAbsolutePath())).getScaledInstance(width, height, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case GIF:
                ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(mediaContainer.getFile().getAbsolutePath()).getScaledInstance(width, height, Image.SCALE_DEFAULT));

                return new JLabel(icon);
            case VIDEO:
                EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
                mediaPlayerComponent.setPreferredSize(new Dimension(width, height));
                mediaPlayerComponent.setFocusable(false);
                return mediaPlayerComponent;
            default:
                return new JLabel("Unsupported media");
        }
    }

    public static MediaType getType(File file){
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

        return type;
    }
}
