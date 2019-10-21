package junkuvo.apps.inputhelper;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import io.realm.Realm;
import junkuvo.apps.inputhelper.fragment.InputListFragment;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.util.InputItemUtil;
import junkuvo.apps.inputhelper.util.VibrateUtil;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static junkuvo.apps.inputhelper.service.NotificationService.REQUEST_CODE_NOTIFICATION;
import static junkuvo.apps.inputhelper.service.NotificationService.channelId;
import static junkuvo.apps.inputhelper.service.NotificationService.channelName;


/**
 *
 */
public class FullActivity extends FragmentActivity implements InputListFragment.OnListFragmentInteractionListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_overlay);
        if (getIntent().hasExtra("id") && getIntent().hasExtra("detail")) {
            this.showDialog(getIntent().getStringExtra("detail"), getIntent().getLongExtra("id", 0L));
        } else {
            finish();
        }
    }

    public void onDestroy() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        super.onDestroy();
    }

    private void showDialog(String content, long id) {
        final Realm realm = ((App) getApplication()).getRealm();
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_save_data, null);
        ((AppCompatEditText) view.findViewById(R.id.et_content)).setText(content);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("保存しておきたいメモを\n入力してください")
                .setView(view)
                .setPositiveButton("保存", null)
                .setNegativeButton("キャンセル", (dialog, which) -> finish());
        final AlertDialog dialog = builder.show();
        view.findViewById(R.id.et_content).setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setOnClickListener(view1 -> {
            String content1 = ((AppCompatEditText) view.findViewById(R.id.et_content)).getText().toString();
            if (TextUtils.isEmpty(content1.trim())) {
                Toast.makeText(this, "メモが空っぽです。", Toast.LENGTH_SHORT).show();
                VibrateUtil.vibrateError(this);
            } else {
                dialog.dismiss();
                InputItemUtil.update(realm, content1, id);
                Toast.makeText(FullActivity.this, "保存しました！", Toast.LENGTH_SHORT).show();
                updateService(content1, id);
                finish();
            }
        });
    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);

    @SuppressLint("RestrictedApi")
    public void updateService(String firstItemDetail, long firstItemId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // LOWじゃないと振動する。DEFAULTでは振動するが、上からポップアップはでない。
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationChannel.enableVibration(false);// doesn't work

            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder.setChannelId(channelId);// コンストラクタで入れてるからいらないかも
        }

        Intent intent = new Intent(this, OverlayActivity.class);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, REQUEST_CODE_NOTIFICATION, intent, FLAG_UPDATE_CURRENT));

        notificationBuilder.setTicker(getString(R.string.app_name));
        if (TextUtils.isEmpty(firstItemDetail)) {
            notificationBuilder.setContentTitle("ここをタップでコピーしたいメモを選択できます");
            notificationBuilder.setContentText("⬇へスワイプでメニュー表示");
        } else {
            notificationBuilder.setContentTitle(firstItemDetail);
            notificationBuilder.setContentText("⬇へスワイプで全文表示");
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(firstItemDetail));
        }
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
                        R.drawable.ic_close_black_24dp,
                        "通知から消す",
                        resultPendingIntent
                );

        Intent openIntent = new Intent(this, InputListActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action actionOpen =
                new NotificationCompat.Action(
                        R.drawable.ic_open_in_new_black_24dp,
                        "アプリを開く",
                        openPendingIntent
                );
        notificationBuilder.mActions.clear();
        notificationBuilder.addAction(action);
        notificationBuilder.addAction(actionOpen);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent editIntent = new Intent(this, FullActivity.class);
            editIntent.putExtra("id", firstItemId);
            editIntent.putExtra("detail", firstItemDetail);
            PendingIntent editPendingIntent = PendingIntent.getActivity(this, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action actionEdit =
                    new NotificationCompat.Action(
                            R.drawable.ic_mode_edit_black_24dp,
                            "編集する",
                            editPendingIntent
                    );
            notificationBuilder.addAction(actionEdit);
        }

        // todo メモトップをコピーするアクション

        // ロックスクリーン上でどう見えるか
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
        // PRIORITY_MINだとどこにも表示されなくなる
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager.notify(R.string.app_name, notificationBuilder.build());

//        Intent intentServiceStop = new Intent(this, NotificationService.class);
//        stopService(intentServiceStop);
//        Intent intent = new Intent(this, NotificationService.class);
//        if (adapter.getItemCount() > 0 && adapter.getItem(0) != null) {
//            intent.putExtra("item", adapter.getItem(0).getDetails());
//        }
//        startService(intent);
    }

    @Override
    public void onListFragmentInteraction(RecyclerView.Adapter adapter, ListItemData item) {

    }

    @Override
    public void onListAdapterCreated(RecyclerView.Adapter adapter) {

    }
}
