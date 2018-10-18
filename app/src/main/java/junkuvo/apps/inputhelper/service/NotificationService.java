package junkuvo.apps.inputhelper.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.squareup.seismic.ShakeDetector;

import junkuvo.apps.inputhelper.OverlayActivity;
import junkuvo.apps.inputhelper.R;
import junkuvo.apps.inputhelper.util.SharedPreferencesUtil;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotificationService extends Service implements ShakeDetector.Listener {

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals("stopService")) {
                stopOverlayPlayerService();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServiceForeground();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);
        registerReceiver(broadcastReceiver, new IntentFilter("stopService"));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopOverlayPlayerService();
        super.onDestroy();
    }

    private void stopOverlayPlayerService() {
//        unregisterReceiver(broadcastReceiver);
        stopSelf();
        broadcastReceiver = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final String channelId = "InputHelper_channel_id";
    public static final CharSequence channelName = "タップしてコピー";
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
    private NotificationManager notificationManager;
    private static final int REQUEST_CODE_NOTIFICATION = 99;

    /**
     * サービスを永続化するために、通知を作成する。これないと、タスクと一緒にOverlayもkillされる
     *
     * @see <a href=https://qiita.com/sakebook/items/8cafc0766b4f8dc95994>NotificationのStyleについて</a>
     */
    public void startServiceForeground() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // LOWじゃないと振動する。DEFAULTでは振動するが、上からポップアップはでない。
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationChannel.enableVibration(false);// doesn't work

            getNotificationManager().createNotificationChannel(notificationChannel);
            notificationBuilder.setChannelId(channelId);// コンストラクタで入れてるからいらないかも
        }

        Intent intent = new Intent(this, OverlayActivity.class);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, REQUEST_CODE_NOTIFICATION, intent, FLAG_UPDATE_CURRENT));

        notificationBuilder.setTicker(getString(R.string.app_name));
        notificationBuilder.setContentTitle("メモをコピーする");
        notificationBuilder.setContentText("タップでコピーしたいメモを選択できます");
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_notification);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        notificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        notificationBuilder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
//                .setShowActionsInCompactView(0, 1, 2));// Notificationを小さい表示にした時にも表示させたいアクションを定義

//        notificationBuilder.mActions.clear();
//        for (int i = 0; i < NotificationBroadcastAction.values().length; i++) {
//            NotificationBroadcastAction action = NotificationBroadcastAction.values()[i]; // ordinal順に取得される
//            if (action.isEnable()) {
//                notificationBuilder.addAction(action.getIconResId(), getString(action.getStringResId()),
//                        PendingIntent.getBroadcast(this, REQUEST_CODE_NOTIFICATION, new Intent(action.name()), FLAG_UPDATE_CURRENT));
//            }
//        }

        Intent stopIntent = new Intent("stopService");
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationCompat.Action action =
                new NotificationCompat.Action(
                        0,
                        "停止する",
                        resultPendingIntent
                );
        notificationBuilder.addAction(action);
        // ロックスクリーン上でどう見えるか
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
        // PRIORITY_MINだとどこにも表示されなくなる
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MIN);

        startForeground(R.string.app_name, notificationBuilder.build());
    }

    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @Override
    public void hearShake() {
        boolean shakable = SharedPreferencesUtil.getBoolean(this, getString(R.string.app_name), SharedPreferencesUtil.PrefKeys.SHAKE.getKey(), false);
        if (shakable) {
            Intent intent = new Intent(this, OverlayActivity.class);
            startActivity(intent);
        }
    }
}
