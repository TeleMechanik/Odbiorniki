package telemechan.dev;

import telemechan.dev.settings.SettingsPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KeyboardHandler{

    public static void registerBinds(){
        JRootPane root = Main.getMainFrame().getRootPane();

        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

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
