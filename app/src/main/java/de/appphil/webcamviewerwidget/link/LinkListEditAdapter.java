package de.appphil.webcamviewerwidget.link;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;


public class LinkListEditAdapter extends BaseAdapter {

    private Context context;

    /***
     * ArrayList containing the Link objects.
     */
    private ArrayList<Link> linklist;

    /***
     * Listener for clicks on the edit and delete imageview.
     */
    private LinkListOnClickListener listener;

    public LinkListEditAdapter(Context context, ArrayList<Link> linklist, LinkListOnClickListener listener) {
        this.context = context;
        this.linklist = linklist;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        if(linklist != null) return linklist.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return linklist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.linklist_item, null);
        }
        boolean activated = linklist.get(position).isEnabled();

        TextView tv = (TextView) view.findViewById(R.id.linklist_item_tv);
        tv.setText(linklist.get(position).getName());

        if(activated) {
            tv.setTextColor(context.getResources().getColor(R.color.activated_text));
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.deactivated_text));
        }

        ImageView ivEdit = (ImageView) view.findViewById(R.id.linklist_item_iv_edit);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position, LinkListClickAction.EDIT);
            }
        });

        ImageView ivDelete = (ImageView) view.findViewById(R.id.linklist_item_iv_delete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position, LinkListClickAction.DELETE);
            }
        });

        return view;
    }
}
