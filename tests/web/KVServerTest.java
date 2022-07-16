package web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class KVServerTest {

    private KVServer server;
    private HttpClient client;
    private final String serverUrl = "http://localhost:" + KVServer.PORT;

    @BeforeEach
    public void startServer() throws IOException {
        server = new KVServer();
        client = HttpClient.newHttpClient();
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void registerTest() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void shouldNotRegisterWithWrongMethod() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString("null", UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void saveAndLoadTest() throws IOException, InterruptedException {
        String apiToken;
        String data = "managerTasksData";
        apiToken = register();

        URI uri = URI.create(serverUrl + "/save/" + "superSecretKey" + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(serverUrl + "/load/" + "superSecretKey" + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(data, response.body());
    }

    @Test
    public void shouldNotSaveWithWrongQuery() throws IOException, InterruptedException {
        String apiToken;
        apiToken = register();

        URI uri = URI.create(serverUrl + "/save/" + "?API_TOKEN=" + "1234");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString("managerTasksData"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(403, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(serverUrl + "/save/" + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString("managerTasksData"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(serverUrl + "/save/" + "superSecretKey" + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(serverUrl + "/save/" + "superSecretKey" + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void shouldNotLoadWithWrongQuery() throws IOException, InterruptedException {
        String apiToken;
        String data = "managerTasksData";
        apiToken = register();
        save(data);

        URI uri = URI.create(serverUrl + "/load/" + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(serverUrl + "/load/" + "someKey" + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(serverUrl + "/load/" + "superSecretKey" + "?API_TOKEN=" + "1234");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(403, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(serverUrl + "/load/" + "superSecretKey" + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString("managerTasksData"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
        assertTrue(response.body().isEmpty());
    }



    private String register() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        return response.body();
    }

    private void save(String data) throws IOException, InterruptedException {
        String apiToken = register();

        URI uri = URI.create(serverUrl + "/save/" + "superSecretKey" + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
    }
}
