package junkuvo.apps.inputhelper.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import junkuvo.apps.inputhelper.BuildConfig;
import junkuvo.apps.inputhelper.InputListActivity;
import junkuvo.apps.inputhelper.OverlayActivity;
import junkuvo.apps.inputhelper.R;

public class NotificationService extends Service {

    //サービスに接続するためのBinder
    public class ServiceLocalBinder extends Binder {
        //サービスの取得
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    //Binderの生成
    private final IBinder binder = new ServiceLocalBinder();

    private Context appContext;
//    private IntentFilter localBroadcastFilter = new IntentFilter();

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (localBroadcastReceiver != null) {
//            LocalBroadcastManager.getInstance(appContext).unregisterReceiver(localBroadcastReceiver);
//            localBroadcastReceiver = null;
//            appContext = null;
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        startServiceForeground();
//        localBroadcastFilter.addAction(BROADCAST_KEY_CLICK);
//        LocalBroadcastManager.getInstance(appContext).registerReceiver(localBroadcastReceiver, localBroadcastFilter);
//        registerReceiver(localBroadcastReceiver, localBroadcastFilter);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private static final String CHANNEL_ID = BuildConfig.APPLICATION_ID + "CHANNEL_ID";
    private void startServiceForeground() {
        // サービスを永続化するために、通知を作成する
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("データをコピー");
        builder.setContentText("タップでコピーしたいデータを選択できます");
//        builder.setSubText("タップで入力値を選択してコピーできます");
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

////        // FIXME : service から unbindする方法がないので、Notification から停止させる機能は一旦なくす
//        builder.addAction(R.mipmap.ic_launcher, "a", PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(BROADCAST_KEY_CLICK), 0));

        //通知タップ時のPendingIntent
//        builder.setContentIntent(PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(BROADCAST_KEY_CLICK), 0));
//        builder.setDeleteIntent(  //通知の削除時のPendingIntent
//                getPendingIntentWithBroadcast(DELETE_NOTIFICATION)
//        );

        Intent intent = new Intent(appContext, OverlayActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(appContext, 0,intent, 0));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            NotificationCompat.BigTextStyle notificationBigTextStyle = new NotificationCompat.BigTextStyle(builder);
            builder.setStyle(notificationBigTextStyle);
            // PRIORITY_MINだとどこにも表示されなくなる
            builder.setPriority(Notification.PRIORITY_MIN);
        }
        // ロックスクリーン上でどう見えるか（見えなくていい）
        builder.setVisibility(Notification.VISIBILITY_SECRET);

        Intent intentAction = new Intent(appContext, InputListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(appContext, 1, intentAction, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_playlist_add_white_48dp, "データの編集", pendingIntent).build();
        builder.addAction(action);

        // サービス永続化
        startForeground(R.string.app_name, builder.build());

    }

//    private final static String BROADCAST_KEY_CLICK = "BROADCAST_KEY_CLICK";
//    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(BROADCAST_KEY_CLICK)) {
//                showEditDialog(context);
//            }
//        }
//    };
//
//    private void showEditDialog(Context context){
//        IntentUtil.startOverlayActivity(context);
//    }
}
