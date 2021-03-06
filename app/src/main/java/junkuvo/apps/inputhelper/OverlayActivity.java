package junkuvo.apps.inputhelper;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import junkuvo.apps.inputhelper.fragment.InputListFragment;
import junkuvo.apps.inputhelper.fragment.OverlayInputListFragment;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;


/**
 * アプリがバックグラウンドにいる場合（バックグラウンドでもKillされて、いない場合でも）に、
 * AzureのNotificationReceiverより呼び出されて、ダイアログFragmentだけを表示するActivity
 * ロック中でも表示される。
 */
public class OverlayActivity extends FragmentActivity implements InputListFragment.OnListFragmentInteractionListener{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.showDialog();
    }

    public void onDestroy() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        super.onDestroy();
    }

    private void showDialog() {
        final DialogFragment fragment = new OverlayInputListFragment();
        fragment.setCancelable(true);

        Bundle bundle = getIntent().getExtras();
        fragment.setArguments(bundle);

        if (!this.isFinishing()) {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    fragment.show(OverlayActivity.this.getSupportFragmentManager(), this.getClass().getName());
                }
            });
        }

    }

    @Override
    public void onListFragmentInteraction(ListItemData item) {

    }
}
