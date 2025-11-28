package telemechan.dev.servercon;

import telemechan.dev.servercon.annotations.PacketEventHandler;
import telemechan.dev.servercon.packets.BasePacket;
import telemechan.dev.servercon.packets.DebugPacket;
import telemechan.dev.servercon.packets.UpdateRequestPacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PacketParser {
    private final List<PacketListener> listeners = new ArrayList<>();


    public void registerHandler(PacketListener listener){
        listeners.add(listener);
    }

    public void parsePackets(String receivedPacket) {
        BasePacket packet;
        String[] rawPacket = receivedPacket.split(":::");
        if(rawPacket.length != 2) return;

        switch (rawPacket[0].toLowerCase()){
            case "debug" -> packet = new DebugPacket(rawPacket[0], rawPacket[1]);
            case "updaterequest" -> packet = new UpdateRequestPacket(rawPacket[0], rawPacket[1]);
//            case "settings" -> packet = null;
            default -> {
                return;
            }
        }

        for (Object listener : listeners) {
            Class<?> clazz = listener.getClass();

            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(PacketEventHandler.class))
                    continue;

                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1)
                    continue;

                Class<?> paramType = params[0];

                if (!paramType.isAssignableFrom(packet.getClass()))
                    continue;

                method.setAccessible(true);

                try {
                    method.invoke(listener, packet);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if(!packet.isCanceled()){
            packet.firePackerEvent();
        }
    }

}
