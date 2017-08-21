package junkuvo.apps.inputhelper.fragment.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import junkuvo.apps.inputhelper.R;

public class ListItemViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public ListItemData mItem;

    public ListItemViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        mIdView = (TextView) itemView.findViewById(R.id.id);
        mContentView = (TextView) itemView.findViewById(R.id.title);

    }

    @Override
    public String toString() {
        return super.toString() + " '" + mContentView.getText() + "'";
    }
}
