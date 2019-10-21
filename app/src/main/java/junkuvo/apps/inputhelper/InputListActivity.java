package junkuvo.apps.inputhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import junkuvo.apps.inputhelper.fragment.InputListFragment;
import junkuvo.apps.inputhelper.fragment.InputListRecyclerViewAdapter;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.service.NotificationService;
import junkuvo.apps.inputhelper.util.InputItemUtil;
import junkuvo.apps.inputhelper.util.IntentUtil;
import junkuvo.apps.inputhelper.util.RealmUtil;
import junkuvo.apps.inputhelper.util.SharedPreferencesUtil;
import junkuvo.apps.inputhelper.util.VibrateUtil;

public class InputListActivity extends AppCompatActivity implements InputListFragment.OnListFragmentInteractionListener, RecognitionListener {

    FloatingActionButton fab;
    InputListRecyclerViewAdapter adapter;
    FloatingActionButton fabSpeak;
    private PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
            .addTestDevice("EBAB8562A2BF0F81C1702F59F3E0E5C6")
            .addTestDevice("F171E99944D7E61C3B4EE10FA9DF36A8")
            .addTestDevice("D35B2B0C156F2B555B9D4F1E2902E7AF")
            .build();
    private int adW;
    private int adH;
    private CoordinatorLayout clMain;
    private LinearLayout llEmpty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clMain = findViewById(R.id.main);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showInputDialog());

        fabSpeak = findViewById(R.id.fab_speak);
        fabSpeak.setOnClickListener(view -> IntentUtil.startVoiceRecognizer(this, this, clMain));

        adW = ((App) getApplication()).getAdW();
        adH = ((App) getApplication()).getAdH();
        llEmpty = findViewById(R.id.ll_empty);

        updateService();
    }

     public void updateService(){
        Intent intentServiceStop = new Intent(this, NotificationService.class);
        stopService(intentServiceStop);
        Intent intent = new Intent(this, NotificationService.class);
        if (adapter.getItemCount() > 0 && adapter.getItem(0) != null) {
            intent.putExtra("detail", adapter.getItem(0).getDetails());
            intent.putExtra("id", adapter.getItem(0).getId());
        }
        startService(intent);
    }

    private void showInputDialog() {
        InputListCreator inputListCreator = new InputListCreator((App) getApplication());
        inputListCreator.showInputItemEditDialog(InputListActivity.this, new InputListCreator.InputEditDialogEventListener() {
            @Override
            public void onPositiveButtonClick(DialogInterface dialogInterface, int id) {
                OrderedRealmCollection<ListItemData> list = RealmUtil.selectAllItem(((App) getApplication()).getRealm());
                if ((list != null) && list.size() > 0) {
                    adapter.setRealmReferenceToAdapter(list);
                    setEmptyLayout(adapter.getItemCount() == 0);
                    updateService();
                }
            }

            @Override
            public void onNegativeButtonClick(DialogInterface dialogInterface, int id) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("FROM_OVERLAY")) {
            if (getIntent().getBooleanExtra("FROM_OVERLAY", false)) {
                showInputDialog();
                getIntent().removeExtra("FROM_OVERLAY");
            }
        }

        PublisherAdView adView = findViewById(R.id.adView_publisher);
        adView.setAdSizes(new AdSize(adW, adH));
        if (!adView.isLoading()) {
            adView.loadAd(adRequest);
//            adView.setAdListener(new AdListener(){
//                @Override
//                public void onAdLoaded() {
//                    super.onAdLoaded();
//                }
//            });
        }

        setEmptyLayout(adapter.getItemCount() == 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.getItem(0);
        boolean isChecked = SharedPreferencesUtil.getBoolean(this, getString(R.string.app_name), SharedPreferencesUtil.PrefKeys.NOTIFICATION_SHOW_IN_BAR.getKey(), false);
        menuItem.setChecked(isChecked);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // チェックボックスの状態変更を行う
            item.setChecked(!item.isChecked());
            SharedPreferencesUtil.saveBoolean(this, getString(R.string.app_name), SharedPreferencesUtil.PrefKeys.NOTIFICATION_SHOW_IN_BAR.getKey(), item.isChecked());
            return true;
        } else if (id == R.id.action_privacy) {
            IntentUtil.startWeb(this, "http://site-1308773-9967-2992.strikingly.com/");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(final RecyclerView.Adapter adapter, final ListItemData item) {
        final Realm realm = ((App) getApplication()).getRealm();
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_save_data, null);
        String content = item.getDetails();
        ((AppCompatEditText) view.findViewById(R.id.et_content)).setText(content);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("保存しておきたいメモを\n入力してください")
                .setView(view)
                .setPositiveButton("保存", null)
                .setNegativeButton("キャンセル", null)
                .setNeutralButton("削除", (dialogInterface, i) -> {
                    RealmUtil.deleteInputItem(realm, item.getId());
                    Snackbar.make(clMain, "削除しました！", Snackbar.LENGTH_SHORT).show();
                    setEmptyLayout(adapter.getItemCount() == 0);
                    updateService();
                });
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
                InputItemUtil.update(realm, content1, item.getId());
                setEmptyLayout(adapter.getItemCount() == 0);
                Snackbar.make(clMain, "保存しました！", Snackbar.LENGTH_SHORT).show();
                updateService();
            }
        });
    }

    @Override
    public void onListAdapterCreated(RecyclerView.Adapter adapter) {
        this.adapter = (InputListRecyclerViewAdapter) adapter;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {
//        List<String> recData = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentUtil.RequestCode requestCodeParam = IntentUtil.RequestCode.getParam(requestCode);
        switch (requestCodeParam) {
            case VOICE_RECOGNIZER:
                if (data != null) {
                    List<String> recData = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (recData != null && recData.size() > 0) {
                        Realm realm = ((App) getApplication()).getRealm();
                        InputItemUtil.save(realm, recData.get(0));
                        Snackbar.make(clMain, "保存しました！", Snackbar.LENGTH_SHORT).show();
                        updateService();
                    }

                    OrderedRealmCollection<ListItemData> list = RealmUtil.selectAllItem(((App) getApplication()).getRealm());
                    if ((list != null) && list.size() > 0) {
                        adapter.setRealmReferenceToAdapter(list);
                        fab.clearAnimation();
                    }
                }
                break;
        }
    }

    public void setEmptyLayout(boolean isEmpty) {
        llEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}
