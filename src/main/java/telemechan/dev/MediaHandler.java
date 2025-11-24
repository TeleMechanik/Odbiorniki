package telemechan.dev;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MediaHandler {
    private final MediaFile mediaFile;

    public MediaHandler(MediaFile file){
        this.mediaFile = file;
    }

    public JComponent getMediaComponent(int width, int height) {
        switch (mediaFile.getType()) {
            case IMAGE:
                try {
                    return new JLabel(new ImageIcon(ImageIO.read(new File(mediaFile.getFile().getAbsolutePath())).getScaledInstance(width, height, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case GIF:
                ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(mediaFile.getFile().getAbsolutePath()).getScaledInstance(width, height, Image.SCALE_DEFAULT));

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
}
