package de.appphil.webcamviewerwidget.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.link.Link;
import de.appphil.webcamviewerwidget.link.LinkListIO;
import de.appphil.webcamviewerwidget.R;

public class ImportActivity extends AppCompatActivity {

    /***
     * Button to import the entered data.
     */
    private Button btnImport;

    /***
     * EditText where the user can put in exported data.
     */
    private EditText et;

    /***
     * Manager for link database.
     */
    private LinkDbManager linkDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        linkDbManager = new LinkDbManager(this);

        // update toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.import_links));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        et = (EditText) findViewById(R.id.import_et);

        btnImport = (Button) findViewById(R.id.import_btn_import);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnImportClicked();
            }
        });
    }

    /***
     * Gets called when user clicks import button.
     * Tries to add the given links to the list.
     */
    private void btnImportClicked() {
        // get user input
        String input = et.getText().toString();

        // check if nothing entered
        if(input.isEmpty()) {
            showImportFailedToast();
            return;
        }

        // try to "read" the input
        ArrayList<Link> importedLinks = new ArrayList<>();
        try {
            String[] parts = input.split("\\}");
            for (String part : parts) {
                part = part.replace("{", "");
                String[] content = part.split(";");
                importedLinks.add(new Link(0, content[0], content[1], true));
                System.out.println("Name: " + content[0] + " Link: " + content[1]);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            showImportFailedToast();
        }

        if(importedLinks.isEmpty()) return;

        for(Link link : importedLinks) {
            linkDbManager.addLink(link.getName(), link.getLink());
        }

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_completed), Toast.LENGTH_LONG).show();
        finish();
    }

    /***
     * Shows a toast with a "failed to import" message.
     */
    private void showImportFailedToast() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_failed), Toast.LENGTH_LONG).show();
    }
}
