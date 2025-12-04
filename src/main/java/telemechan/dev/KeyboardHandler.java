package telemechan.dev;

import telemechan.dev.settings.SettingsPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KeyboardHandler{

    public static void registerBinds(){
        JRootPane root = Main.getMainFrame().getRootPane();

        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_I,
                java.awt.event.InputEvent.CTRL_DOWN_MASK
        ), "openSettings");

        am.put("openSettings", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsPanel settingsPanel = new SettingsPanel();

                settingsPanel.setVisible(true);
            }
        });

    }
}
