package client;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class AbstractSender implements Runnable{

    private final HttpClient client;

    protected AbstractSender(HttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            try {
                var request = prepareRequest();
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                System.out.println("Failed to send request, " + e.getMessage());
            }
        }
    }

    abstract HttpRequest prepareRequest() throws IOException;

}
