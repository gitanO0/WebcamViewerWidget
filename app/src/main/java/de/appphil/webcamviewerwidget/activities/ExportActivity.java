package de.appphil.webcamviewerwidget.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListIO;
import de.appphil.webcamviewerwidget.utils.CheckBoxListViewAdapter;

public class ExportActivity extends AppCompatActivity {

    /***
     * ListView with the names of the links.
     */
    private ListView lv;

    /***
     * Custom adapter with checkboxes.
     */
    private CheckBoxListViewAdapter adapter;

    /***
     * Button to export the selected links.
     */
    private Button btnExport;

    /***
     * Manager for the link database.
     */
    private LinkDbManager linkDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        linkDbManager = new LinkDbManager(this);

        // update toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.export_links));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        lv = (ListView) findViewById(R.id.export_lv);

        // get the linklist
        ArrayList<Link> linklist = linkDbManager.getAllLinks();

        // create and set custom listview adapter to listview
        adapter = new CheckBoxListViewAdapter(this, R.layout.checkbox_listview_item, linklist);
        lv.setAdapter(adapter);

        // set onitemclicklistener to listview which changes state of checkbox when clicked
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // update checkbox
                CheckBoxListViewAdapter.CBHolder viewHolder = (CheckBoxListViewAdapter.CBHolder) view.getTag();
                viewHolder.cbAdapterCheckBox.setChecked(!viewHolder.cbAdapterCheckBox.isChecked());
                // get link object
                Link link = adapter.getItem(position);
                // change checked state of link object
                link.setChecked(viewHolder.cbAdapterCheckBox.isChecked());
            }
        });

        btnExport = (Button) findViewById(R.id.export_btn_export);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnExportClicked();
            }
        });

    }

    /***
     * Gets called when the export button is clicked.
     */
    private void btnExportClicked() {
        String export = "";
        for(int i = 0; i < adapter.getCount(); i++) {
            // get the Link object
            Link link = adapter.getItem(i);
            // check if this link should be exported
            if(link.isChecked()) {
                export = export + "{" + link.getName() + ";" + link.getLink() + "}";
            }
        }
        // copy to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Export", export);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show();
        finish();
    }
}
