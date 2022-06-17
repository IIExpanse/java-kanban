package tasks;

import java.util.Objects;

public class Task {

    protected final String name;
    protected final String description;
    protected TasksStatuses status;
    protected Integer id;

    public Task(String name, String description, TasksStatuses status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && name.equals(task.name)
                && description.equals(task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    @Override
    public String toString() {
        Integer descriptionSize = null;
        if (description != null) {
            descriptionSize = description.length();
        }

        return "Task{" +
                "id=" + id +
                ",\n title='" + name +
                ",\n description='" + descriptionSize +
                ",\n status='" + status +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public TasksStatuses getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }
}
