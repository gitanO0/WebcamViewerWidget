package de.appphil.webcamviewerwidget;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.Calendar;

import de.appphil.webcamviewerwidget.services.WidgetUpdateService;
import de.appphil.webcamviewerwidget.utils.PendingIntents;
import de.appphil.webcamviewerwidget.utils.Vars;

public class SettingsActivity extends Activity {

    /***
     * Checkbox to select if the widget should automatically update the image.
     */
    private CheckBox cbAutoUpdateWidget;

    /***
     * Layout with more settings about auto update.
     * This is visible only when cbAutoUpdateWidget is checked.
     */
    private RelativeLayout autoUpdateWidgetLayout;

    /***
     * EditText to select the auto update interval.
     */
    private EditText etAutoUpdateInterval;

    /***
     * CheckBox to select if the widget should only update when wifi is enabled.
     */
    private CheckBox cbOnlyUpdateWithWifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        autoUpdateWidgetLayout = (RelativeLayout) findViewById(R.id.settings_auto_update_widget_layout);

        final SharedPreferences sharedPref = this.getSharedPreferences(Vars.PREFS, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        boolean autoUpdate = sharedPref.getBoolean(Vars.AUTO_UPDATE_WIDGET, false);
        if(autoUpdate) autoUpdateWidgetLayout.setVisibility(View.VISIBLE);
        boolean onlyUpdateWithWifi = sharedPref.getBoolean(Vars.ONLY_UPDATE_WITH_WIFI, true);
        int autoUpdateInterval = sharedPref.getInt(Vars.AUTO_UPDATE_INTERVAL, 1);

        cbAutoUpdateWidget = (CheckBox) findViewById(R.id.settings_cb_auto_update_widget);
        cbAutoUpdateWidget.setChecked(autoUpdate);
        cbAutoUpdateWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(Vars.AUTO_UPDATE_WIDGET, b);
                editor.commit();
                if(b) {
                    autoUpdateWidgetLayout.setVisibility(View.VISIBLE);
                    setAlarm(Integer.parseInt(etAutoUpdateInterval.getText().toString()));
                } else {
                    autoUpdateWidgetLayout.setVisibility(View.GONE);
                    cancelAlarm();
                }
            }
        });

        etAutoUpdateInterval = (EditText) findViewById(R.id.settings_et_auto_update_interval);

        cbOnlyUpdateWithWifi = (CheckBox) findViewById(R.id.settings_cb_only_update_with_wifi);
        cbOnlyUpdateWithWifi.setChecked(onlyUpdateWithWifi);
        cbOnlyUpdateWithWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(Vars.ONLY_UPDATE_WITH_WIFI, b);
                editor.commit();
            }
        });

        etAutoUpdateInterval = (EditText) findViewById(R.id.settings_et_auto_update_interval);
        etAutoUpdateInterval.setText("" + autoUpdateInterval);
        etAutoUpdateInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1)return;
                int interval = Integer.parseInt(charSequence.toString());
                editor.putInt(Vars.AUTO_UPDATE_INTERVAL, interval);
                editor.commit();
                setAlarm(interval);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setAlarm(int interval) {
        // first: cancel other alarm
        cancelAlarm();

        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        // set new alarm
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * interval, PendingIntents.getUpdateWidgetPendingIntent(getApplicationContext(), true));
    }

    private void cancelAlarm() {
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(PendingIntents.getUpdateWidgetPendingIntent(getApplicationContext(), true));
    }
}
