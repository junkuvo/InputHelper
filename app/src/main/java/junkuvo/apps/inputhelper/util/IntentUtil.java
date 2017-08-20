package junkuvo.apps.inputhelper.util;

import android.content.Context;
import android.content.Intent;

import junkuvo.apps.inputhelper.InputListActivity;
import junkuvo.apps.inputhelper.OverlayActivity;
import junkuvo.apps.inputhelper.service.NotificationService;

public class IntentUtil {
    public static void startOverlayActivity(Context context) {
        Intent intent = new Intent(context, OverlayActivity.class);
        context.startActivity(intent);
    }
}
