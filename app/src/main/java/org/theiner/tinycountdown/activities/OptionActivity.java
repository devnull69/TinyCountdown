package org.theiner.tinycountdown.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.theiner.tinycountdown.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OptionActivity extends Activity {

    private int year;
    private int month;
    private int day;

    private TextView editTerminDatum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        editTerminDatum = (TextView) findViewById(R.id.editTerminDatum);
        EditText editTerminName = (EditText) findViewById(R.id.editTerminName);

        SharedPreferences settings = getSharedPreferences(OverviewActivity.PREFS_NAME, 0);
        String strStartDatum = settings.getString("terminDatum", null);

        if(strStartDatum == null) {
            Button btnCancel = (Button) findViewById(R.id.btnCancel);
            btnCancel.setEnabled(false);
        }

        strStartDatum = settings.getString("terminDatum", "");
        String terminName = settings.getString("terminName", "");

        editTerminDatum.setText(strStartDatum);
        editTerminName.setText(String.valueOf(terminName));

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if(!"".equals(strStartDatum)) {
            day = Integer.parseInt(strStartDatum.substring(0,2));
            month = Integer.parseInt(strStartDatum.substring(3,5)) - 1;
            year = Integer.parseInt(strStartDatum.substring(6,10));
        } else {
            setDate(editTerminDatum);
        }

    }

    public void btnOk_Click(View view) {
        // Prüfen, ob gültiges Datum gewählt wurde
        EditText editTerminName = (EditText) findViewById(R.id.editTerminName);

        String strStartDatum = editTerminDatum.getText().toString();

        SharedPreferences settings = getSharedPreferences(OverviewActivity.PREFS_NAME, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date startDatum = new Date();
        try {
            startDatum = sdf.parse(strStartDatum);
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
                startDatum = sdf2.parse(strStartDatum);
            } catch (ParseException e1) {
                Toast toast = Toast.makeText(getApplicationContext(), "Ungültiges Datum!", Toast.LENGTH_SHORT);
                toast.show();
                e1.printStackTrace();
            }
        }
        try {
            Date heute = new Date();
            if(heute.getTime() - startDatum.getTime() > 0)
                throw new ParseException("Date must be in the future", 0);

            String terminName = editTerminName.getText().toString();

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("terminDatum", sdf.format(startDatum));
            editor.putString("terminName", terminName);
            editor.commit();
            this.finish();
        } catch (ParseException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Ungültiges Datum!", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        } catch(NumberFormatException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Falsches Zahlenformat!", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }

    }

    public void btnCancel_Click(View view) {
        this.finish();
    }

    public void setDate(View view) {
        DialogFragment dFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);

        dFragment.setArguments(args);

        dFragment.show(getFragmentManager(), "Date Picker");
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        editTerminDatum.setText(new StringBuilder().append(day<10?"0"+day:day).append(".")
                .append(month<10?"0"+month:month).append(".").append(year));
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            Bundle args = getArguments();

            int year = args.getInt("year");
            int month = args.getInt("month");
            int day = args.getInt("day");

            DatePickerDialog dpd = new DatePickerDialog(getActivity(),this,year,month,day);

            dpd.setTitle("Datum des Termins");

            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            OptionActivity act = (OptionActivity) getActivity();
            act.showDate(year, month+1, day);
        }

    }
}
