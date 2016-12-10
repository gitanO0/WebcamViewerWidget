package de.appphil.webcamviewerwidget.activities;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListAdapter;
import de.appphil.webcamviewerwidget.link.LinkListEditAdapter;
import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.link.RVEditOnItemClickListener;
import de.appphil.webcamviewerwidget.link.RVOnItemClickListener;

public class LinkListActivity extends AppCompatActivity {

    private static final String TAG = LinkListActivity.class.getSimpleName();

    /***
     * RecyclerView to show the linklist.
     */
    private RecyclerView rv;

    /***
     * Contains the link objects.
     */
    private ArrayList<Link> linklist;

    /***
     * Button to add a link to the list.
     */
    private FloatingActionButton btnAdd;

    /***
     * Wheter the user edits the list or not.
     */
    private boolean editing = false;

    /***
     * Toolbar.
     */
    private Toolbar toolbar;

    /***
     * Menu.
     */
    private Menu menu;

    /***
     * Database manager for links.
     */
    private LinkDbManager linkDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linklist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.linklist));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        linkDbManager = new LinkDbManager(this);

        // recyclerview to show the list
        rv = (RecyclerView) findViewById(R.id.linklist_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);

        btnAdd = (FloatingActionButton) findViewById(R.id.linklist_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLinkDialog();
            }
        });

        updateRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_linklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_linklist_edit:
                if(!editing) {
                    final LinkListEditAdapter adapter = new LinkListEditAdapter(getApplicationContext(), linklist, new RVEditOnItemClickListener() {
                        @Override
                        public void onItemClickEdit(Link link) {
                            showEditLinkDialog(link);
                        }

                        @Override
                        public void onItemClickDelete(Link link) {
                            showDeleteLinkDialog(link);
                        }
                    });
                    rv.setAdapter(adapter);

                    editing = true;
                    item.setTitle(getResources().getString(R.string.ready_with_editing));
                } else {
                    updateRecyclerView();
                }
                return true;
            case R.id.menu_linklist_info:
                startInfoActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Puts the link names of the linklist hashmap in the listview.
     */
    private void updateRecyclerView() {
        // load links from database
        linklist = linkDbManager.getAllLinks();

        if(linklist.isEmpty()) {
            Log.d(TAG, "Linklist from db is empty.");
        } else {
            Log.d(TAG, "Linklist:");
            for(Link link : linklist) {
                Log.d(TAG, link.getId() + " " + link.getName() + " " + link.getLink());
            }
        }

        LinkListAdapter adapter = new LinkListAdapter(this, linklist, new RVOnItemClickListener() {
            @Override
            public void onItemClick(Link link) {
                // nothing at the moment
            }
        });
        rv.setAdapter(adapter);

        editing = false;

        if(menu != null) {
            menu.findItem(R.id.menu_linklist_edit).setTitle(getResources().getString(R.string.edit));
        }
    }

    /***
     * Starts the InfoActivity.
     */
    private void startInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    /***
     * Shows the dialog to edit or delete the selected link.
     * @param link Selected link object.
     */
    private void showEditLinkDialog(final Link link) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_editlink);

        final EditText etName = (EditText) dialog.findViewById(R.id.dialog_editlink_et_name);
        etName.setText(link.getName());
        final EditText etLink = (EditText) dialog.findViewById(R.id.dialog_editlink_et_link);
        etLink.setText(link.getLink());

        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_editlink_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss the dialog
                dialog.dismiss();
            }
        });

        Button btnSave = (Button) dialog.findViewById(R.id.dialog_editlink_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get name and link
                String name = etName.getText().toString();
                String linkString = etLink.getText().toString();
                // get id of link in database table
                long id = linkDbManager.getIdByName(link.getName());
                // update linklist
                linkDbManager.updateLinkById(id, name, linkString);
                // update listview
                updateRecyclerView();
                // dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /***
     * Shows the dialog which asks if the selected link should deleted.
     * @param link Selected link object.
     */
    private void showDeleteLinkDialog(final Link link) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_deletelink);

        Button btnNo = (Button) dialog.findViewById(R.id.dialog_deletelink_btn_no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss the dialog
                dialog.dismiss();
            }
        });

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_deletelink_btn_yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get id from the link in database table
                long id = linkDbManager.getIdByName(link.getName());
                // remove from database table
                linkDbManager.deleteLinkById(id);
                // update listview
                updateRecyclerView();

                // dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /***
     * Shows the dialog to add a new link to the list.
     */
    private void showAddLinkDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_addlink);

        final EditText etName = (EditText) dialog.findViewById(R.id.dialog_addlink_et_name);
        final EditText etLink = (EditText) dialog.findViewById(R.id.dialog_addlink_et_link);

        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_addlink_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                dialog.dismiss();
            }
        });

        Button btnAdd = (Button) dialog.findViewById(R.id.dialog_addlink_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get input from edittexts
                String name = etName.getText().toString();
                String link = etLink.getText().toString();

                Log.d(TAG, "User input: " + name + " " + link);

                // add new Link objects to link database table
                linkDbManager.addLink(name, link);

                // update listview
                updateRecyclerView();

                // dismiss the dialog
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        // restart to update listview
        finish();
        startActivity(getIntent());
    }

}
