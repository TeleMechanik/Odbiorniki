package telemechan.dev.media.customtypes.jspane;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class JSPluginLoader {
    public static File preparePlugin(File zipFile) throws IOException {
        // Create directory name from zip name (e.g., "myplugin.zip" -> "myplugin")
        String folderName = zipFile.getName().substring(0, zipFile.getName().lastIndexOf('.'));
        Path targetDir = zipFile.getParentFile().toPath().resolve(folderName);

        // 1. Create the directory if it doesn't exist
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 2. Extract the ZIP
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = targetDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    // Ensure parent directories exist (for nested files in zip)
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }

        // 3. Return the main.js file
        File mainJs = targetDir.resolve("main.js").toFile();
        if (!mainJs.exists()) {
            throw new FileNotFoundException("Plugin zip missing main.js in base directory!");
        }
        return mainJs;
    }
}
