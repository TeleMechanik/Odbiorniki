package telemechan.dev.media;

import telemechan.dev.media.customtypes.jspane.JSPanel;
import telemechan.dev.media.customtypes.jspane.JSPluginLoader;
import telemechan.dev.media.customtypes.pdf.AutoScrollingPDFComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
                    BufferedImage image = ImageIO.read(mediaContainer.getFile());

                    float scale = Math.min(
                            (float) width / image.getWidth(),
                            (float) height / image.getHeight()
                    );

                    int newW = Math.round(image.getWidth() * scale);
                    int newH = Math.round(image.getHeight() * scale);

                    Image scaled = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

                    JLabel label = new JLabel(new ImageIcon(scaled));
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setVerticalAlignment(JLabel.CENTER);

                    return label;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case GIF:
                Image img = Toolkit.getDefaultToolkit()
                        .getImage(mediaContainer.getFile().getAbsolutePath());

                ImageIcon base = new ImageIcon(img);

                int imgW = base.getIconWidth();
                int imgH = base.getIconHeight();

                float scale = Math.min(
                        (float) width / imgW,
                        (float) height / imgH
                );

                int newW = Math.round(imgW * scale);
                int newH = Math.round(imgH * scale);

                Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

                JLabel label = new JLabel(new ImageIcon(scaled));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);

                return label;

            case VIDEO:
                EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
                mediaPlayerComponent.setPreferredSize(new Dimension(width, height));
                mediaPlayerComponent.setFocusable(false);
                return mediaPlayerComponent;
            case JS:
                return new JSPanel(mediaContainer.getFile(), width, height);
            case JSZIP:
                try {
                    // Extract and get the entry point
                    File mainJs = JSPluginLoader.preparePlugin(mediaContainer.getFile());

                    // Return your component, passing the main.js file
                    // The component will use mainJs.getParent() for its IO base directory
                    return new JSPanel(mainJs, width, height);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new JLabel("Error loading JS Plugin: " + e.getMessage());
                }

            case PDF:
                return new AutoScrollingPDFComponent(mediaContainer.getFile(), width, height);
            default:
                return new JLabel("Unsupported media");
        }
    }

    public static MediaType getType(File file){
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        String ext;

        if (dot != -1) {
            String[] nameParts = name.split("\\.");
            ext = nameParts[nameParts.length - 1];
        }else{
            ext = "";
        }

        MediaType type;
        switch (ext.toLowerCase()) {
            case  "jpg", "jpeg", "png" -> type = MediaType.IMAGE;
            case  "gif" -> type = MediaType.GIF;
            case  "pdf" -> type = MediaType.PDF;
            case  "mp4", "mov" -> type = MediaType.VIDEO;
            case  "js" -> type = MediaType.JS;
            case  "zip" -> {
                if(name.contains("plugin")){
                    type = MediaType.JSZIP;
                }else{
                    type = MediaType.UNKNOWN;
                }
            }
            default -> type = MediaType.UNKNOWN;
        }

        return type;
    }
}
