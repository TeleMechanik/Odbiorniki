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
import telemechan.dev.servercon.WebsocketReceiver;
import telemechan.dev.servercon.PacketHandler;
import telemechan.dev.servercon.PacketParser;
import telemechan.dev.settings.ClientSettings;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;


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

    @Getter
    static ClientSettings settings;

    @Getter
    static File dataFolder;

    public static void main(String[] args) {
        dataFolder = getAppDataFolder();
        File configFile = new File(dataFolder, "config.json");

        if (!configFile.exists()) {
            ClientSettings.saveDefault(configFile);
        }

        settings = new ClientSettings(configFile);

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
        String url = "ws://"+ settings.getServerAddress() +"/";
        try {
            session = container.connectToServer(WebsocketReceiver.class, URI.create(url));
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

    public static File getAppDataFolder() {
        Properties props = new Properties();
        try (InputStream in = Main.class.getResourceAsStream("/config.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String appName = props.getProperty("app.name");

        String os = System.getProperty("os.name").toLowerCase();
        String path;

        if (os.contains("win")) {
            path = System.getenv("APPDATA");
            if (path == null) path = System.getProperty("user.home") + "\\AppData\\Roaming";
        } else { // Linux/Unix
            path = System.getenv("XDG_DATA_HOME");
            if (path == null) path = System.getProperty("user.home") + "/.local/share";
        }

        File folder = new File(path, appName);
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }
}
