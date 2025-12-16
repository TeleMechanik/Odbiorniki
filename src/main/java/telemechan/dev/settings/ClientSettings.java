package telemechan.dev.settings;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import telemechan.dev.Main;
import telemechan.dev.media.MediaFile;
import telemechan.dev.media.MediaHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Getter
public class ClientSettings {
    /**
     * UUID of the receiver
     */
    @Nullable
    public String uuid;

    @NonNull
    public String serverAddress = "";

    /**
     * Map holding data required to display specific content at specific time
     */
    public HashMap<TimeRange, String> timedDisplay = new HashMap<>();

    private File defaultDisplay;

    /**
     * Editable config file
     */
    private final File configFile;

    /**
     * Main holder of settings of the receiver
     * @param config file to get the configuration from
     */
    public ClientSettings(File config){
        configFile = config;

        reloadConfig();
    }

    /**
     * This method saves data provided to the config file
     * @param key under what key should the value be saved
     * @param value what value should be saved
     */
    public void saveData(String key, String value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root;

        if (configFile.exists()) {
            root = (ObjectNode) mapper.readTree(configFile);
        } else {
            root = mapper.createObjectNode();
        }

        root.put(key, value);

        mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, root);

        reloadConfig();
    }


    /**
     * Reloads the settings updating the current variables with loaded ones
     */
    private void reloadConfig(){
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(configFile);

        uuid = root.path("uuid").asString();
        serverAddress = root.path("serverAddress").asString();
        defaultDisplay = new File(root.path("defaultImgPath").asString());

        JsonNode timedDisplayNode = root.get("timedDisplay");
        JsonNode timedArrayNode = mapper.readTree(timedDisplayNode.asString());

        timedDisplay.clear();
        MediaHandler.preloadedMedia.clear();

        for(JsonNode entry : timedArrayNode){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime from = LocalDateTime.parse(entry.path("range").path("from").asString(), formatter);
            LocalDateTime to = LocalDateTime.parse(entry.path("range").path("to").asString(), formatter);

            TimeRange timeRange = new TimeRange(from, to);

            String fileName =  entry.path("media").path("file").asString();
            String filePath = Main.getDataFolder().getPath() + "/upload/" + fileName;
            MediaFile mediaFile = new MediaFile(new File(filePath));

            MediaHandler.preloadedMedia.put(fileName, mediaFile);

            timedDisplay.put(timeRange, fileName);
        }
    }

    /**
     * If config doesn't exist create one and save default non null values
     * @param configFile path to the config file
     */
    public static void saveDefault(File configFile) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("uuid", ""); // default UUID
        root.put("name", ""); // default name
        root.put("serverAddress", ""); // default address
        root.put("defaultImgPath", "");

        ArrayNode timedDisplayArray = root.putArray("timedDisplay");

        mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, root);
    }
}
