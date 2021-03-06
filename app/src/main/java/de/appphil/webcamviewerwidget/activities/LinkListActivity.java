package de.appphil.webcamviewerwidget.activities;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

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

        updateRecyclerView(false);

        if(firstOpen()) {
            ShowcaseView view = new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(btnAdd))
                    .setContentTitle(getResources().getString(R.string.showcase_title_add_link))
                    .setContentText(getResources().getString(R.string.showcase_text_add_link))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .withMaterialShowcase()
                    .build();
            view.setButtonText(getResources().getString(R.string.showcase_close));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
            layoutParams.setMargins(margin, margin, margin, margin);
            view.setButtonPosition(layoutParams);
        }
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
                    showLinkListEditAdapter();

                    editing = true;
                    item.setTitle(getResources().getString(R.string.ready_with_editing));
                } else {
                    updateRecyclerView(false);
                }
                return true;
            case R.id.menu_linklist_export:
                startExportActivity();
                return true;
            case R.id.menu_linklist_import:
                startImportActivity();
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
    private void updateRecyclerView(boolean editModeEnabled) {
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

        if(!editModeEnabled) {
            LinkListAdapter adapter = new LinkListAdapter(this, linklist, new RVOnItemClickListener() {
                @Override
                public void onItemClick(Link link) {
                    Log.d(TAG, "User clicked on link with id: " + link.getId() + " and name: " + link.getName());
                    Intent intent = new Intent(getApplicationContext(), LinkListViewImageActivity.class);
                    intent.putExtra(LinkListViewImageActivity.EXTRA_LINK_NAME, link.getName());
                    intent.putExtra(LinkListViewImageActivity.EXTRA_LINK_LINK, link.getLink());
                    startActivity(intent);
                }
            });
            rv.setAdapter(adapter);

            editing = false;

            if (menu != null) {
                menu.findItem(R.id.menu_linklist_edit).setTitle(getResources().getString(R.string.edit));
            }
        } else {
            showLinkListEditAdapter();
        }
    }

    private void showLinkListEditAdapter() {
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
    }

    /***
     * Starts the InfoActivity.
     */
    private void startInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    /***
     * Starts the ExportActivity.
     */
    private void startExportActivity() {
        Intent intent = new Intent(this, ExportActivity.class);
        startActivity(intent);
    }

    /***
     * Starts the ImportActivity.
     */
    private void startImportActivity() {
        Intent intent = new Intent(this, ImportActivity.class);
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
                updateRecyclerView(true);
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
                updateRecyclerView(true);

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

                if(name.isEmpty() || link.isEmpty()) {
                    return;
                }

                Log.d(TAG, "User input: " + name + " " + link);

                // add new Link objects to link database table
                linkDbManager.addLink(name, link);

                // update listview
                updateRecyclerView(false);

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

    /***
     * If this it's the first time the app gets opened.
     * @return
     */
    private boolean firstOpen() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean firstOpen = sharedPreferences.getBoolean("firstOpen", true);
        if (firstOpen) {
            // first time
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstOpen", false);
            editor.commit();
            return true;
        }
        return false;
    }

}
