package de.appphil.webcamviewerwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    /***
     * Button to start LinkListActivity.
     */
    private Button btnLinklist;

    /***
     * Button to start SettingsActivity.
     */
    private Button btnSettings;

    /***
     * Button to start InfoActivity.
     */
    private Button btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLinklist = (Button) findViewById(R.id.menu_btn_linklist);
        btnLinklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start LinkListActivity
                Intent intent = new Intent(getApplicationContext(), LinkListActivity.class);
                startActivity(intent);
            }
        });

        btnSettings = (Button) findViewById(R.id.menu_btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start SettingsActivity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnInfo = (Button) findViewById(R.id.menu_btn_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start InfoActivity
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
