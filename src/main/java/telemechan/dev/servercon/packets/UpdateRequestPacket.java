package telemechan.dev.servercon.packets;

import telemechan.dev.Main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UpdateRequestPacket extends BasePacket{
    public UpdateRequestPacket(String type, String value) {
        super(type, value);
    }

    @Override
    public void firePackerEvent() {
        try {
            System.out.println("Trying to download the file needed...");

            String serverUrl = Main.getSettings().getServerAddress();
            URL url = URI.create(serverUrl + (serverUrl.charAt(serverUrl.length() - 1) == '/' ? "" : "/") + "upload?uuid=" + Main.getSettings().getUuid()).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            String disposition = conn.getHeaderField("Content-Disposition");
            String filename = "downloaded_file";

            if (disposition != null && disposition.contains("filename=")) {
                filename = disposition.split("filename=")[1].replace("\"", "");
            }
            File path = new File(Main.getAppDataFolder() + "/upload/");
            try (InputStream in = conn.getInputStream()) {
                if(!path.exists()) path.mkdirs();
                Files.copy(in, Paths.get( path + "/" + filename), StandardCopyOption.REPLACE_EXISTING);
            }

            try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(path + "/" + filename))){
                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null){
                    String filePath = path + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                            byte[] buffer = new byte[8192];
                            int read;
                            while ((read = zipIn.read(buffer)) != -1) {
                                bos.write(buffer, 0, read);
                            }
                        }
                    }
                    zipIn.closeEntry();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            File file = new File(path + "/" + filename);
            file.delete();

            Set<String> neededFiles = new HashSet<>(Main.getSettings().getTimedDisplay().values());
            for(File testedFile : Objects.requireNonNull(new File(path + "/").listFiles())){
                if (!neededFiles.contains(testedFile.getName())) {
                    testedFile.delete();
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
