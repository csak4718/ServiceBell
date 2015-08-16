package com.yahoo.mobile.intern.nest.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.yahoo.mobile.intern.nest.R;
import com.yahoo.mobile.intern.nest.utils.Common;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity
                        implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraChangeListener {

    private boolean mUserChooseLocation;
    static final int MIN_RADIUS = 500;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Circle mRadiusCircle;
    private int mRadius;

    @Bind(R.id.seekBar_radius) SeekBar mRadiusSeekBar;

    private Location mCurLocation;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mUserChooseLocation = getIntent().getBooleanExtra(Common.EXTRA_SEEKBAR,false);

        ButterKnife.bind(this);
        if(!mUserChooseLocation)
            mRadiusSeekBar.setVisibility(View.GONE);

        setUpMapIfNeeded();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


        mRadius = MIN_RADIUS;
        drawCircleOnMap();

        mRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                mRadius = MIN_RADIUS*(1+progress);
                drawCircleOnMap();
            }
        });


    }

    @OnClick(R.id.btn_ok)
    void btnOk() {

        //LatLng position = new LatLng(mCurLocation.getLatitude(),mCurLocation.getLongitude());//mMap.getCameraPosition().target;

        LatLng position = mMap.getCameraPosition().target;
        Intent it = new Intent();
        it.putExtra(Common.EXTRA_LOCATION, position);
        setResult(RESULT_OK, it);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        //mMap.animateCamera(cameraUpdate);
        //locationManager.removeUpdates(this);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setOnCameraChangeListener(this);
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mCurLocation  = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng latLng = new LatLng(mCurLocation.getLatitude(), mCurLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("camera changed", "" + cameraPosition);
        drawCircleOnMap();
    }

    public void drawCircleOnMap(){
        if(!mUserChooseLocation)
            return;

        if(mRadiusCircle != null)
            mRadiusCircle.remove();
        mRadiusCircle = mMap.addCircle(new CircleOptions()
                .center(mMap.getCameraPosition().target)
                .radius(mRadius)
                .strokeColor(Color.BLUE));
    }

}
