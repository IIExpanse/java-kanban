package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected final String name;
    protected final String description;
    protected TasksStatuses status;
    protected Integer id;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, TasksStatuses status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status
                && Objects.equals(id, task.id)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, startTime, duration);
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
                ",\n start time='" + startTime +
                ",\n duration='" + duration +
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plus(duration);
    }
}

