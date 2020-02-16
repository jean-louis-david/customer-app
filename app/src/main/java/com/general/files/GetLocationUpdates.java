package com.general.files;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.utils.Utils;

public class GetLocationUpdates implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context mContext;

    public int UPDATE_INTERVAL = 4000; // 10 sec
    public int FATEST_INTERVAL = 2000; // 5 sec
    private int DISPLACEMENT = 8; // 10 meters
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LocationUpdates locationsUpdates;
    boolean isPermissionDialogShown = false;
    private LastLocationListner LastLocationListner;
    Location mLastLocation;
    boolean isApiConnected = false;

    public GetLocationUpdates(Context context, int displacement) {
        this.mContext = context;
        this.DISPLACEMENT = displacement;
        buildGoogleApiClient();
        createLocationRequest();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    public void startLocationUpdates() {

        boolean isLocationPermissionGranted = new GeneralFunctions(mContext).checkLocationPermission(isPermissionDialogShown);
        if (isLocationPermissionGranted == true) {
//            Utils.printLog("Loc start update","called");
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (Exception e) {
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
            }
        } else {
            isPermissionDialogShown = true;
        }
    }

    public void stopLocationUpdates() {
//        Utils.printLog("Loc stop update","called");
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Utils.printLog("Location changed", "changed");
        if (locationsUpdates != null) {
            Utils.printLog("locationsUpdates", "not null");
            locationsUpdates.onLocationUpdate(location);
        }

        this.mLastLocation = location;
    }

    public Location getLocation() {
        return this.mLastLocation;
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void StopUpdates() {
        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle arg0) {

        isApiConnected = true;
        boolean isLocationPermissionGranted = new GeneralFunctions(mContext).checkLocationPermission(isPermissionDialogShown);

        if (isLocationPermissionGranted == true) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (locationsUpdates != null) {
                locationsUpdates.onLocationUpdate(mLastLocation);
            } else if (LastLocationListner != null) {
                if (mLastLocation != null) {
                    LastLocationListner.handleLastLocationListnerCallback(mLastLocation);
                } else if (mLastLocation == null) {
                    LastLocationListner.handleLastLocationListnerNOVALUECallback(0);
                }
            }
        } else {
            isPermissionDialogShown = true;
        }
        startLocationUpdates();
    }

    public boolean isApiConnected() {
        return this.isApiConnected;
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public interface LocationUpdates {
        void onLocationUpdate(Location location);
    }

    public void setLocationUpdatesListener(LocationUpdates locationsUpdates) {
        this.locationsUpdates = locationsUpdates;
    }

    public interface LastLocationListner {
        void handleLastLocationListnerCallback(Location mLastLocation);

        void handleLastLocationListnerNOVALUECallback(int id);
    }

    public void setLastLocationListener(LastLocationListner LastLocationListner) {
        this.LastLocationListner = LastLocationListner;
    }
}
