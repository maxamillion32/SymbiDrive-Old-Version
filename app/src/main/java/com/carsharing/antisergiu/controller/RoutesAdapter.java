package com.carsharing.antisergiu.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.Time;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carsharing.antisergiu.main.LoginDialog;
import com.carsharing.antisergiu.main.MatchingPoolsActivity;
import com.carsharing.antisergiu.main.MyPools;
import com.carsharing.antisergiu.main.Route;
import com.carsharing.antisergiu.model.MatchingPoolItem;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RoutesAdapter extends BaseAdapter {

    private ArrayList<MatchingPoolItem> routes;

    public RoutesAdapter() {
        routes = new ArrayList<MatchingPoolItem>();
    }

    public void addItem(MatchingPoolItem item) {
        routes.add(item);
        notifyDataSetChanged();
    }

    public int getCount() {
        return routes.size();
    }

    public MatchingPoolItem getItem(int position) {
        return routes.get(position);
    }

    public long getItemId(int position) {
        return routes.get(position).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout listLayout;
        MatchingPoolItem currRoute = routes.get(position);

        if (convertView == null) {
            listLayout = new LinearLayout(parent.getContext());
            listLayout.setOrientation(LinearLayout.HORIZONTAL);

//            ================ INFORMATION LAYOUT ==========================
            LinearLayout infoLayout = new LinearLayout(listLayout.getContext());
            LinearLayout.LayoutParams infoLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            infoLayoutParams.setMargins(20, 0, 0, 0);
            infoLayout.setLayoutParams(infoLayoutParams);
            infoLayout.setOrientation(LinearLayout.VERTICAL);

            TextView driverView = new TextView(parent.getContext());
            driverView.setText("Driver: " + currRoute.getDriver());
            driverView.setTextSize(18);
            driverView.setTextColor(Color.DKGRAY);
            infoLayout.addView(driverView);

            TextView ratingView = new TextView(parent.getContext());
            ratingView.setText("Rating: " + currRoute.getRating());
            ratingView.setTextSize(18);
            ratingView.setTextColor(Color.DKGRAY);
            infoLayout.addView(ratingView);

//            ================= JOIN Button Layout =================
            RelativeLayout buttonLayout = new RelativeLayout(parent.getContext());

            Button joinBtn = new Button(parent.getContext());
            joinBtn.setText("JOIN");
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonParams.setMargins(0,0,20,0);
            joinBtn.setPadding(30,0,30,0);

            joinBtn.setLayoutParams(buttonParams);
            joinBtn.setBackgroundResource(android.R.color.holo_green_dark);
            joinBtn.setTextColor(Color.WHITE);
            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity host = (Activity) view.getContext();
                    LoginDialogListener loginDialogListener = new LoginDialogListener(host, view);


                }
            });
            buttonLayout.addView(joinBtn);
            listLayout.addView(infoLayout);
            listLayout.addView(buttonLayout);
        }
        else {
            listLayout = (LinearLayout) convertView;
        }

        return listLayout;
    }


}