package com.symbidrive.timteam.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.symbidrive.timteam.controller.CreatePoolDialogListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import static android.content.DialogInterface.OnDismissListener;

public class CreatePoolActivity extends Activity implements OnDismissListener{

    public static String sharedPrefsName = "AppPrefs";
    public static SharedPreferences prefs;
    protected static boolean mIsRegistered = false;
    static MapController mapController;

    public CreatePoolActivity() {


    }

    public static Context getContext() {
        return CreatePoolActivity.getContext();
    }

    public static boolean getRegistrationStatus() {
        return mIsRegistered;
    }

    public static void setmIsRegistered(boolean value) {
        mIsRegistered = value;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("registered", true);
        editor.commit();
    }

    public void savePool() {
        if (MainActivity.loginSuccessful) {

            LatLng origin = mapController.getOrigin();
            LatLng destination = mapController.getDestination();

            if (origin == null || destination == null) {
                CustomAlertDialog alertDialog = new CustomAlertDialog(this);
                if (origin == null) {
                    alertDialog.createDialog("Create Pool Error", "Please select source and destination for your route!", null);
                } else {
                    alertDialog.createDialog("Create Pool Error", "Please select the destination of your route!", null);
                }
            } else {
                String date = ((EditText) findViewById(R.id.create_tv_date)).getText().toString();
                String hour = ((EditText) findViewById(R.id.create_tv_time)).getText().toString();
                String seats = ((Spinner) findViewById(R.id.seats)).getSelectedItem().toString();
                String driverUsername = prefs.getString("username", "");
                long time = 0;

                boolean weekly = false;

                date += (" " + hour);
                Log.v("DATE", date);
                java.util.Date d = null;

                try {
                    SimpleDateFormat isoFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                    isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));
                    d = isoFormat.parse(date);
                    time = d.getTime();
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                Log.v("TIME", String.valueOf(time));
                addPool(driverUsername, new ParseGeoPoint(origin.latitude, origin.longitude),
                        new ParseGeoPoint(destination.latitude, destination.longitude),
                        new Date(time), Integer.parseInt(seats), weekly);

                Log.v("symbidrive", "Date: " + date + " Hour: " + hour);
                Log.v("symbidrive", "Seats: " + seats + " Weekly: " + weekly);
                Log.v("symbidrive", "Username: " + driverUsername);

                // after the pool is saved, redirect to mypools view
                CustomAlertDialog successAlertDialog = new CustomAlertDialog(this);
                successAlertDialog.createDialog("Create Pool", "Pool Created Successfully!",
                        new CreatePoolDialogListener(this, MyPools.class));
            }

        } else {
            Log.v("symbidrive", "Nu e logat");
        }

    }

    // add pool to server
    public void addPool(String driverUsn, ParseGeoPoint source, ParseGeoPoint dest, Date date, Integer seats, Boolean weekly) {
        HashMap<String, Object> params = new HashMap <String, Object> ();
        params.put("driver", driverUsn);
        params.put("source", source);
        params.put("destination", dest);
        params.put("date", date);
        params.put("seats", seats);
        params.put("weekly", weekly);

        ParseCloud.callFunctionInBackground("addPool", params, new FunctionCallback<String>() {
            public void done(String res, ParseException e) {
                if (e == null) {
                    // 'res' are valoarea: Pool created successfully!
                } else {
                    // 'res' are valoarea: Pool already exists!
                }
            }
        });
    }

    public String convertToGMT(String date, int timeZone) {
        String[] time = date.split(":");
        int hourOfDay = Integer.parseInt(time[0]);
        String minuteSting = time[1];
        String hourString = "";

        hourOfDay = hourOfDay + timeZone;
        if (hourOfDay < 0) {
            hourOfDay = Math.abs(hourOfDay);
            hourString = "0" + hourOfDay;
        } else if (hourOfDay < 10) {
            hourString = "0" + hourOfDay;
        } else {
            hourString = "" + hourOfDay;
        }

        return hourString + ":" + minuteSting;
    }

    public static String convertToHour(int hourOfDay, int minute) {

        String hourString;
         if (hourOfDay < 10) {
            hourString = "0" + hourOfDay;
        } else {
             hourString = "" + hourOfDay;
        }

        String minuteSting;
        if (minute < 10)
            minuteSting = "0" + minute;
        else
            minuteSting = "" +minute;

        return hourString + ":" + minuteSting;
    }

    public static String convertToDate(int year, int month, int day) {
        String monthString;
        month = month + 1;
        if(month < 10) {
            monthString = "0" + month;
        } else {
            monthString = "" + month;
        }

        String dayString;
        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = "" + day;
        }

        return monthString + "/" + dayString + "/" + year;

    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        savePool();
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            ((TextView) getActivity().findViewById(R.id.create_tv_time)).setText(convertToHour(hourOfDay, minute));

        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
           ((TextView) getActivity().findViewById(R.id.create_tv_date)).setText(convertToDate(year, month, day));
        }
    }

    public void showLoginDialog(View view) {
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.show(this.getFragmentManager(), "fragment_login");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pool);
        prefs = getSharedPreferences(sharedPrefsName, MODE_MULTI_PROCESS);
        mIsRegistered = prefs.getBoolean("registered", false);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    public void showTimePickerDialog(View v) {
        Log.d("TIME", "shows the time");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.
                getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View view) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(this.getFragmentManager(), "datePicker");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {



        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_pool, container, false);
            // Get a handle to the Map Fragment
            GoogleMap map;
            map = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            LatLng loc=new LatLng(44.435791, 26.047450);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,15));

            mapController = new MapController(map);

            // set set current time
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);

            String time = convertToHour(hour, minute);
            ((TextView) rootView.findViewById(R.id.create_tv_time)).setText(time);


            // set current date
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            ((TextView) rootView.findViewById(R.id.create_tv_date)).setText(convertToDate(year,month,day));


            return rootView;
        }


    }



}
