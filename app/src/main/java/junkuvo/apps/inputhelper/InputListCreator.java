package junkuvo.apps.inputhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
            Context context = view.getContext();
            List<ListItemData> list = RealmUtil.selectAllItem(realm);
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(new InputListRecyclerViewAdapter(list, listener));
//            recyclerView.setAdapter(new InputListRecyclerViewAdapter(ListItems.ITEMS, listener));
        }
    }

    public AlertDialog createInputItemEditDialog(Context context) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("ダイアログ")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }
}
