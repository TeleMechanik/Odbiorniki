package telemechan.dev.servercon;

import telemechan.dev.Main;
import telemechan.dev.media.MediaFile;
import telemechan.dev.media.MediaHandler;
import telemechan.dev.servercon.annotations.PacketEventHandler;
import telemechan.dev.servercon.packets.DebugPacket;
import telemechan.dev.servercon.packets.UpdateRequestPacket;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PacketHandler implements PacketListener{
    @PacketEventHandler
    public void receiveDebugMessage(DebugPacket packet){
        System.out.println("Debug packet ready for parsing!");
    }

    @PacketEventHandler
    public void receiveUpdateRequest(UpdateRequestPacket packet) throws Exception{
        System.out.println("Trying to download the file needed...");

        URL url = URI.create("http://se01.creperus.top:10210/obrazek").toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        String disposition = conn.getHeaderField("Content-Disposition");
        String filename = "downloaded_file";

        if (disposition != null && disposition.contains("filename=")) {
            filename = disposition.split("filename=")[1].replace("\"", "");
        }

        try (InputStream in = conn.getInputStream()) {
            Files.copy(in, Paths.get("C:/Pulpit/TeleMechanik/Testy atomowe/upload/" + filename), StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("✔ Saved file as: " + filename);

        File file = new File("C:/Pulpit/TeleMechanik/Testy atomowe/upload/" + filename);
        Main.updateMainFrame(new MediaFile(file, MediaHandler.getType(file)));
    }
}
