package web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class KVTaskClientTest {

    private KVServer server;
    private KVTaskClient client;

    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        client = new KVTaskClient("http://localhost:" + KVServer.PORT);
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void putAndLoadTest() {
        String key = "superSecretKey";
        String data = "managerTasksData";

        client.put(key, data);
        assertEquals(data, client.load(key));
    }

    @Test
    public void shouldReturnNullForWrongKey() {
        String key = "superSecretKey";
        assertNull(client.load(key));
    }
}
