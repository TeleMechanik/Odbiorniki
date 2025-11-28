package telemechan.dev;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import lombok.Getter;
import lombok.Setter;
import telemechan.dev.media.MediaFile;
import telemechan.dev.media.MediaHandler;
import telemechan.dev.media.MediaType;
import telemechan.dev.servercon.Endpoint;
import telemechan.dev.servercon.PacketHandler;
import telemechan.dev.servercon.PacketParser;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;


public class Main {
    @Getter
    static JFrame mainFrame;

    @Getter @Setter
    static MediaHandler mediaHandler;

    static int width, height;
    static JComponent currentComponent;

    @Getter
    static Session session;

    @Getter
    static PacketParser parser;

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

        mainFrame.setLayout(new BorderLayout());

        mediaHandler = new MediaHandler(new MediaFile(new File("C:/Users/stasd/Downloads/1459929400_454.jpg"), MediaType.IMAGE));
        currentComponent = mediaHandler.getMediaComponent(width, height);
        mainFrame.add(currentComponent, BorderLayout.CENTER);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setUndecorated(true);
        mainFrame.setVisible(true);

        KeyboardHandler.registerBinds();

        parser = new PacketParser();
        parser.registerHandler(new PacketHandler());

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxBinaryMessageBufferSize(1024 * 1024);
        container.setDefaultMaxTextMessageBufferSize(1024 * 1024);
        String url = "ws://se01.creperus.top:10210/";
        try {
            session = container.connectToServer(Endpoint.class, URI.create(url));
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateMainFrame(MediaFile mediaFile){
        if (currentComponent != null) {
            if(currentComponent instanceof EmbeddedMediaPlayerComponent component){
                component.mediaPlayer().controls().stop();
            }
            mainFrame.getContentPane().remove(currentComponent);
        }

        mediaHandler = new MediaHandler(mediaFile);
        currentComponent = mediaHandler.getMediaComponent(width, height);
        mainFrame.add(currentComponent, BorderLayout.CENTER);

        mainFrame.revalidate();
        mainFrame.repaint();

        if(currentComponent instanceof EmbeddedMediaPlayerComponent component){
            component.mediaPlayer().media().play(
                    mediaFile.getFile().getAbsolutePath(),
                    ":input-repeat=65535",
                    ":no-audio"
            );

            component.setFocusable(false);
            component.setRequestFocusEnabled(false);
        }

        mainFrame.requestFocusInWindow();
    }
}
