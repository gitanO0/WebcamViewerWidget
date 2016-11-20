package de.appphil.webcamviewerwidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ImportActivity extends Activity {

    /***
     * Button to import the entered data.
     */
    private Button btnImport;

    /***
     * EditText where the user can put in exported data.
     */
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

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
        ArrayList<Link> importedLinks = new ArrayList<Link>();
        try {
            String[] parts = input.split("\\}");
            for (String part : parts) {
                part = part.replace("{", "");
                String[] content = part.split(";");
                importedLinks.add(new Link(content[0], content[1], true));
                System.out.println("Name: " + content[0] + " Link: " + content[1]);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            showImportFailedToast();
        }

        if(importedLinks.isEmpty()) return;

        try {
            ArrayList<Link> linklist = LinkListIO.loadLinklist(getApplicationContext());
            linklist.addAll(importedLinks);
            LinkListIO.saveLinklist(getApplicationContext(), linklist);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_completed), Toast.LENGTH_LONG).show();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            showImportFailedToast();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            showImportFailedToast();
        }
    }

    /***
     * Shows a toast with a "failed to import" message.
     */
    private void showImportFailedToast() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_failed), Toast.LENGTH_LONG).show();
    }
}
