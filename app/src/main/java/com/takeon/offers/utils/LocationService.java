package com.takeon.offers.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.text.DateFormat;
import java.util.Date;

public class LocationService implements
    LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

  public static final String TOKEN = "token";
  private static final String TAG = "LocationActivity";
  private static final long INTERVAL = 60 * 60 * 1000;
  private static final long FASTEST_INTERVAL = 1000 * 60;
  Context context;
  private GoogleApiClient mGoogleApiClient;
  private Location mCurrentLocation;
  private String mLastUpdateTime;
  private LocationRequest mLocationRequest;
  private LocationGet locationGet;
  private boolean isOneTime;
  private String token;
  private boolean hasClientConfig = false;
  private long interval = 9 * 1000;
  private long fastestInterval = 5 * 1000;

  public LocationService(String token, LocationGet locationGet) {
    this.token = token;
    this.locationGet = locationGet;
  }

  private void createLocationRequest() {
    mLocationRequest = new LocationRequest();

    if (hasClientConfig) {
      mLocationRequest.setInterval(interval);
    } else {
      mLocationRequest.setInterval(INTERVAL);
    }

    if (hasClientConfig) {
      mLocationRequest.setFastestInterval(fastestInterval);
    } else {
      mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    float smallestDisplacementMeters = 10;
    mLocationRequest.setSmallestDisplacement(smallestDisplacementMeters);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  public void setInterval(long interval) {
    hasClientConfig = true;
    this.interval = interval;
  }

  public void setFastestInterval(long fastestInterval) {
    hasClientConfig = true;
    this.fastestInterval = fastestInterval;
  }

  public void init(Context context) {
    this.context = context;
    System.out.println(TAG + "Init location service ...............................");

    //show error dialog if GoolglePlayServices not available
    if (!isGooglePlayServicesAvailable()) {
      System.out.println(TAG + "isGooglePlayServicesAvailable..........false");
      return;
    }

    createLocationRequest();
    mGoogleApiClient = new GoogleApiClient.Builder(context)
        .addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();

    mGoogleApiClient.connect();
  }

  public void stop() {
    System.out.println(TAG + "onStop fired ..............");
    mGoogleApiClient.disconnect();
    System.out.println(TAG + "isConnected ...............: " + mGoogleApiClient.isConnected());
  }

  private boolean isGooglePlayServicesAvailable() {
    int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
    if (ConnectionResult.SUCCESS == status) {
      return true;
    } else {
      System.out.println(TAG + "isGooglePlayServicesAvailable ...............: " + status);
      //            GooglePlayServicesUtil.getErrorDialog(status, context, 0).show();
      return false;
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    System.out.println(
        TAG + "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
    startLocationUpdates();
  }

  private void startLocationUpdates() {

    try {

      Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      if (location == null) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
            this);
      } else {
        onLocationChanged(location);
      }
    } catch (SecurityException se) {
      Log.e(TAG, "Go into settings and find Gps Tracker app and enable Location.");
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    System.out.println(TAG + "Connection failed: " + connectionResult.toString());
  }

  @Override
  public void onLocationChanged(Location location) {
    System.out.println(
        TAG + "Firing onLocationChanged..............................................");

    mCurrentLocation = location;
    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

    if (locationGet != null) {
      locationGet.onLocationGet(mCurrentLocation);
    }

    if (isOneTime) {
      stopLocationUpdates();
      stop();
    }
  }

  public Location getLocation() {
    return mCurrentLocation;
  }

  private void updateUI() {
    System.out.println(TAG + "UI update initiated .............");
    if (null != mCurrentLocation) {
      String lat = String.valueOf(mCurrentLocation.getLatitude());
      String lng = String.valueOf(mCurrentLocation.getLongitude());
      System.out.println(TAG + "" + "At Time: " + mLastUpdateTime + "\n" +
          "Latitude: " + lat + "\n" +
          "Longitude: " + lng + "\n" +
          "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
          "Provider: " + mCurrentLocation.getProvider());
    } else {
      System.out.println(TAG + "location is null ...............");
    }
  }

  public void stopLocationUpdates() {
    LocationServices.FusedLocationApi.removeLocationUpdates(
        mGoogleApiClient, this);
    System.out.println(TAG + "Location update stopped .......................");
  }

  public boolean isOneTime() {
    return isOneTime;
  }

  public void setOneTime(boolean isOneTime) {
    this.isOneTime = isOneTime;
  }

  public interface LocationGet {
    void onLocationGet(Location location);
  }
}
