package telemechan.dev;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardHandler{

    public static void registerBinds(){
        // Get the root pane
        JRootPane root = Main.getMainFrame().getRootPane();

// InputMap + ActionMap for global shortcuts
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

// Example: Ctrl+I opens settings
        im.put(KeyStroke.getKeyStroke("control I"), "openSettings");
        am.put("openSettings", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsPanel settingsPanel = new SettingsPanel();

                settingsPanel.setVisible(true);
            }
        });

    }
}
