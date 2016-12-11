package de.appphil.webcamviewerwidget.activities;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;

public class ImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.import_links));
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        final EditText et = (EditText) findViewById(R.id.import_et);

        FloatingActionButton btnImport = (FloatingActionButton) findViewById(R.id.import_btn_import);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importClicked(et.getText().toString());
            }
        });
    }

    /***
     * Gets called when the import button is clicked.
     * @param input
     */
    private void importClicked(String input) {
        // check if nothing entered
        if(input.isEmpty()) {
            showImportFailedToast();
            return;
        }

        LinkDbManager linkDbManager = new LinkDbManager(this);

        // try to "read" the input
        try {
            String[] parts = input.split("\\}");
            for (String part : parts) {
                part = part.replace("{", "");
                String[] content = part.split(";");
                linkDbManager.addLink(content[0], content[1]);
                System.out.println("Name: " + content[0] + " Link: " + content[1]);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            showImportFailedToast();
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
