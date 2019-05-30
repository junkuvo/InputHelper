package junkuvo.apps.inputhelper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import junkuvo.apps.inputhelper.App;
import junkuvo.apps.inputhelper.InputListCreator;
import junkuvo.apps.inputhelper.R;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class InputListFragment extends DialogFragment {

    private OnListFragmentInteractionListener mListener;
    private InputListCreator inputListCreator;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InputListFragment() {
    }

    @SuppressWarnings("unused")
    public static InputListFragment newInstance(int columnCount) {
        InputListFragment fragment = new InputListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.recyclerview_input_list, container, false);
        inputListCreator = new InputListCreator((App) getActivity().getApplication());
        inputListCreator.prepareInputListView(view, mListener);
        setTouchHelper(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(RecyclerView.Adapter adapter, ListItemData item);

        void onListAdapterCreated(RecyclerView.Adapter adapter);
    }

    int from;
    int to;

    private void setTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder viewHolder1) {
                        from = viewHolder.getAdapterPosition();
                        to = viewHolder1.getAdapterPosition();
                        inputListCreator.getInputListRecyclerViewAdapter().notifyItemMoved(from, to);
                        try {
                            inputListCreator.move(recyclerView,
                                    inputListCreator.getInputListRecyclerViewAdapter().getItem(from),
                                    inputListCreator.getInputListRecyclerViewAdapter().getItem(to));
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    }

                    // move 終わりでよばれる
                    @Override
                    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        super.clearView(recyclerView, viewHolder);
                        viewHolder.itemView.setAlpha(1.0f);
                    }

                    // move 始まりと終わりで呼ばれる 始まりでactionState=2, 終わりで 0
                    @Override
                    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                        super.onSelectedChanged(viewHolder, actionState);
                        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                            viewHolder.itemView.setAlpha(0.5f);
                        }
                        // 終わりでなにもしない
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
