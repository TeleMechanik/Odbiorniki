package telemechan.dev.servercon;

import jakarta.websocket.*;
import telemechan.dev.Main;

import java.io.IOException;
import java.util.Optional;

@ClientEndpoint
public class WebsocketReceiver {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected!");
        try {
            session.getBasicRemote().sendText("UUID:::" + Optional.ofNullable(Main.getSettings().getUuid()).orElse("NaN"));
            session.getBasicRemote().sendText("updaterequest:::" + Main.getSettings().getUuid());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        Main.getParser().parsePackets(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Disconnected: " + reason);
    }
}
