package de.appphil.webcamviewerwidget.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListAdapter;
import de.appphil.webcamviewerwidget.link.LinkListClickAction;
import de.appphil.webcamviewerwidget.link.LinkListEditAdapter;
import de.appphil.webcamviewerwidget.link.LinkListIO;
import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.link.LinkListOnClickListener;
import de.appphil.webcamviewerwidget.utils.CurrentLink;

public class LinkListActivity extends Activity {

    /***
     * TextView which acts as a button for editing links.
     */
    private TextView tvEdit;

    /***
     * Button to export links.
     */
    private Button btnExport;

    /***
     * Button to import links.
     */
    private Button btnImport;

    /***
     * ListView to shows the linklist.
     */
    private ListView lv;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linklist);

        // ActionBar
        // first: Get the custom actionbar view
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.actionbar_linklist, null);

        // get the textview which acts as a button to edit the linklist
        tvEdit = (TextView) view.findViewById(R.id.actionbar_linklist_tv_edit);
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editing) {
                    LinkListEditAdapter adapter = new LinkListEditAdapter(getApplicationContext(), linklist, new LinkListOnClickListener() {
                        @Override
                        public void onClick(int position, LinkListClickAction action) {
                            if (action == LinkListClickAction.EDIT) {
                                showEditLinkDialog(position);
                            } else if (action == LinkListClickAction.DELETE) {
                                showDeleteLinkDialog(position);
                            }
                        }
                    });
                    lv.setAdapter(adapter);
                    editing = true;
                    tvEdit.setText(getResources().getString(R.string.ready_with_editing));
                } else {
                    updateListView();
                }
            }
        });
        // set the custom view
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(view);


        btnExport = (Button) findViewById(R.id.linklist_btn_export);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExportActivity();
            }
        });

        btnImport = (Button) findViewById(R.id.linklist_btn_import);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImportActivity();
            }
        });

        lv = (ListView) findViewById(R.id.linklist_lv);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                showEditLinkDialog(itemPosition);
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // item of list view was clicked
                // this should be the current link
                CurrentLink.saveCurrentLinkName(getApplicationContext(), linklist.get(i).getName());
            }
        });

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        updateListView();
    }

    /***
     * Gets called when the user clicks on the export button.
     * Starts the ExportActivity.
     */
    private void startExportActivity() {
        Intent intent = new Intent(this, ExportActivity.class);
        startActivity(intent);
    }

    /***
     * Gets called when the user clicks on the import button.
     * Starts the ImportActivity.
     */
    private void startImportActivity() {
        Intent intent = new Intent(this, ImportActivity.class);
        startActivity(intent);
    }

    /***
     * Puts the link names of the linklist hashmap in the listview.
     */
    private void updateListView() {
        if(linklist.isEmpty()) return;

        // get array list with link names
        ArrayList<String> names = getLinkNames();

        LinkListAdapter adapter = new LinkListAdapter(this, linklist, names);
        lv.setAdapter(adapter);

        editing = false;
        tvEdit.setText(getResources().getString(R.string.edit));
    }

    /***
     * Returns an arraylist with the names of the links in the linklist.
     * @return
     */
    private ArrayList<String> getLinkNames() {
        ArrayList<String> names = new ArrayList<String>();
        for(Link link : linklist) {
            names.add(link.getName());
        }
        return names;
    }

    /***
     * Shows the dialog to edit or delete the selected link.
     * @param itemPosition Position of the selected link.
     */
    private void showEditLinkDialog(final int itemPosition) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_editlink);

        Link link = linklist.get(itemPosition);

        final EditText etName = (EditText) dialog.findViewById(R.id.dialog_editlink_et_name);
        etName.setText(link.getName());
        final EditText etLink = (EditText) dialog.findViewById(R.id.dialog_editlink_et_link);
        etLink.setText(link.getLink());

        final CheckBox cbActivated = (CheckBox) dialog.findViewById(R.id.dialog_editlink_cb_activated);
        cbActivated.setChecked(link.isEnabled());

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
                String link = etLink.getText().toString();
                // check if link should be activated
                boolean activated = cbActivated.isChecked();
                // update linklist
                linklist.set(itemPosition, new Link(name, link, activated));
                // save linklist
                try {
                    LinkListIO.saveLinklist(getApplicationContext(), linklist);
                    // update listview
                    updateListView();
                } catch (IOException e) {
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
                showDeleteLinkDialog(itemPosition);
            }
        });

        dialog.show();
    }

    /***
     * Shows the dialog which asks if the selected link should deleted.
     * @param itemPosition Position of the selected link.
     */
    private void showDeleteLinkDialog(final int itemPosition) {
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
                linklist.remove(itemPosition);

                // save linklist to file
                try {
                    LinkListIO.saveLinklist(getApplicationContext(), linklist);
                    // update listview
                    updateListView();
                } catch (IOException e) {
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
                    updateListView();
                } catch (IOException e) {
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
