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
import com.unse.proyecto.ubicua.BuildConfig;
import com.unse.proyecto.ubicua.interfaz.activity.MainActivity;
import com.unse.proyecto.ubicua.principal.util.Utils;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGPSStatus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;

public class GPSModule implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        ResultCallback<Status> {

    private static final int REQUEST_GROUP_PERMISSIONS_LOCATION = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;
    private static final int REQUEST_LOCATION = 3000;
    MainActivity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;

    public GPSModule(MainActivity activity) {
        mActivity = activity;
        init();
    }

    private void init() {
        Utils.showLog("Ubicación desactivada, se reintentará nuevamente.");
        Utils.showLog("Ubicación desactivada, se reintentará nuevamente.");
        Utils.showLog("Ubicación desactivada, se reintentará nuevamente.");
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
                .setInterval(3000)
                //.setFastestInterval(UPDATE_FASTEST_INTERVAL)
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

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                Status status = result.getStatus();

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
            case REQUEST_GROUP_PERMISSIONS_LOCATION:
                switch (grantResults[0]) {
                    case PackageManager.PERMISSION_GRANTED:
                        processLastLocation();
                        startLocationUpdates();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        stopLocationUpdates();
                        Utils.showToast(mActivity.getApplicationContext(), "Por favor, concede el permiso");
                        break;
                }
                break;
        }
    }

    private void updateLocationUI() {
        Utils.showLog(String.format("Latitud: %s - Longitud: %s", mLastLocation.getLatitude(), mLastLocation.getLongitude()));
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
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            intent.setData(uri);
            mActivity.startActivityForResult(intent, REQUEST_GROUP_PERMISSIONS_LOCATION);
            Utils.showToast(mActivity.getApplicationContext(), "Por favor, autoriza el permiso");

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
        updateLocationUI();
    }


    @Override
    public void onResult(@NonNull Status status) {
    }

}
