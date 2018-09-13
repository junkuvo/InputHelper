package junkuvo.apps.inputhelper.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import junkuvo.apps.inputhelper.InputListActivity;
import junkuvo.apps.inputhelper.OverlayActivity;
import junkuvo.apps.inputhelper.R;
import junkuvo.apps.inputhelper.fragment.InputListFragment.OnListFragmentInteractionListener;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;
import junkuvo.apps.inputhelper.fragment.item.ListItemViewHolder;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ListItemData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class InputListRecyclerViewAdapter extends RealmRecyclerViewAdapter<ListItemData, ListItemViewHolder> {

    private OrderedRealmCollection<ListItemData> mListItemData;
    private OnListFragmentInteractionListener mListener;
    private boolean isEmpty = true;

    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * 空だったところに登録した時、Realmに参照を置き換える
     *
     * @param itemDatas
     */
    public void setRealmReferenceToAdapter(OrderedRealmCollection<ListItemData> itemDatas) {
        mListItemData = itemDatas;
        if (itemDatas != null && itemDatas.size() > 0) {
            isEmpty = false;
        }
    }

    public void setEmptyLayout() {
        ListItemData itemData = new ListItemData();
        itemData.setDetails("右下のボタンから\nよく使うデータや忘れたくないことをメモしておきましょう♪");
        mListItemData = new RealmList<>();
        mListItemData.add(itemData);
        isEmpty = true;
    }

    public InputListRecyclerViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<ListItemData> items, boolean autoUpdate, OnListFragmentInteractionListener listener) {
        super(context, items, autoUpdate);
        commonConstructor(items, listener);
    }

    private boolean fromNotification = false;

    public InputListRecyclerViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<ListItemData> items, boolean autoUpdate, OnListFragmentInteractionListener listener, boolean fromNotification) {
        super(context, items, autoUpdate);
        commonConstructor(items, listener);
        this.fromNotification = fromNotification;
        setHasStableIds(true);
    }

    private void commonConstructor(@Nullable OrderedRealmCollection<ListItemData> items, OnListFragmentInteractionListener listener){
        mListener = listener;
        mListItemData = items;

        if (items != null) {
            if (items.size() > 0) {
                isEmpty = false;
            } else {
                setEmptyLayout();
            }
        }
        mListener.onListAdapterCreated(this);
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        final ListItemViewHolder viewHolder = (ListItemViewHolder) holder;
        viewHolder.mItem = mListItemData.get(position);
        viewHolder.mContentView.setText(mListItemData.get(position).getDetails());

        if (!isEmpty) {
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
        } else {
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), InputListActivity.class);
                    view.getContext().startActivity(intent);
                    if (view.getContext() instanceof OverlayActivity) {
                        ((OverlayActivity) view.getContext()).finish();
                    }
                }
            });
        }

        if(fromNotification){
            viewHolder.ivCopy.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivCopy.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mListItemData.size();
    }

    @Override
    public long getItemId(int index) {
        return mListItemData.get(index).getId();
    }
}
