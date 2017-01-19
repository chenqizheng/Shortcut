package com.yonyou.shortcut;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.util.List;


public class ActionListRecyclerViewAdapter extends RecyclerView.Adapter<ActionListRecyclerViewAdapter.ViewHolder> {

    private final List<Action> mValues;

    public ActionListRecyclerViewAdapter(List<Action> items) {
        mValues = items;
    }

    public void bind(List<Action> items) {
        if (mValues != null) {
            mValues.clear();
            mValues.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_password, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId() + "");
        holder.mContentView.setText(mValues.get(position).getName() + "  " + mValues.get(position).getValue());

        holder.mDelview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionDao(v.getContext()).remove(holder.mItem);
                ShortCutUtils.delShortcut(v.getContext(), mValues.get(position));
                bind(new ActionDao(v.getContext()).list());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final Button mDelview;
        public Action mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDelview = (Button) view.findViewById(R.id.del);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
