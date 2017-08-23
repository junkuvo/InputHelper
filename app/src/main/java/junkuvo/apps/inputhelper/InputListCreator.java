package junkuvo.apps.inputhelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import io.realm.Realm;
import junkuvo.apps.inputhelper.fragment.InputListFragment;
import junkuvo.apps.inputhelper.fragment.InputListRecyclerViewAdapter;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.util.RealmUtil;

public class InputListCreator {

    private Realm realm;

    public InputListCreator(App app) {
        realm = app.getRealm();
    }

    public void prepareInputListView(View view, InputListFragment.OnListFragmentInteractionListener listener) {
        // Set the adapter
        if (view instanceof RecyclerView) {
            List<ListItemData> list = RealmUtil.selectAllItem(realm);
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(new InputListRecyclerViewAdapter(list, listener));
        }
    }

    public AlertDialog createInputItemEditDialog(final Activity activity, @Nullable final InputEditDialogEventListener inputEditDialogEventListener) {

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_save_data, null);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("保存しておきたいデータを\n入力してください")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String content = ((AppCompatEditText) view.findViewById(R.id.et_content)).getText().toString();

                        ListItemData listItemData = new ListItemData();
                        listItemData.setId(System.currentTimeMillis());
                        listItemData.setTitle("title");
                        listItemData.setDetails(content);
                        listItemData.setCreateDateTime(String.valueOf(System.currentTimeMillis()));
                        RealmUtil.insertItem(realm, listItemData);
//                        if(activity instanceof OverlayActivity){
//                            ((OverlayActivity) activity).finish();
//                        }

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
        return builder.create();
    }

    interface InputEditDialogEventListener{
        void onPositiveButtonClick(DialogInterface dialogInterface, int id);

        void onNegativeButtonClick(DialogInterface dialogInterface, int id);
    }

}

