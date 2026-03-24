package telemechan.dev.media.customtypes.jspane;

import org.graalvm.polyglot.HostAccess;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JSFetchBridge {
    private static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @HostAccess.Export
    public byte[] sendRequest(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Java/GraalVM")
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofByteArray()).body();
        } catch (Exception e) {
            System.err.println("Fetch error: " + e.getMessage());
            return null;
        }
    }

    @HostAccess.Export
    public String decode(byte[] bytes) {
        if (bytes == null) return "";
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }
}
