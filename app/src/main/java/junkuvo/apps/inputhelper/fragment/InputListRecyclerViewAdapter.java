package junkuvo.apps.inputhelper.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import junkuvo.apps.inputhelper.R;
import junkuvo.apps.inputhelper.fragment.InputListFragment.OnListFragmentInteractionListener;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.fragment.item.ListItemViewHolder;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ListItemData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class InputListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ListItemData> mListItemData;
    private final OnListFragmentInteractionListener mListener;

    public InputListRecyclerViewAdapter(List<ListItemData> items, OnListFragmentInteractionListener listener) {
        mListener = listener;
        if(items.size() > 0) {
            mListItemData = items;
        }else{
            ListItemData itemData = new ListItemData();
            itemData.setDetails("右下のボタンから\nよく使うデータを登録しておきましょう♪");
            mListItemData = new ArrayList<>();
            mListItemData.add(itemData);
            mListener.onListEmpty();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ListItemViewHolder viewHolder = (ListItemViewHolder) holder;
        viewHolder.mItem = mListItemData.get(position);
        viewHolder.mContentView.setText(mListItemData.get(position).getDetails());

        if(mListItemData.get(position).getId() != 0) {
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(InputListRecyclerViewAdapter.this, viewHolder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListItemData.size();
    }

}
