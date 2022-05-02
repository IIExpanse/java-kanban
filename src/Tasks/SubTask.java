package Tasks;

public class SubTask extends Task {

    private final int parentEpicId;

    public SubTask(String title, String description, String status, int parentEpicId) {
        super(title, description, status);
        this.parentEpicId = parentEpicId;
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
                ",\n title='" + title +
                ",\n description='" + descriptionSize +
                ",\n status='" + status +
                '}';
    }

    public int getParentEpicId() {
        return parentEpicId;
    }
}
