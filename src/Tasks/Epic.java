package Tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private String status;
    private final List<Integer> subTasksIds = new ArrayList<>();
    private final List<Integer> unfinishedTasksIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description, "NEW");
    }

    @Override
    public String toString() {
        Integer descriptionSize = null;
        if (description != null) {
            descriptionSize = description.length();
        }

        return "Epic{" +
                "id=" + id +
                ",\n title='" + title +
                ",\n description='" + descriptionSize +
                ",\n status='" + status +
                ",\n subTasksList=" + subTasksIds +
                ",\n unfinishedTasksIds=" + unfinishedTasksIds +
                '}';
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void refreshStatus() {
        if (subTasksIds.size() == unfinishedTasksIds.size()) {
            this.status = "NEW";

        } else if (unfinishedTasksIds.size() > 0) {
            this.status = "IN PROGRESS";

        } else {
            this.status = "DONE";
        }
    }

    public void assignNewSubtask(int id, String subTaskStatus) {
        subTasksIds.add(id);
        if (!subTaskStatus.equals("DONE")) {
            unfinishedTasksIds.add(id);
        }
        refreshStatus();
    }

    public void removeSubTask(int id) {
        subTasksIds.remove((Integer) id);
        unfinishedTasksIds.remove((Integer) id);
        refreshStatus();
    }

    public void removeAllSubtasks() {
        subTasksIds.clear();
        unfinishedTasksIds.clear();
    }
}
