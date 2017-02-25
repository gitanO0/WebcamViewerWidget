package de.appphil.webcamviewerwidget.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.utils.CheckableListAdapter;


public class SelectMultipleLinksActivity extends AppCompatActivity {

    private static final String TAG = SelectMultipleLinksActivity.class.getSimpleName();
    public static final String CANCELED_CAUSE_LINKLIST_EMPTY = "linklist_empty";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectmultiplelinks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.select_link));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.selectmultiplelinks_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);

        LinkDbManager linkDbManager = new LinkDbManager(this);

        ArrayList<Link> links = linkDbManager.getAllLinks();
        if(links.isEmpty()) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", CANCELED_CAUSE_LINKLIST_EMPTY);
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }

        final CheckableListAdapter adapter = new CheckableListAdapter(linkDbManager.getAllLinks());
        rv.setAdapter(adapter);

        FloatingActionButton btnExport = (FloatingActionButton) findViewById(R.id.selectmultiplelinks_btn);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", adapter.getCheckedLinks());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }
}
