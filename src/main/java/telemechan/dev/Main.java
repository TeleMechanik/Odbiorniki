package telemechan.dev;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import telemechan.dev.media.MediaContainer;
import telemechan.dev.media.MediaHandler;
import telemechan.dev.media.MediaType;
import telemechan.dev.servercon.WebsocketReceiver;
import telemechan.dev.servercon.PacketHandler;
import telemechan.dev.servercon.PacketParser;
import telemechan.dev.settings.ClientSettings;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;


public class Main {
    @Getter
    static JFrame mainFrame;

    @Getter @Setter
    static MediaHandler mediaHandler;

    @Getter @Setter
    static MediaContainer currentFile;

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

    static Scheduler scheduler;

    public static void main(String[] args) {
        dataFolder = getAppDataFolder();
        File configFile = new File(dataFolder, "config.json");

        if (!configFile.exists()) {
            ClientSettings.saveDefault(configFile);
        }

        settings = new ClientSettings(configFile);

        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame();
        mainFrame.setUndecorated(true);


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();
        mainFrame.setSize(width, height);
        mainFrame.setLocation(0, 0);

        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().setBackground(Color.BLACK);

        mediaHandler = new MediaHandler(new MediaContainer(generatePlaceholder(), MediaType.IMAGE, -1));
        currentComponent = mediaHandler.getMediaComponent(width, height);
        mainFrame.add(currentComponent, BorderLayout.CENTER);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor invisibleCursor = toolkit.createCustomCursor(image, new Point(0, 0), "invisible");

        mainFrame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e){
                super.windowGainedFocus(e);

                mainFrame.setCursor(invisibleCursor);
            }
            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                mainFrame.setCursor(Cursor.getDefaultCursor());
            }
        });

        GraphicsDevice d = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice();
        boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");

        if (!isLinux && d.isFullScreenSupported()) {
            mainFrame.setUndecorated(true);
            mainFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            mainFrame.setVisible(true);
        } else {
            mainFrame.setUndecorated(true);
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setVisible(true);
        }

        KeyboardHandler.registerBinds();

        parser = new PacketParser();
        parser.registerHandler(new PacketHandler());

        reconnectToServer();

        scheduler = new Scheduler();
        scheduler.startSchedules();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.stopScheduler();
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void updateMainFrame(MediaContainer mediaContainer){
        if (currentComponent != null) {
            if(currentComponent instanceof EmbeddedMediaPlayerComponent component){
                component.mediaPlayer().controls().stop();
            }
            mainFrame.getContentPane().remove(currentComponent);
        }
        currentFile = mediaContainer;
        mediaHandler = new MediaHandler(mediaContainer);
        currentComponent = mediaHandler.getMediaComponent(width, height);

        mainFrame.add(currentComponent, BorderLayout.CENTER);

        mainFrame.revalidate();
        mainFrame.repaint();

        if(currentComponent instanceof EmbeddedMediaPlayerComponent component){
            component.mediaPlayer().media().play(
                    mediaContainer.getFile().getAbsolutePath(),
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

    public static File generatePlaceholder() {
        int width = mainFrame.getWidth();
        int height = mainFrame.getHeight();

        File f;
        if(settings.getDefaultDisplay() != null && settings.getDefaultDisplay().exists()){
            f = settings.getDefaultDisplay();
        }else {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            g.dispose();

            try {
                f = File.createTempFile("placeholder_", ".png");
                ImageIO.write(img, "png", f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return f;
    }

    @SneakyThrows
    public static void reconnectToServer(){
        if(session != null && session.isOpen()) session.close();
        session = null;

        if(settings.getUuid().isBlank() && settings.getToken().isBlank())return;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxBinaryMessageBufferSize(1024 * 1024);
        container.setDefaultMaxTextMessageBufferSize(1024 * 1024);

        String wssUrl;
        String serverUrl = settings.getServerAddress();
        String lastChar = serverUrl.charAt(serverUrl.length() - 1) == '/' ? "ws" : "/ws";
        if(serverUrl.contains("https://")) {
            wssUrl = "wss://" + serverUrl.replace("https://", "") + lastChar;
        }else {
            wssUrl = "ws://" + serverUrl.replace("http://", "") + lastChar;
        }

        try {
            session = container.connectToServer(WebsocketReceiver.class, URI.create(wssUrl));
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }
}
