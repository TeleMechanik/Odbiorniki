package telemechan.dev.media.customtypes.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AutoScrollingPDFComponent extends JLabel {
    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage = 0;
    private final int targetW;
    private final int targetH;
    private Timer loopTimer;

    public AutoScrollingPDFComponent(File file, int width, int height) {
        this.targetW = width;
        this.targetH = height;
        this.setHorizontalAlignment(JLabel.CENTER);

        try {
            this.document = Loader.loadPDF(file);
            this.renderer = new PDFRenderer(document);

            updatePage();

            this.loopTimer = new Timer(10000, e -> {
                currentPage++;
                if (currentPage >= document.getNumberOfPages()) {
                    currentPage = 0;
                }
                updatePage();
            });
            loopTimer.start();

        } catch (IOException e) {
            this.setText("Failed to load PDF");
        }

        this.addHierarchyListener(e -> {
            // Only cleanup if the component is actually being removed from a window context
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!this.isDisplayable()) {
                    System.out.println("Cleaning up PDF resources...");
                    cleanup();
                }
            }
        });
    }

    private void updatePage() {
        try {
            // Fallback: If for some reason target dimensions are 0, use a default
            int w = (targetW > 0) ? targetW : 800;
            int h = (targetH > 0) ? targetH : 1000;

            PDRectangle pageSize = document.getPage(currentPage).getMediaBox();

            float scaleW = (float) w / pageSize.getWidth();
            float scaleH = (float) h / pageSize.getHeight();
            float finalScale = Math.min(scaleW, scaleH);

            // Ensure scale is at least something visible
            if (finalScale <= 0) finalScale = 1.0f;

            BufferedImage img = renderer.renderImage(currentPage, finalScale);

            // Update UI on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                this.setIcon(new ImageIcon(img));
                this.revalidate(); // Tell layout manager something changed
                this.repaint();    // Tell JVM to paint the new pixels
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanup() {
        if (loopTimer != null) loopTimer.stop();
        try {
            if (document != null) document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}