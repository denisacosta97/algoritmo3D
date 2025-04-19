package com.unse.proyecto.ubicua.sensores;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.unse.proyecto.ubicua.ConfigApp;
import com.unse.proyecto.ubicua.interfaz.activity.MainActivity;
import com.unse.proyecto.ubicua.principal.util.Utils;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGPSStatus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;

import java.util.ArrayList;
import java.util.List;

public class GPSModule implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        ResultCallback<Status> {

    private static final int REQUEST_GROUP_PERMISSIONS_LOCATION = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;
    private static final int REQUEST_LOCATION = 3000;
    private MainActivity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;
    private Boolean isInit = Boolean.FALSE;
    private List<LatLng> history;

    private static GPSModule instance;

    public static synchronized GPSModule getInstance(MainActivity activity) {
        if (instance == null) {
            instance = new GPSModule(activity);
        } else {
            instance.setActivity(activity);
        }
        return instance;
    }

    private void setActivity(MainActivity activity) {
        this.mActivity = activity;
    }

    public GPSModule(MainActivity activity) {
        mActivity = activity;
        history = new ArrayList<>();
        init();
    }

    private void init() {
        buildGoogleApiClient();
        // Crear configuración de peticiones
        createLocationRequest();
        // Crear opciones de peticiones
        buildLocationSettingsRequest();
        // Verificar ajustes de ubicación actuales
        checkLocationSettings();

    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(mActivity, this)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(5000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient, mLocationSettingsRequest
                );

        result.setResultCallback(result1 -> {
            Status status = result1.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    if (isLocationPermissionGranted())
                        startLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(
                                mActivity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Sin operaciones
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    openGPSDialog();
                    break;

            }
        });
    }

    public void openGPSDialog() {
        DialogoGPSStatus dialogoGPSStatus = new DialogoGPSStatus();
        dialogoGPSStatus.show(mActivity.getSupportFragmentManager(), "dialogo");
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        isInit = Boolean.TRUE;
                        processLastLocation();
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        openGPSDialog();
                        break;
                }
                break;
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
            case REQUEST_GROUP_PERMISSIONS_LOCATION:
                if (grantResults.length != 0){
                    switch (grantResults[0]) {
                        case PackageManager.PERMISSION_GRANTED:
                            isInit = Boolean.TRUE;
                            processLastLocation();
                            startLocationUpdates();
                            break;
                        case PackageManager.PERMISSION_DENIED:
                            stopLocationUpdates();
                            Utils.showToast(mActivity.getApplicationContext(), "Por favor, concede el permiso de ubicación");
                            break;
                    }
                    break;
                }
        }
    }

    private void updateLocationUI() {
        Utils.showLog(String.format("Latitud: %s - Longitud: %s", mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        if (isInit){
            isInit = Boolean.FALSE;
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mActivity.initLocation(latLng);
        }
        mActivity.updateLocation(mLastLocation);

    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient, this);
    }


    private void getLastLocation() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }else{
            manageDeniedPermission();
        }
    }

    private void processLastLocation() {
        getLastLocation();
        if (mLastLocation != null) {
            updateLocationUI();
        }
    }

    private void startLocationUpdates() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mActivity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } else {
            manageDeniedPermission();
        }
    }

    private void manageDeniedPermission() {
        Utils.showLog("Ubicación desactivada, se reintentará nuevamente.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Abrir configuracion
            Intent intent = new Intent();
            intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", ConfigApp.APPLICATION_ID, null);
            intent.setData(uri);
            mActivity.startActivityForResult(intent, REQUEST_GROUP_PERMISSIONS_LOCATION);
            Utils.showToast(mActivity.getApplicationContext(), "Por favor, autoriza el permiso de ubicación");

        } else {
            ActivityCompat.requestPermissions(
                    mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mActivity.onMapReady(googleMap);

    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                mActivity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Obtenemos la última ubicación al ser la primera vez
        processLastLocation();
        // Iniciamos las actualizaciones de ubicación
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void onPause() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }

    }

    public void onRestart(){
        processLastLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        addHistory(location);
        updateLocationUI();
    }

    private static final float MIN_DISTANCE = 0.5f;
    private LatLng lastPoint = null;
    private Long lastSavedTime = 0l;

    private void addHistory(Location location) {
        if (location.getAccuracy() > 20)
            return;

        long now = System.currentTimeMillis();
        if (now - lastSavedTime < 10_000)
            return;

        LatLng currentPoint = new LatLng(location.getLatitude(), location.getLongitude());

        if (lastPoint != null) {
            float[] results = new float[1];
            Location.distanceBetween(
                    lastPoint.latitude, lastPoint.longitude,
                    currentPoint.latitude, currentPoint.longitude,
                    results);
            if (results[0] < MIN_DISTANCE)
                return; // muy cerca, no guardar
        }

        lastSavedTime = now;
        lastPoint = currentPoint;

        if (history == null)
            history = new ArrayList<>();
        history.add(currentPoint);
    }


    public List<LatLng> getHistory() {
        return history;
    }

    @Override
    public void onResult(@NonNull Status status) {
    }

}
