package telemechan.dev.servercon.packets;

import lombok.Getter;
import lombok.Setter;

public abstract class BasePacket {
    @Getter
    protected String value;

    @Getter
    protected String type;

    @Setter
    protected boolean canceled = false;

    public BasePacket(String type, String value){
        this.type = type;
        this.value = value;
    }

    public abstract void firePackerEvent();

    public boolean isCanceled(){
        return canceled;
    }
}
