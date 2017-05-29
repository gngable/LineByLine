package com.mercangelsorfware.linebyline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    TextView accuracyLabel;
    TextView distanceLabel;
    TextView gpsStatusLabel;

    Location lastLocation;
    Location lastRouteLocation;

    ArrayList<Waypoint> currentTrack = new ArrayList<Waypoint>();

    ArrayList<ArrayList<Waypoint>> segments = new ArrayList<>();

    Double routeLength = 0.0;

    boolean recordingRoute = false;

    String lastStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        boolean gps_enabled = false;

        try
        {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception ex)
        {}

        if (!gps_enabled)
        {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Location services are not enabled, please enable and restart this app.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                    System.exit(0);
                }
            });

            dialog.show();
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, new LocationListener(){

            @Override
            public void onLocationChanged(Location location)
            {
                final Location loc = location;

                lastLocation = location;

                double acc = location.getAccuracy();
                String accuracy = "";
                if (acc <= 3.0)
                {
                    accuracy = "Excellent";
                }
                else if (acc <= 6)
                {
                    accuracy = "Good";
                }
                else if (acc < 10)
                {
                    accuracy = "Fair";
                }
                else
                {
                    accuracy = "Poor";
                }

                accuracy += " (" + String.format("%.2f", acc * 3.28084) + "ft)";

                final String accuracylbl = accuracy;
                final Date date = new Date(loc.getTime());

                if (recordingRoute)
                {
                    //if (lastRouteLocation == null || lastRouteLocation.distanceTo(loc) > 1.0)
                    {
                        currentTrack.add(new Waypoint("", loc));

                        if (lastRouteLocation != null)
                            routeLength += (lastRouteLocation.distanceTo(loc));

                        lastRouteLocation = loc;
                    }
                }

                gpsStatusLabel.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        accuracyLabel.setText("Accuracy: " + accuracylbl);

                        if (recordingRoute)
                        {
                            double distance = routeLength * 0.000621371;
                            distanceLabel.setText(String.format("%.2f", distance));
                        }
                    }
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle bundle)
            {
                if (status == lastStatus) return;

                lastStatus = status;

                String statustext = "";

                if (status == LocationProvider.OUT_OF_SERVICE)
                {
                    statustext = "Out of Service";
                }
                else if (status == LocationProvider.AVAILABLE)
                {
                    statustext = "Available";
                }
                else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
                {
                    statustext = "Temporarily Unavailable";
                }

                gpsStatusLabel.setText("GPS Status: " + statustext);
            }

            @Override
            public void onProviderEnabled(String p1)
            {
                // TODO: Implement this method
            }

            @Override
            public void onProviderDisabled(String p1)
            {
                // TODO: Implement this method
            }
        });
            }

            @Override
            public void onAccuracyChanged(Sensor p1, int p2)
            {
                // TODO: Implement this method
            }


        };
    }
}
