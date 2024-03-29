package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private final int parentEpicId;

    public SubTask(String title,
                   String description,
                   TasksStatuses status,
                   LocalDateTime startTime,
                   int duration,
                   int parentEpicId) {

        super(title, description, status, startTime, duration);
        this.parentEpicId = parentEpicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return parentEpicId == subTask.parentEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentEpicId);
    }

    @Override
    public String toString() {
        Integer descriptionSize = null;
        if (description != null) {
            descriptionSize = description.length();
        }

        return "SubTask{" +
                "id=" + id +
                ",\n parentEpicId=" + parentEpicId +
                ",\n title='" + name +
                ",\n description='" + descriptionSize +
                ",\n status='" + status +
                ",\n start time='" + startTime +
                ",\n duration='" + duration +
                '}';
    }

    public int getParentEpicId() {
        return parentEpicId;
    }

}
