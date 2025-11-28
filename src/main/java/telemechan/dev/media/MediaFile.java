package telemechan.dev.media;

import lombok.Getter;

import java.io.File;
@Getter
public class MediaFile {
    private File file;
    private MediaType type;

    public MediaFile(File file, MediaType type) {
        this.file = file;
        this.type = type;
    }
}

