package de.appphil.webcamviewerwidget.link;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;

public class LinkListEditAdapter extends RecyclerView.Adapter<LinkListEditAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public ImageView ivEdit;
        public ImageView ivDelete;

        public ViewHolder(View view) {
            super(view);
            this.tv = (TextView) view.findViewById(R.id.linklist_item_tv);
            this.ivEdit = (ImageView) view.findViewById(R.id.linklist_item_iv_edit);
            this.ivDelete = (ImageView) view.findViewById(R.id.linklist_item_iv_delete);
        }

        public void bind(Context context, final Link link, final RVEditOnItemClickListener listener) {
            tv.setText(link.getName());
            if(link.isEnabled()) {
                tv.setTextColor(context.getResources().getColor(R.color.enabled_text));
            } else {
                tv.setTextColor(context.getResources().getColor(R.color.disabled_text));
            }

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClickEdit(link);
                }
            });
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClickDelete(link);
                }
            });
        }

    }

    private Context context;

    /***
     * List containing the link objects.
     */
    private ArrayList<Link> linklist;

    private RVEditOnItemClickListener listener;

    public LinkListEditAdapter(Context context, ArrayList<Link> linklist, RVEditOnItemClickListener listener) {
        this.context = context;
        this.linklist = linklist;
        this.listener = listener;
    }

    @Override
    public LinkListEditAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.linklist_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(context, linklist.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return linklist.size();
    }
}
