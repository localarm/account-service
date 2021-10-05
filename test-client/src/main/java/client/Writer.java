package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.concurrent.ThreadLocalRandom;

public class Writer extends AbstractSender{

    private final String url;
    private final int id;

    public Writer(HttpClient client, String url, int id) {
        super(client);
        this.url = url;
        this.id = id;
    }

    @Override
    HttpRequest prepareRequest() {
        int value = -40 + ThreadLocalRandom.current().nextInt(0, 100);
        return HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"amount\" : \"" + value + "\" }"))
                .setHeader("Content-type", "application/json")
                .uri(URI.create(url + "/accounts/" + id))
                .build();
    }
}
