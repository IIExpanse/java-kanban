package Tasks;

import java.util.Objects;

public class Task {

    // Поля названия и описания сделаны финальными, т.к. не меняются без замены задачи.
    // Смена id предусмотрена для реализации замены уже существующей задачи.
    protected final String title;
    protected final String description;
    protected final String status;
    protected int id;

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && title.equals(task.title)
                && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
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

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }
}
