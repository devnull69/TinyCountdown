package org.theiner.tinycountdown.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.widget.RemoteViews;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.theiner.tinycountdown.R;
import org.theiner.tinycountdown.activities.OverviewActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.theiner.tinycountdown.activities.OverviewActivity.PREFS_NAME;

/**
 * Created by TTheiner on 16.12.2016.
 */

public class TinyWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        try {
            for (int i = 0; i < count; i++) {
                int widgetId = appWidgetIds[i];

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.widget_overview);

                // hole Werte aus den Settings
                Calendar heute = Calendar.getInstance();

                Calendar terminDatum = Calendar.getInstance();

                SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                String strStartDatum = settings.getString("terminDatum", "01.08.2017");
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                terminDatum.setTime(sdf.parse(strStartDatum));

                LocalDate heuteDate = LocalDate.fromCalendarFields(heute);
                LocalDate terminDate = LocalDate.fromCalendarFields(terminDatum);
                long days = Days.daysBetween(heuteDate, terminDate).getDays();

                // Text setzen
                remoteViews.setImageViewBitmap(R.id.ivTextImage, getTageAlsBitmap(context, String.valueOf(days)));

                // Hintergrundbild setzen
                File sdDir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File fileDir = new File(sdDir, "TinyCountdownCache");

                String imageFilename = fileDir.getPath() + File.separator + "background.png";
                File imageFile = new File(imageFilename);
                if(imageFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    remoteViews.setImageViewBitmap(R.id.ivBackgroundImage, myBitmap);
                }

                // Click auf Image löst App aus
                Intent intent = new Intent(context, OverviewActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                remoteViews.setOnClickPendingIntent(R.id.ivTextImage, pendingIntent);

                appWidgetManager.updateAppWidget(widgetId, remoteViews);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getTageAlsBitmap(Context context, String tage)
    {
        Bitmap myBitmap = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/DSEG7Classic-Bold.ttf" );
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(tf);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, 222, 40, 61));
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(tage, 100, 75, paint);
        return myBitmap;
    }
}
