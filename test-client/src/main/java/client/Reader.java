package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class Reader extends AbstractSender {

    private final String url;
    private final int id;

    public Reader(HttpClient client, String url, int id) {
        super(client);
        this.url = url;
        this.id = id;
    }

    @Override
    HttpRequest prepareRequest()  {

        return HttpRequest.newBuilder()
                .uri(URI.create(url + "/accounts/" + id))
                .GET()
                .build();
    }
}
