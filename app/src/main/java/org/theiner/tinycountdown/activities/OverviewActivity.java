package org.theiner.tinycountdown.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.theiner.tinycountdown.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OverviewActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "TinyCountdownFile";

    private void zeigeWerte() throws ParseException {

        TextView txtTerminName = (TextView) findViewById(R.id.txtTerminName);
        TextView txtTage = (TextView) findViewById(R.id.txtTage);

        Calendar heute = Calendar.getInstance();

        Calendar terminDatum = Calendar.getInstance();

        // hole Werte aus den Settings
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String strStartDatum = settings.getString("terminDatum", "01.08.2017");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        terminDatum.setTime(sdf.parse(strStartDatum));

        String terminName = settings.getString("terminName", "");

        LocalDate heuteDate = LocalDate.fromCalendarFields(heute);
        LocalDate terminDate = LocalDate.fromCalendarFields(terminDatum);
        long days = Days.daysBetween(heuteDate, terminDate).getDays();

        txtTerminName.setText(terminName);
        txtTage.setText(String.valueOf(days));

    }

    private void zeigeOptionen() {
        Intent intent = new Intent(this, OptionActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String strStartDatum = settings.getString("terminDatum", null);

            if(strStartDatum != null) {
                zeigeWerte();
            } else {
                zeigeOptionen();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            zeigeWerte();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // OptionActivity aufrufen
            zeigeOptionen();
        }

        if(id == R.id.action_close) {
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

}
