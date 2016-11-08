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
    }
}
