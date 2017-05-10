package org.theiner.tinycountdown.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.theiner.tinycountdown.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.theiner.tinycountdown.R.id.ivBackgroundImage;

public class OverviewActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "TinyCountdownFile";

    private TextView txtTage = null;
    private ImageView ivBackgroundImage = null;

    private boolean firstStart = true;

    private void zeigeWerte() throws ParseException {

        TextView txtTerminName = (TextView) findViewById(R.id.txtTerminName);
        ivBackgroundImage = (ImageView) findViewById(R.id.ivBackgroundImage);
        TextView txtTagenLiteral = (TextView) findViewById(R.id.txtTagenLiteral);

        Calendar heute = Calendar.getInstance();

        Calendar terminDatum = Calendar.getInstance();

        // hole Werte aus den Settings
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String strStartDatum = settings.getString("terminDatum", "01.08.2017");
        firstStart = settings.getBoolean("firstStart", true);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        terminDatum.setTime(sdf.parse(strStartDatum));

        String terminName = settings.getString("terminName", "");

        LocalDate heuteDate = LocalDate.fromCalendarFields(heute);
        LocalDate terminDate = LocalDate.fromCalendarFields(terminDatum);
        long days = Days.daysBetween(heuteDate, terminDate).getDays();

        String strDays = String.valueOf(days);

        txtTerminName.setText(terminName);
        txtTage.setText(strDays + (strDays.startsWith("1")?"  ":""));

        // 1 Tag
        if(days == 1)
            txtTagenLiteral.setText("Tag");
        else
            txtTagenLiteral.setText("Tagen");

        // Gibt es schon ein gecachetes Hintergrundbild?
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File fileDir = new File(sdDir, "TinyCountdownCache");

        String imageFilename = fileDir.getPath() + File.separator + "background.png";
        File imageFile = new File(imageFilename);
        if(imageFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            ivBackgroundImage.setImageBitmap(myBitmap);
        } else {
            // Nachricht anzeigen und evtl. in die Optionen wechseln, nur beim Erststart
            if(firstStart) {
                new AlertDialog.Builder(this)
                        .setTitle("Hintergrundbild")
                        .setMessage("Du hast noch kein Hintergrundbild festgelegt. Möchtest Du das jetzt tun?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                zeigeOptionen();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })

                        .show();
            }
        }
        firstStart = false;

        // Setting speichern
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstStart", false);
        editor.commit();
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

        txtTage = (TextView) findViewById(R.id.txtTage);
        AssetManager myassets = this.getAssets();
        Typeface tf = Typeface.createFromAsset(myassets, "fonts/DSEG7Classic-Bold.ttf" );
        txtTage.setTypeface(tf);

        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String strStartDatum = settings.getString("terminDatum", null);

            if(strStartDatum != null) {
                zeigeWerte();
            } else {
                firstStart = false;
                zeigeOptionen();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!firstStart) {
            try {
                zeigeWerte();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        if(id == R.id.action_share) {
            takeScreenshot();
        }

        return super.onOptionsItemSelected(item);
    }

    public void takeScreenshot() {
        // get RelativeLayout view and shoot it

        RelativeLayout rlScreen = (RelativeLayout) findViewById(R.id.rlScreen);

        Bitmap bitmap = Bitmap.createBitmap(rlScreen.getWidth(),
                rlScreen.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rlScreen.draw(canvas);

        try {
            File file = new File(this.getCacheDir(), "screenshot.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(Intent.createChooser(shareIntent, "Screenshot teilen"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
