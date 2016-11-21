package de.appphil.webcamviewerwidget.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.R;

public class CheckBoxListViewAdapter extends BaseAdapter {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Link> linklist;

    public CheckBoxListViewAdapter(Context context, int layoutResourceId, ArrayList<Link> linklist) {
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.linklist = linklist;
    }

    @Override
    public int getCount() {
        return linklist.size();
    }

    @Override
    public Link getItem(int i) {
        return linklist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        CBHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, viewGroup, false);

            holder = new CBHolder();
            holder.cbAdapterCheckBox = (CheckBox)row.findViewById(R.id.cbAdapterCheckBox);

            row.setTag(holder);
        }
        else
        {
            holder = (CBHolder)row.getTag();
        }

        holder.cbAdapterCheckBox.setText(linklist.get(position).getName());
        holder.cbAdapterCheckBox.setClickable(false);

        return row;
    }

    public static class CBHolder
    {
        public CheckBox cbAdapterCheckBox;
    }
}
