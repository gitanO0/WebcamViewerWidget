package de.appphil.webcamviewerwidget.activities;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RemoteViews;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.db.LinkDbManager;
import de.appphil.webcamviewerwidget.widgets.singleautoupdatewidget.SAUWidgetUpdateService;


/**
 * Created by Philipp on 22.12.2016.
 */

public class SAUWidgetConfigActivity extends AppCompatActivity {

    private static final String TAG = SAUWidgetConfigActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 35242;

    private int appWidgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // let the user select a link
        Intent i = new Intent(this, SelectLinkActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            int linkId = data.getExtras().getInt("result");
            Log.d(TAG, "got result linkId: " + linkId);
            // add this link to the db table
            LinkDbManager linkDbManager = new LinkDbManager(this);
            linkDbManager.addSingleAutoUpdateWidget(appWidgetId, linkId);

            // update widget (cause onUpdate is not called because of the configuration acitivity)
            Intent updateService = new Intent(this, SAUWidgetUpdateService.class);
            updateService.putExtra("id", appWidgetId);
            startService(updateService);

            // return intent
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }
}
