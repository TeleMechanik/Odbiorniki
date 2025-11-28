package telemechan.dev.servercon;

import jakarta.websocket.*;
import telemechan.dev.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;

@ClientEndpoint
public class Endpoint {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected!");
        InetAddress localHost;
        try {
            localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();

            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }

            String macAddress = String.join("-", hexadecimal);
            String clientId = "MAC:" + macAddress;

            session.getBasicRemote().sendText(clientId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @OnMessage
    public void onMessage(String message) {
        Main.getParser().parsePackets(message);
    }

    @Deprecated(forRemoval = true)
    @OnMessage
    public void onMessage(ByteBuffer data) {
        try {
            byte[] bytes = new byte[data.remaining()];
            data.get(bytes);

            File file = new File("C:/Pulpit/TeleMechanik/Testy atomowe/upload/received.png");

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();

            System.out.println("PNG received and saved!");

            Main.getParser().parsePackets("Teto word of the day! AUTISM");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Disconnected: " + reason);
    }
}
