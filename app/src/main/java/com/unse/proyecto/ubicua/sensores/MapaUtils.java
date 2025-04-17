package com.unse.proyecto.ubicua.sensores;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.unse.proyecto.ubicua.R;

import java.util.Calendar;
import java.util.Objects;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

public class MapaUtils {

    public static LatLng init(Activity activity, GoogleMap googleMap) {

        darkMode(activity, googleMap);

        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return null;
        } else {
            Criteria criteria = new Criteria();
            LocationManager locationManager = (LocationManager) Objects.requireNonNull(activity.getApplicationContext())
                    .getSystemService(LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                googleMap.setMyLocationEnabled(true);
            }
            String provider = locationManager != null ? locationManager.getBestProvider(criteria, true) : null;
            LatLng sde = null;
            if (provider != null) {
                if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    sde = new LatLng(location.getLatitude(), location.getLongitude());
                } else {
                    sde = new LatLng(-27.791156, -64.250532);
                }
            } else {
                sde = new LatLng(-27.791156, -64.250532);
            }

            return sde;
        }
    }

    public static void darkMode(Activity activity, GoogleMap googleMap) {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        /*if (timeOfDay >= 20) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity.getApplicationContext(), R.raw.map_night_mode));
        } else if (timeOfDay < 8) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity.getApplicationContext(), R.raw.map_night_mode));
        }*/
    }

    public static Bitmap getDrawable(Context context, int id, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        final Bitmap b = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    public static boolean isShow(LatLng object, Circle circle) {
        float[] distance = new float[2];
        Location.distanceBetween(object.latitude, object.longitude,
                circle.getCenter().latitude, circle.getCenter().longitude, distance);

        if (distance[0] < 100
                //circle.getRadius()
        ) {
            return true;
        }
        return false;
    }

}
