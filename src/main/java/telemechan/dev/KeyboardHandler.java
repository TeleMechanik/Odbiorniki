package telemechan.dev;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyBoardListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_I && e.isControlDown()) {
            SettingsPanel settingsPanel = new SettingsPanel();

            settingsPanel.setVisible(true);
        }
    }
}
