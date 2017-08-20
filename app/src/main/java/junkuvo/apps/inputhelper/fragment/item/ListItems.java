package junkuvo.apps.inputhelper.fragment.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class ListItems {

    public static final List<ListItemData> ITEMS = new ArrayList<ListItemData>();

    public static final Map<String, ListItemData> ITEM_MAP = new HashMap<String, ListItemData>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createListItem(i));
        }
    }

    private static void addItem(ListItemData item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ListItemData createListItem(int position) {
        return new ListItemData(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

}
