package de.appphil.webcamviewerwidget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.utils.CurrentLink;

public class LinkListActivity extends Activity {

    /***
     * Button to show a dialog to add a new link to the list.
     */
    private Button btnAddLink;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linklist);

        btnAddLink = (Button) findViewById(R.id.linklist_btn_add_link);
        btnAddLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLinkDialog();
            }
        });

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
                showDeleteLinkDialog(itemPosition);
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
        ArrayList<String> names = new ArrayList<String>();
        for(Link link : linklist) {
            names.add(link.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);
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
                linklist.add(new Link(name, link));

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
