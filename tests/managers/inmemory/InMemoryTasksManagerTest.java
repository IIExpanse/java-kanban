package managers.inmemory;

import managers.TasksManagerTest;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTasksManagerTest extends TasksManagerTest<InMemoryTasksManager> {

    @BeforeEach
    public void refreshManager() {
        manager = new InMemoryTasksManager();
    }
}
