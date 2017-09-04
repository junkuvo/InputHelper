package junkuvo.apps.inputhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import junkuvo.apps.inputhelper.R;
import junkuvo.apps.inputhelper.service.NotificationService;
import junkuvo.apps.inputhelper.util.SharedPreferencesUtil;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean shouldShow = SharedPreferencesUtil.getBoolean(context,context.getString(R.string.app_name), SharedPreferencesUtil.PrefKeys.NOTIFICATION_SHOW_IN_BAR.getKey(), false);

        if(shouldShow) {
            Intent i = new Intent(context, NotificationService.class);
            context.startService(i);
        }
    }
}
