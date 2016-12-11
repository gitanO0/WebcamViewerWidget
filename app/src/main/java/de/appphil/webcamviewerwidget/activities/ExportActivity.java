package de.appphil.webcamviewerwidget.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.utils.CheckableListAdapter;

public class ExportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.export_links));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.export_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);

        LinkDbManager linkDbManager = new LinkDbManager(this);

        ArrayList<Link> links = linkDbManager.getAllLinks();
        final CheckableListAdapter adapter = new CheckableListAdapter(links);
        rv.setAdapter(adapter);

        FloatingActionButton btnExport = (FloatingActionButton) findViewById(R.id.export_btn_export);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportClicked(adapter.getCheckedLinks());
            }
        });
    }

    /***
     * Gets called when the export button is clicked.
     * @param checkedLinks
     */
    private void exportClicked(ArrayList<Link> checkedLinks) {
        String exportedLinksString = "";
        for(Link link : checkedLinks) {
            exportedLinksString += linkToString(link);
        }
        // copy this string to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Exported links", exportedLinksString);
        clipboard.setPrimaryClip(clip);
        // show toast
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show();

        finish();
    }

    /***
     * Puts the name and link of the Link object into a string.
     * @param link
     * @return
     */
    private String linkToString(Link link) {
        return "{" + link.getName() + ";" + link.getLink() + "}";
    }
}
