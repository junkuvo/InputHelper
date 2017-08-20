package junkuvo.apps.inputhelper.fragment.item;

import io.realm.RealmModel;

public class ListItemData implements RealmModel {
    public final String id;
    public final String content;
    public final String details;

    public ListItemData(String id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }

    @Override
    public String toString() {
        return content;
    }
}
