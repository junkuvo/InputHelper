package junkuvo.apps.inputhelper;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import junkuvo.apps.inputhelper.fragment.InputListFragment;
import junkuvo.apps.inputhelper.fragment.InputListRecyclerViewAdapter;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.service.NotificationService;
import junkuvo.apps.inputhelper.util.InputItemUtil;
import junkuvo.apps.inputhelper.util.RealmUtil;

public class InputListActivity extends AppCompatActivity implements InputListFragment.OnListFragmentInteractionListener {

    FloatingActionButton fab;
    InputListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputListCreator inputListCreator = new InputListCreator((App) getApplication());
                inputListCreator.createInputItemEditDialog(InputListActivity.this, new InputListCreator.InputEditDialogEventListener() {
                    @Override
                    public void onPositiveButtonClick(DialogInterface dialogInterface, int id) {
                        if(adapter.isEmpty()){
                            OrderedRealmCollection<ListItemData> list = RealmUtil.selectAllItem(((App)getApplication()).getRealm());
                            if((list != null) && list.size() > 0){
                                adapter.setRealmReferenceToAdapter(list);
                                fab.clearAnimation();
                            }
                        }
                    }

                    @Override
                    public void onNegativeButtonClick(DialogInterface dialogInterface, int id) {

                    }
                }).show();
            }
        });

        Intent intent = new Intent(this, NotificationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter.isEmpty()) {
            startAnimationFab();
        }
    }

    private void startAnimationFab(){
        Animation blinkAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        fab.startAnimation(blinkAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_input_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            notificationService = ((NotificationService.ServiceLocalBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            notificationService = null;
        }
    };

    @SuppressWarnings("unused")
    private NotificationService notificationService;

    @Override
    public void onListFragmentInteraction(final RecyclerView.Adapter adapter, final ListItemData item) {
        final Realm realm = ((App) getApplication()).getRealm();
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_save_data, null);
        String content = item.getDetails();
        ((AppCompatEditText) view.findViewById(R.id.et_content)).setText(content);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("保存しておきたいデータを\n入力してください")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String content = ((AppCompatEditText) view.findViewById(R.id.et_content)).getText().toString();
                        InputItemUtil.update(realm, content, item.getId());
                        Snackbar.make(findViewById(R.id.main), "保存しました！", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("キャンセル", null)
                .setNeutralButton("削除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RealmUtil.deleteInputItem(realm, item.getId());
                        Snackbar.make(findViewById(R.id.main), "削除しました！", Snackbar.LENGTH_SHORT).show();
                        if(adapter.getItemCount() == 0){
                            ((InputListRecyclerViewAdapter)adapter).setEmptyLayout();
                            startAnimationFab();
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void onListAdapterCreated(RecyclerView.Adapter adapter) {
        this.adapter = (InputListRecyclerViewAdapter) adapter;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(KeyEvent.ACTION_DOWN == event.getAction()){
            if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("アプリを終了しますか？\n通知バーからのコピーができなくなります");
                builder.setPositiveButton("終了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }

        }
        return super.dispatchKeyEvent(event);
    }
}
