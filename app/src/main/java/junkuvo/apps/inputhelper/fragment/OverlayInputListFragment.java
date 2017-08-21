package junkuvo.apps.inputhelper.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import junkuvo.apps.inputhelper.App;
import junkuvo.apps.inputhelper.InputListCreator;
import junkuvo.apps.inputhelper.R;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.util.ClipboardUtil;

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
        View view = inflater.inflate(R.layout.fragment_inputlist_list, container, false);
        inputListCreator.prepareInputListView(view, new InputListFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(ListItemData item) {
                ClipboardUtil.copy(getContext(), item.getDetails());
                Toast.makeText(getContext(), "コピーしました！", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        return builder.create();
    }

}
