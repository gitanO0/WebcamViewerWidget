package de.appphil.webcamviewerwidget.widgets.switchwidget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.RVEditOnItemClickListener;

public class SwitchWidgetLinksEditAdapter extends RecyclerView.Adapter<SwitchWidgetLinksEditAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public ImageView ivDelete;

        public ViewHolder(View view) {
            super(view);
            this.tv = (TextView) view.findViewById(R.id.linklist_item_delete_only_tv);
            this.ivDelete = (ImageView) view.findViewById(R.id.linklist_item_delete_only_iv_delete);
        }

        public void bind(final Link link, final RVEditOnItemClickListener listener) {
            tv.setText(link.getName());

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

    public SwitchWidgetLinksEditAdapter(Context context, ArrayList<Link> linklist, RVEditOnItemClickListener listener) {
        this.context = context;
        this.linklist = linklist;
        this.listener = listener;
    }

    /***
     * Sets the linklist.
     * @param linklist ArrayList with all the Link objects.
     */
    public void updateLinklist(ArrayList<Link> linklist) {
        this.linklist = linklist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.linklist_item_delete_only, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(linklist.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return linklist.size();
    }
}