package junkuvo.apps.inputhelper.util;

import io.realm.Realm;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;

public class InputItemUtil {
    public static void save(Realm realm, String content){
        ListItemData listItemData = new ListItemData();
        listItemData.setId(System.currentTimeMillis());
        listItemData.setTitle("title");
        listItemData.setDetails(content);
        listItemData.setCreateDateTime(String.valueOf(System.currentTimeMillis()));
        RealmUtil.insertItem(realm, listItemData);
    }

    public static void update(Realm realm, String content, long id){
        ListItemData listItemData = new ListItemData();
        listItemData.setId(id);
        listItemData.setTitle("title");
        listItemData.setDetails(content);
        listItemData.setCreateDateTime(String.valueOf(System.currentTimeMillis()));
        RealmUtil.updateInputItem(realm, listItemData);
    }

}
