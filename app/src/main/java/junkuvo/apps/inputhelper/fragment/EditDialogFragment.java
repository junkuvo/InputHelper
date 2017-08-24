package junkuvo.apps.inputhelper.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import junkuvo.apps.inputhelper.R;


/**
 * PushNotificationActivityに描画されるDialogFragmentのクラス
 * 通知内容に応じたダイアログが表示されるだけ
 */
public class EditDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("入力ダイアログ")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent = new Intent();
//                        intent.putExtras(getArguments());
//                        intent.setComponent((ComponentName) getArguments().getParcelable(AbstractWkToolbarActivity.ExtraKey.NEXT_ACTIVITY_BY_PUSH.getKey()));
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
        return alert.create();
    }
}
