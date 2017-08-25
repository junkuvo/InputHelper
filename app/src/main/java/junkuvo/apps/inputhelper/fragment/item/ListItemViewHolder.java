package junkuvo.apps.inputhelper.fragment.item;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import junkuvo.apps.inputhelper.R;

public class ListItemViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final AppCompatTextView mContentView;
    public ListItemData mItem;

    public ListItemViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        mContentView = (AppCompatTextView) itemView.findViewById(R.id.title);

    }

    @Override
    public String toString() {
        return super.toString() + " '" + mContentView.getText() + "'";
    }
}
