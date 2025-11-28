package telemechan.dev.servercon.packets;

public class UpdateRequestPacket extends BasePacket{
    public UpdateRequestPacket(String type, String value) {
        super(type, value);
    }

    @Override
    public void firePackerEvent() {

    }
}
