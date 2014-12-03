package com.carsharing.antisergiu.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class SearchPoolActivity extends Activity {

    public static String convertToHour(int hourOfDay, int minute) {

        String hourString;
        if (hourOfDay < 10)
            hourString = "0" + hourOfDay;
        else
            hourString = "" +hourOfDay;

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

    public static String convertToGMT(String date, int timeZone) {
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

    public void showLoginDialog(View view) {
        // TODO add login logic here

        // TODO after login show the MyPools View

//        LoginDialog loginDialog = new LoginDialog();
//        loginDialog.show(this.getFragmentManager(), "fragment_login");
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pool);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_my_profile) {
            Intent intent = new Intent(this, MyProfile.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        MapController mapController;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search_pool, container, false);
            rootView.findViewById(R.id.search_pool_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchPool(v);
                }
            });
            GoogleMap map;
            map = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            LatLng loc=new LatLng(44.435791, 26.047450);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

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

        public void searchPool(View view) {
            LatLng source = mapController.getOrigin();
            LatLng destination = mapController.getDestination();

            if (source == null || destination == null) {
                AlertDialog alertDialog = new AlertDialog(this.getActivity());
                if (source == null) {
                    alertDialog.createDialog("Search Pool Error", "Please select source and destination for your route!");
                } else {
                    alertDialog.createDialog("Search Pool Error", "Please select the destination of your route!");
                }
            } else {
                Intent matchingPoolIntent = new Intent(getActivity(), MatchingPoolsActivity.class);
                matchingPoolIntent.putExtra("SOURCE_LAT", source.latitude);
                matchingPoolIntent.putExtra("SOURCE_LONG", source.longitude);
                matchingPoolIntent.putExtra("DEST_LAT", destination.latitude);
                matchingPoolIntent.putExtra("DEST_LONG", destination.longitude);
                matchingPoolIntent.putExtra("DATE", ((EditText)getActivity().findViewById(R.id.create_tv_date)).getText().toString());
                matchingPoolIntent.putExtra("HOUR", convertToGMT(((EditText)getActivity().findViewById(R.id.create_tv_time)).getText().toString(), 2));
                startActivity(matchingPoolIntent);
            }

        }
    }


}
