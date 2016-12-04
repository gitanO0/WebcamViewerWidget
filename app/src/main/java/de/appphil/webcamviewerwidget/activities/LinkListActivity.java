package de.appphil.webcamviewerwidget.activities;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListAdapter;
import de.appphil.webcamviewerwidget.link.LinkListEditAdapter;
import de.appphil.webcamviewerwidget.link.LinkListIO;
import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.link.RVEditOnItemClickListener;
import de.appphil.webcamviewerwidget.link.RVOnItemClickListener;

public class LinkListActivity extends AppCompatActivity {

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
    private Button btnAdd;

    /***
     * Wheter the user edits the list or not.
     */
    private boolean editing = false;

    /***
     * Allows drag and drop to swap linklist items.
     */
    private ItemTouchHelper itemTouchHelper;

    /***
     * Toolbar.
     */
    private Toolbar toolbar;

    /***
     * Menu.
     */
    private Menu menu;

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


        // recyclerview to show the list
        rv = (RecyclerView) findViewById(R.id.linklist_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);

        btnAdd = (Button) findViewById(R.id.linklist_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLinkDialog();
            }
        });

        // load linklist
        try {
            linklist = LinkListIO.loadLinklist(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

                    // allow drag and drop to swap list items
                    ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.Callback() {
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            // get viewHolders and targets positions in adapter and then swap them
                            Collections.swap(linklist, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                            adapter.updateLinklist(linklist);
                            // notify the adapter that the order switched
                            adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                            // save linklist
                            try {
                                LinkListIO.saveLinklist(getApplicationContext(), linklist);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}

                        @Override
                        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                                    ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                        }
                    };

                    // Create ItemTouchHelper and attach it to the recyclerview
                    itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                    itemTouchHelper.attachToRecyclerView(rv);

                    editing = true;
                    item.setTitle(getResources().getString(R.string.ready_with_editing));
                } else {
                    updateRecyclerView();
                }
                return true;
            case R.id.menu_linklist_export:
                startExportActivity();
                return true;
            case R.id.menu_linklist_import:
                startImportActivity();
                return true;
            case R.id.menu_linklist_settings:
                startSettingsActivity();
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
        if(linklist.isEmpty()) return;

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

        // item swapping should not be working anymore
        if(itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
        }
    }

    /***
     * Starts the SettingsActivity.
     */
    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
                // update linklist
                linklist.set(getItemPosition(link), new Link(name, linkString, link.isEnabled()));
                // save linklist
                try {
                    LinkListIO.saveLinklist(getApplicationContext(), linklist);
                    // update listview
                    updateRecyclerView();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.edit_failed), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                // dismiss the dialog
                dialog.dismiss();
            }
        });

        Button btnDelete = (Button) dialog.findViewById(R.id.dialog_editlink_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss dialog
                dialog.dismiss();
                // show dialog and ask if the user really wants to delete the link
                showDeleteLinkDialog(link);
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
                // remove from array list
                linklist.remove(getItemPosition(link));

                // save linklist to file
                try {
                    LinkListIO.saveLinklist(getApplicationContext(), linklist);
                    // update listview
                    updateRecyclerView();
                } catch (Exception e) {
                    e.printStackTrace();
                    // show information to user
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_delete_link), Toast.LENGTH_LONG).show();
                }

                // dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /***
     * Returns the position of the given link in the linklist.
     * @param link Link object where the position is searched.
     * @return Position of the given link object in the linklist.
     */
    private int getItemPosition(Link link) {
        for(int i = 0; i < linklist.size(); i++) {
            if(linklist.get(i) == link) {
                return i;
            }
        }
        return -1; // Fail
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

                // name can't contain ":"
                if(name.contains(":")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.name_cant_contain) + " :", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                // add new Link objects to linklist
                linklist.add(new Link(name, link, true));

                // try to save linklist
                try {
                    LinkListIO.saveLinklist(getApplicationContext(), linklist);

                    // update listview
                    updateRecyclerView();
                } catch (Exception e) {
                    e.printStackTrace();
                    // show information to user
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_add_link), Toast.LENGTH_LONG).show();
                }

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
