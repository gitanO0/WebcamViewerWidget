package de.appphil.webcamviewerwidget.link;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;

public class LinkListAdapter extends ArrayAdapter<String> {

    private Context context;

    /***
     * ArrayList containing the Link objects.
     */
    private ArrayList<Link> linklist;

    public LinkListAdapter(Context context, ArrayList<Link> linklist, ArrayList<String> linkNames) {
        super(context, android.R.layout.simple_list_item_1, linkNames);
        this.context = context;
        this.linklist = linklist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean activated = isLinkActivated(getItem(position));

        View view = super.getView(position, convertView, parent);
        TextView tv = (TextView) view.findViewById(android.R.id.text1);

        if(activated) {
            tv.setTextColor(context.getResources().getColor(R.color.activated_text));
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.deactivated_text));
        }
        
        return view;
    }

    /***
     * Returns if the link with the given name is activated or not.
     * @param name
     * @return
     */
    private boolean isLinkActivated(String name) {
        for(Link link : linklist) {
            if(link.getName().equals(name)) {
                return link.isActivated();
            }
        }
        return false;
    }
}
