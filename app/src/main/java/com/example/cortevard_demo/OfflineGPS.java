package com.example.cortevard_demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OfflineGPS extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;

    String locationText = "";
    String locationLatitude = "";
    String locationLongitude = "";

    private int mInterval = 3000; // 3 seconds by default, can be changed later
    private Handler mHandler;
    AlertDialog ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Alert Dialog
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(OfflineGPS.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Atención");

        // Setting Dialog Message
        String string1 = "Espere entre 10 y 40 segundos para que se actualicen sus coordenadas.";

        alertDialog2.setMessage(string1);

        // Setting Icon to Dialog
        alertDialog2.setIcon(R.drawable.ic_launcher_background);

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Continuar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // Showing Alert Dialog
        ad = alertDialog2.show();

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                mHandler = new Handler();
                startRepeatingTask();
            }
        }, 5000); //5 seconds
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
        if (ad != null) { ad.dismiss(); ad = null; }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                getLocation(); //this function can change value of mInterval.

                if (locationText.toString() == "") {
                    Toast.makeText(getApplicationContext(), "Buscando coordenadas.", Toast.LENGTH_LONG).show();
                }
                else {
                    /*yourlat.setText(locationLatitude.toString());
                    yourlong.setText(locationLongitude.toString());*/
                    Intent intent = new Intent();
                    intent.putExtra("Longitude", locationLongitude.toString());
                    intent.putExtra("Latitude", locationLatitude.toString());
                    setResult(1,intent);
                    finish();
                }
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationText = location.getLatitude() + "," + location.getLongitude();
        locationLatitude = location.getLatitude() + "";
        locationLongitude = location.getLongitude() + "";
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(OfflineGPS.this, "Por favor habilite el GPS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}