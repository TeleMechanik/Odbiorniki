package telemechan.dev.servercon.packets;

import telemechan.dev.Main;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

public class SettingsPacket extends BasePacket {
    public SettingsPacket(String type, String value) {
        super(type, value);
    }

    @Override
    public void firePackerEvent() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> config = mapper.readValue(value, new TypeReference<>() {});

        for (Map.Entry<String, Object> entry : config.entrySet()) {
            Object val = entry.getValue();

            String stringValue;
            if (val instanceof String) {
                stringValue = (String) val;
            } else {
                // Convert arrays/objects back to JSON string
                stringValue = mapper.writeValueAsString(val);
            }

            Main.getSettings().saveData(entry.getKey(), stringValue);
        }

    }
}
