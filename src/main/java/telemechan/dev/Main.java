package telemechan.dev;

import lombok.Getter;
import lombok.Setter;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    @Getter
    static JFrame mainFrame;

    @Getter @Setter
    static MediaHandler mediaHandler;

    static int width, height;
    static JComponent currentComponent; // keep track of displayed component

    public static void main(String[] args) {
        try {
            // Example: Windows style
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();

        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();
        mainFrame.setBounds(center.x - width / 2, center.y - height / 2, width, height);

        mainFrame.setLayout(new BorderLayout()); // important

        mediaHandler = new MediaHandler(new MediaFile(new File("C:/Users/stasd/Downloads/1459929400_454.jpg"), MediaType.IMAGE));
        currentComponent = mediaHandler.getMediaComponent(width, height);
        mainFrame.add(currentComponent, BorderLayout.CENTER);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setUndecorated(true);
        mainFrame.setVisible(true);

        KeyboardHandler.registerBinds();
    }

    public static void updateMainFrame(MediaFile mediaFile){
        // remove the currently displayed component
        if (currentComponent != null) {
            if(currentComponent instanceof EmbeddedMediaPlayerComponent component){
                component.mediaPlayer().controls().stop();
            }
            mainFrame.getContentPane().remove(currentComponent);
        }

        // create new media handler and component
        mediaHandler = new MediaHandler(mediaFile);
        currentComponent = mediaHandler.getMediaComponent(width, height);
        mainFrame.add(currentComponent, BorderLayout.CENTER);

        mainFrame.revalidate();
        mainFrame.repaint();

        if(currentComponent instanceof EmbeddedMediaPlayerComponent component){
            component.mediaPlayer().media().play(
                    mediaFile.getFile().getAbsolutePath(),
                    ":input-repeat=65535",   // infinite loop
                    ":no-audio"              // mute
            );

            component.setFocusable(false);
            component.setRequestFocusEnabled(false);
        }

        mainFrame.requestFocusInWindow();
    }
}
