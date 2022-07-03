package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {

    private final List<Integer> subTasksIds = new ArrayList<>();
    private final List<Integer> unfinishedTasksIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description, TasksStatuses.NEW, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasksIds, epic.subTasksIds) && Objects.equals(unfinishedTasksIds, epic.unfinishedTasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds, unfinishedTasksIds);
    }

    @Override
    public String toString() {
        Integer descriptionSize = null;
        if (description != null) {
            descriptionSize = description.length();
        }

        return "Epic{" +
                "id=" + id +
                ",\n title='" + name +
                ",\n description='" + descriptionSize +
                ",\n status='" + status +
                ",\n start time='" + startTime +
                ",\n duration='" + duration +
                ",\n subTasksList=" + subTasksIds +
                ",\n unfinishedTasksIds=" + unfinishedTasksIds +
                '}';
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public List<Integer> getUnfinishedTasksIds() {
        return unfinishedTasksIds;
    }

    public void assignNewSubtask(int id, TasksStatuses subTaskStatus) {
        if (subTasksIds.contains(id)) {
            removeSubTask(id);
        }

        subTasksIds.add(id);
        if (!subTaskStatus.equals(TasksStatuses.DONE)) {
            unfinishedTasksIds.add(id);
        }
    }

    public void removeSubTask(int id) {
        subTasksIds.remove((Integer) id);
        unfinishedTasksIds.remove((Integer) id);
    }

    public void setStatus(TasksStatuses status) {
        this.status = status;
    }

    public void setEpicStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEpicDuration(Duration duration) {
        this.duration = duration;
    }
}
