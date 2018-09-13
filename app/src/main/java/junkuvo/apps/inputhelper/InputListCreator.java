package junkuvo.apps.inputhelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import junkuvo.apps.inputhelper.fragment.InputListFragment;
import junkuvo.apps.inputhelper.fragment.InputListRecyclerViewAdapter;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.util.InputItemUtil;
import junkuvo.apps.inputhelper.util.RealmUtil;

public class InputListCreator {

    private Realm realm;

    public InputListCreator(App app) {
        realm = app.getRealm();
    }

    public void prepareInputListView(View view, InputListFragment.OnListFragmentInteractionListener listener){
        prepareInputListView(view, listener, false);
    }

    public void prepareInputListView(View view, InputListFragment.OnListFragmentInteractionListener listener, boolean fromNotification) {
        // Set the adapter
        if (view instanceof RecyclerView) {
            OrderedRealmCollection<ListItemData> list = RealmUtil.selectAllItem(realm);
            RecyclerView recyclerView = (RecyclerView) view;

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(view.getContext()).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            recyclerView.setAdapter(new InputListRecyclerViewAdapter(view.getContext(), list, true, listener, fromNotification));
        }
    }

    public AlertDialog createInputItemEditDialog(final Activity activity, @Nullable final InputEditDialogEventListener inputEditDialogEventListener) {

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_save_data, null);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("保存しておきたいメモを\n入力してください")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String content = ((AppCompatEditText) view.findViewById(R.id.et_content)).getText().toString();
                        InputItemUtil.save(realm, content);
                        Snackbar.make(activity.findViewById(R.id.main), "保存しました！", Snackbar.LENGTH_SHORT).show();

                        if (inputEditDialogEventListener != null) {
                            inputEditDialogEventListener.onPositiveButtonClick(dialog, id);
                        }
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                        if(activity instanceof OverlayActivity){
//                            ((OverlayActivity) activity).finish();
//                        }
                        if (inputEditDialogEventListener != null) {
                            inputEditDialogEventListener.onNegativeButtonClick(dialog, id);
                        }
                    }
                });

        final AlertDialog dialog = builder.create();
        view.findViewById(R.id.et_content).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && dialog.getWindow() != null) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        return dialog;
    }

    interface InputEditDialogEventListener {
        void onPositiveButtonClick(DialogInterface dialogInterface, int id);

        void onNegativeButtonClick(DialogInterface dialogInterface, int id);
    }

}

