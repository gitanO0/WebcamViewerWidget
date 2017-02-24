package de.appphil.webcamviewerwidget.utils;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.link.Link;

public class CheckableListAdapter extends RecyclerView.Adapter<CheckableListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public CheckBox cb;

        public ViewHolder(View view) {
            super(view);
            this.tv = (TextView) view.findViewById(R.id.checkable_list_item_tv);
            this.cb = (CheckBox) view.findViewById(R.id.checkable_list_item_cb);
        }

        public void bind(String text) {
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.setChecked(!cb.isChecked());
                }
            });
        }

    }

    /***
     * ArrayList containing all the Link objects that should be shown in the recycler view.
     */
    private ArrayList<Link> links;

    private HashMap<Link, CheckBox> map;

    public CheckableListAdapter(ArrayList<Link> links) {
        this.links = links;
        map = new HashMap<Link, CheckBox>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkable_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Link link = links.get(position);
        holder.bind(link.getName());
        if(!map.containsKey(link)) {
            map.put(link, holder.cb);
        }
    }

    public ArrayList<Link> getCheckedLinks() {
        ArrayList<Link> checkedLinks = new ArrayList<>();
        for(Map.Entry<Link, CheckBox> entry : map.entrySet()) {
            if(entry.getValue().isChecked()) {
                checkedLinks.add(entry.getKey());
            }
        }
        return checkedLinks;
    }

    @Override
    public int getItemCount() {
        return links.size();
    }
}