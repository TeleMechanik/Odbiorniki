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

    public MediaFile(File file){
        this.file = file;
        this.type = MediaHandler.getType(file);
    }
}
//TODO (concept) create support for .js plugins and somehow prepare media handlers to handle the plugins

