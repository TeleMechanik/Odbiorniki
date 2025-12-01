package telemechan.dev.servercon.packets;

import telemechan.dev.Main;
import telemechan.dev.media.MediaFile;
import telemechan.dev.media.MediaHandler;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class UpdateRequestPacket extends BasePacket{
    public UpdateRequestPacket(String type, String value) {
        super(type, value);
    }

    @Override
    public void firePackerEvent() {
        try {
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
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
