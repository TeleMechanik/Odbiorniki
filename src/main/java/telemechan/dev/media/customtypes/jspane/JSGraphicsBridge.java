package telemechan.dev.media.customtypes.jspane;

import lombok.Setter;
import org.graalvm.polyglot.HostAccess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JSGraphicsBridge {
    @Setter
    private Graphics2D g2d;
    @Setter
    private String filePath;

    @HostAccess.Export
    public void fillRect(int x, int y, int w, int h, String hexColor) {
        g2d.setColor(Color.decode(hexColor));
        g2d.fillRect(x, y, w, h);
    }

    @HostAccess.Export
    public void fillRectTexture(int x, int y, int w, int h, String file ) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath + "/" + file));
            g2d.drawImage(image, x, y, w, h, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @HostAccess.Export
    public void fillRectRotated(double x, double y, double w, double h, double radians, String hexColor) {
        var oldTransform = g2d.getTransform();

        g2d.translate(x, y);
        g2d.rotate(radians);

        g2d.setColor(Color.decode(hexColor));
        // Cast to int here so the bridge handles the "lossy" conversion
        g2d.fillRect((int)(-w/2), (int)(-h/2), (int)w, (int)h);

        g2d.setTransform(oldTransform);
    }

    @HostAccess.Export
    public void drawText(String text, double x, double y, double size, String hexColor) {
        g2d.setColor(Color.decode(hexColor));
        g2d.setFont(new Font("SansSerif", Font.BOLD, (int)size));
        g2d.drawString(text, (int)x, (int)y);
    }
}