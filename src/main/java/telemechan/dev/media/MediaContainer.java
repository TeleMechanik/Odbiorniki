package telemechan.dev.media;

import lombok.Getter;

import java.io.File;
@Getter
public class MediaContainer implements Comparable<MediaContainer>{
    private final File file;
    private final MediaType type;
    private final int priority;
    private final String id;

    public MediaContainer(File file, MediaType type, int priority, String id) {
        this.file = file;
        this.type = type;
        this.priority = priority;
        this.id = id;
    }

    public MediaContainer(File file, int priority, String id){
        this.file = file;
        this.type = MediaHandler.getType(file);
        this.priority = priority;
        this.id = id;
    }

    @Override
    public int compareTo(MediaContainer container) {
        return Integer.compare(container.priority, this.priority);
    }
}
//TODO (concept) create support for .js plugins and somehow prepare media handlers to handle the plugins

