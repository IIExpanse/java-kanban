package tasks;

import java.util.Objects;

public class Task {

    protected final String title;
    protected final String description;
    protected TaskStatuses status;
    protected Integer id;

    public Task(String title, String description, TaskStatuses status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && title.equals(task.title)
                && description.equals(task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, id);
    }

    @Override
    public String toString() {
        Integer descriptionSize = null;
        if (description != null) {
            descriptionSize = description.length();
        }

        return "Task{" +
                "id=" + id +
                ",\n title='" + title +
                ",\n description='" + descriptionSize +
                ",\n status='" + status +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }
}
