package telemechan.dev.servercon;

import jakarta.websocket.*;
import telemechan.dev.Main;

import java.io.IOException;

@ClientEndpoint
public class WebsocketReceiver {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected!");
        try {
            if(!Main.getSettings().getUuid().isBlank()) {
                session.getBasicRemote().sendText("UUID:::" + Main.getSettings().getUuid());
                session.getBasicRemote().sendText("updaterequest:::" + Main.getSettings().getUuid());
            }else{
                session.getBasicRemote().sendText("token:::" + Main.getSettings().getToken());
            }

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
