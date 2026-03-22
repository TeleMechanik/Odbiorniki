package telemechan.dev.media.customtypes.jspane;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

public class JSPanel extends JPanel {
    private final BufferedImage canvas;
    private final JSGraphicsBridge bridge;
    private Context context;
    private String scriptCode;

    private Timer animationTimer;

    public JSPanel(File jsFile, int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.bridge = new JSGraphicsBridge();

        try {
            this.scriptCode = Files.readString(jsFile.toPath());

            // Setup the engine with permission to access our Bridge class
            IOAccess ioConfig = IOAccess.newBuilder()
                    .allowHostFileAccess(true)
                    .build();

            this.context = Context.newBuilder("js")
                    .hostClassLoader(Thread.currentThread().getContextClassLoader())
                    .allowHostAccess(HostAccess.ALL)
                    .allowIO(ioConfig)
                    .allowExperimentalOptions(true)
                    .out(System.out)  // Redirects standard JS output to Java System.out
                    .err(System.err)  // Redirects JS errors to Java System.err
                    .option("js.commonjs-require", "true")
                    .option("js.commonjs-require-cwd", jsFile.getParent())
                    .build();

            // Put the bridge into the JS global scope as 'gfx'
            // These must be set BEFORE startAnimation() is called
            context.getBindings("js").putMember("screenWidth", width);
            context.getBindings("js").putMember("screenHeight", height);
            context.getBindings("js").putMember("gfx", bridge);

            context.eval("js", scriptCode);

            bridge.setFilePath(jsFile.getParent());

            startAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAnimation() {
        Value updateFunction = context.getBindings("js").getMember("update");

        if (updateFunction != null && updateFunction.canExecute()) {
            animationTimer = new Timer(16, e -> {
                // Clear the image buffer
                Graphics2D g2d = canvas.createGraphics();
                g2d.setBackground(new Color(0,0,0,0));
                g2d.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                bridge.setG2d(g2d);

                // Execute the JS update function
                updateFunction.execute();

                g2d.dispose();
                repaint();
            });
            animationTimer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }
}