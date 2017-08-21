package junkuvo.apps.inputhelper.fragment.item;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ListItemData extends RealmObject {
    @PrimaryKey
    private long id;
    @Required
    private String title;
    @Required
    private String details;
    @Required
    private String createDateTime;


    public ListItemData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    @Override
    public String toString() {
        return id + title + details + createDateTime;
    }
}
