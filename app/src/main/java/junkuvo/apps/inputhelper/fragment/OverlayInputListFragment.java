package junkuvo.apps.inputhelper.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import junkuvo.apps.inputhelper.App;
import junkuvo.apps.inputhelper.InputListActivity;
import junkuvo.apps.inputhelper.InputListCreator;
import junkuvo.apps.inputhelper.R;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.util.ClipboardUtil;
import junkuvo.apps.inputhelper.util.VibrateUtil;

/**
 *
 */
public class OverlayInputListFragment extends DialogFragment {

    private InputListCreator inputListCreator;

    public OverlayInputListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputListCreator = new InputListCreator((App) getActivity().getApplication());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup container = getActivity().findViewById(R.id.ll_overlay);
        View view = inflater.inflate(R.layout.recyclerview_input_list, container, false);
        inputListCreator.prepareInputListView(view, new InputListFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(RecyclerView.Adapter adapter, ListItemData item) {
                ClipboardUtil.copy(getContext(), item.getDetails());
                Toast.makeText(getContext(), item.getDetails() + "\nコピーしました！", Toast.LENGTH_SHORT).show();
                VibrateUtil.vibrate(getContext());
                dismiss();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }

            @Override
            public void onListAdapterCreated(RecyclerView.Adapter adapter) {

            }
        }, true);

        builder.setView(view);
        builder.setMessage("コピーしたいメモを\nタップしてください");
        builder.setPositiveButton("新しいメモの追加", (dialog, which) -> {
            Intent intent = new Intent(getContext(), InputListActivity.class);
            intent.putExtra("FROM_OVERLAY", true);
            startActivity(intent);
            getActivity().finish();
        });
        builder.setNegativeButton("キャンセル", (dialogInterface, i) -> getActivity().finish());
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
