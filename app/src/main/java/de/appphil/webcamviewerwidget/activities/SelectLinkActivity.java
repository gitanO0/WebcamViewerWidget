package de.appphil.webcamviewerwidget.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListAdapter;
import de.appphil.webcamviewerwidget.link.RVOnItemClickListener;

public class SelectLinkActivity extends AppCompatActivity {

    private static final String TAG = SelectLinkActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlink);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.select_link));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.selectlink_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);

        LinkDbManager linkDbManager = new LinkDbManager(this);

        LinkListAdapter adapter = new LinkListAdapter(this, linkDbManager.getAllLinks(), new RVOnItemClickListener() {
            @Override
            public void onItemClick(Link link) {
                Log.d(TAG, "User clicked on link with id: " + link.getId());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", (int)link.getId());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        rv.setAdapter(adapter);

    }
}
