package web;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVTaskClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final String apiToken;

    public KVTaskClient(String serverUrl) throws IOException, InterruptedException {
        this.serverUrl = serverUrl;
        URI registerUri = URI.create(serverUrl + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(registerUri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        apiToken = response.body();
    }

    public void put(String key, String serializedData) {
        try {
            URI uri = URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + apiToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(serializedData, UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));

            if (200 != response.statusCode()) {
                System.out.println("Сервер не сохранил состояние менеджера. Код ответа: " + response.statusCode());
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка при сохранении менеджера на сервер: " + e.getMessage());
        }
    }

    public String load(String key) {
        try {
            URI uri = URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + apiToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));

            if (200 != response.statusCode()) {
                System.out.println("Сервер не вернул состояние менеджера. Код ответа: " + response.statusCode());
                return null;
            }
            return response.body();

        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка при загрузке менеджера с сервера: " + e.getMessage());
            return null;
        }
    }
}
