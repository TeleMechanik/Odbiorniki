package telemechan.dev.servercon;

import telemechan.dev.servercon.annotations.PacketEventHandler;
import telemechan.dev.servercon.packets.DebugPacket;
import telemechan.dev.servercon.packets.SettingsPacket;
import telemechan.dev.servercon.packets.UpdateRequestPacket;

public class PacketHandler implements PacketListener{
    @PacketEventHandler
    public void receiveDebugMessage(DebugPacket packet) {
        System.out.println("Debug packet ready for parsing!");
    }

    @PacketEventHandler
    public void receiveUpdateRequest(UpdateRequestPacket packet) {}

    @PacketEventHandler
    public void receiveSettingsUpdate(SettingsPacket packet) {}
}
