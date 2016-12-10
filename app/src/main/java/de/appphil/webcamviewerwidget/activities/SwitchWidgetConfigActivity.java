package de.appphil.webcamviewerwidget.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.db.SwitchWidgetLinksRow;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListAdapter;
import de.appphil.webcamviewerwidget.link.LinkListEditAdapter;
import de.appphil.webcamviewerwidget.link.RVEditOnItemClickListener;
import de.appphil.webcamviewerwidget.link.RVOnItemClickListener;
import de.appphil.webcamviewerwidget.widgets.switchwidget.SwitchWidgetLinksEditAdapter;

public class SwitchWidgetConfigActivity extends AppCompatActivity {

    private static final String TAG = SwitchWidgetConfigActivity.class.getSimpleName();

    /***
     * Button to add a link to the list.
     */
    private Button btnAdd;

    /***
     * View to shows the links.
     */
    private RecyclerView rv;

    /***
     * Wheter the user edits the list or not.
     */
    private boolean editing = false;

    private LinkDbManager linkDbManager;

    private int appWidgetId;

    private static final int REQUEST_CODE = 3552;

    private ArrayList<Link> linklist;

    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switchwidgetconfig);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.config));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        appWidgetId = getIntent().getIntExtra("id", 0);
        Log.d(TAG, "Started switch widget confi activity for widget with id: " + appWidgetId);

        btnAdd = (Button) findViewById(R.id.switchwidgetconfig_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), SelectLinkActivity.class), REQUEST_CODE);
            }
        });

        rv = (RecyclerView) findViewById(R.id.switchwidgetconfig_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);

        linkDbManager = new LinkDbManager(this);

        // check if this widget is added to the database already
        if(!linkDbManager.isSwitchWidgetSaved(appWidgetId)) {
            linkDbManager.addSwitchWidget(appWidgetId, -1);
        } else {
            updateRecyclerView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            int linkId = data.getExtras().getInt("result");
            Log.d(TAG, "SwitchWidgetConfigActivity got result linkId: " + linkId);
            // add this link to the widgets linklist
            linkDbManager.addLinkToSwitchWidgetLinklist(appWidgetId, linkId);

            updateRecyclerView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_switchwidgetconfig, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_switchwidgetconfig_edit:
                if(!editing) {
                    final SwitchWidgetLinksEditAdapter adapter = new SwitchWidgetLinksEditAdapter(getApplicationContext(), linklist, new RVEditOnItemClickListener() {
                        @Override
                        public void onItemClickEdit(Link link) {
                            // not needed here
                        }
                        @Override
                        public void onItemClickDelete(Link link) {
                            // remove link with the given id from the db table
                            linkDbManager.deleteLinkFromSwitchWidgetLinklist(appWidgetId, (int)link.getId());
                            updateRecyclerView();
                            menu.findItem(R.id.menu_switchwidgetconfig_edit).setTitle(getResources().getString(R.string.edit));
                            editing = false;
                        }
                    });
                    rv.setAdapter(adapter);

                    editing = true;
                    item.setTitle(getResources().getString(R.string.ready_with_editing));
                } else {
                    updateRecyclerView();
                    item.setTitle(getResources().getString(R.string.edit));
                    editing = false;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Gets the links from db and shows them in rv.
     */
    private void updateRecyclerView() {
        ArrayList<SwitchWidgetLinksRow> rows = linkDbManager.getSwitchWidgetLinksRowsByWidgetId(appWidgetId);
        Log.d(TAG, "Rows: " + rows.size());
        for(SwitchWidgetLinksRow row : rows) {
            Log.d(TAG, "Row: " + row.getSwitchWidgetId() + " " + row.getLinkId() + " " + row.getPos());
        }

        linklist = rowsToLinks(rows);
        LinkListAdapter adapter = new LinkListAdapter(this, linklist, new RVOnItemClickListener() {
            @Override
            public void onItemClick(Link link) {

            }
        });
        rv.setAdapter(adapter);
    }

    private ArrayList<Link> rowsToLinks(ArrayList<SwitchWidgetLinksRow> rows) {
        ArrayList<Link> links = new ArrayList<>();
        for(SwitchWidgetLinksRow row : rows) {
            Link link = linkDbManager.getLinkById(row.getLinkId());
            if(link != null) {
                links.add(link);
            }
        }
        return links;
    }
}