package telemechan.dev.servercon.packets;

public class DebugPacket extends BasePacket{

    public DebugPacket(String type, String value) {
        super(type, value);
    }

    @Override
    public void firePackerEvent() {
        System.out.println("Packet type: " + type);
        System.out.println("Packet content: " + value);
    }
}
