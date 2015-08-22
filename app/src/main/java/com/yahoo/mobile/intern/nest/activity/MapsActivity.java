package com.yahoo.mobile.intern.nest.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
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
import com.yahoo.mobile.intern.nest.utils.ParseUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity
                        implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraChangeListener {

    private double mLat, mLong;
    private boolean mGivenPinLocation;
    private boolean mShowRange;
    static final int MIN_RADIUS = 500;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    public GoogleApiClient mGoogleApiClient;
    private Geocoder mGeocoder;
    private Circle mRadiusCircle;
    private int mRadius;


    @Bind(R.id.seekBar_radius) SeekBar mRadiusSeekBar;
    @Bind(R.id.map_search_view) SearchView mSearchView;

    private Location mCurLocation;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mShowRange = getIntent().getBooleanExtra(Common.EXTRA_SEEKBAR,false);
        mGivenPinLocation = getIntent().getBooleanExtra(Common.EXTRA_HAS_PIN,false);
        if(mGivenPinLocation){
            mLat = getIntent().getDoubleExtra(Common.EXTRA_LAT, 0);
            mLong = getIntent().getDoubleExtra(Common.EXTRA_LONG, 0);
        }


        ButterKnife.bind(this);
        if(!mShowRange)
            mRadiusSeekBar.setVisibility(View.GONE);

        setUpMapIfNeeded();

        mGeocoder = new Geocoder(this, Locale.getDefault());
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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadius = MIN_RADIUS * (1 + progress);
                drawCircleOnMap();
            }
        });


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchClicked(query).execute();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }



    @OnClick(R.id.btn_ok)
    void btnOk() {
        //LatLng position = new LatLng(mCurLocation.getLatitude(),mCurLocation.getLongitude());//mMap.getCameraPosition().target;
        if(mShowRange){

            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {

                    int h =bitmap.getHeight();
                    int w =bitmap.getWidth();
                    Bitmap cropBmp = Bitmap.createBitmap(bitmap, 0, (h-w)/2, w, w);
                    ParseUtils.updateUserMap(cropBmp);

                    String path = saveToInternalStorage(cropBmp);

                    LatLng position = mMap.getCameraPosition().target;
                    Intent it = new Intent();
                    it.putExtra(Common.EXTRA_MAP_PATH, path);
                    it.putExtra(Common.EXTRA_LOCATION, position);
                    it.putExtra(Common.EXTRA_RADIUS, mRadius/MIN_RADIUS);
                    it.putExtra(Common.EXTRA_ADDRESS, mSearchView.getQuery().toString());
                    setResult(RESULT_OK, it);
                    finish();
                }
            });
        }
        else {
            LatLng position = mMap.getCameraPosition().target;
            Intent it = new Intent();
            it.putExtra(Common.EXTRA_LOCATION, position);
            it.putExtra(Common.EXTRA_ADDRESS, mSearchView.getQuery().toString());
            setResult(RESULT_OK, it);
            finish();
        }
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

        if(mGivenPinLocation) {

            LatLng latLng = new LatLng(mLat, mLong);
            Log.d("map",""+latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mCurLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng latLng = new LatLng(mCurLocation.getLatitude(), mCurLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("camera changed", "" + cameraPosition);
        drawCircleOnMap();

        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(
                    cameraPosition.target.latitude ,
                    cameraPosition.target.longitude,
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            mSearchView.setQuery(addresses.get(0).getAddressLine(0), false);
        }

    }

    public void drawCircleOnMap(){
        if(!mShowRange)
            return;

        if(mRadiusCircle != null)
            mRadiusCircle.remove();

        mRadiusCircle = mMap.addCircle(new CircleOptions()
                .center(mMap.getCameraPosition().target)
                .radius(mRadius)
                .strokeWidth(0)
                .fillColor(Color.argb(63, 0, 0, 255)));


        getSupportFragmentManager().findFragmentById(R.id.map).getView().invalidate();

    }



    private class SearchClicked extends AsyncTask<Void, Void, Boolean> {
        private String toSearch;
        private Address address;

        public SearchClicked(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault() );
                List<Address> results = geocoder.getFromLocationName(toSearch, 1);

                if (results.size() == 0) {
                    return false;
                }

                address = results.get(0);
                Location location = new Location("dummyprovider");
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());
                mCurLocation = location;

            } catch (Exception e) {
                Log.e("", "Something went wrong: ", e);
                return false;
            }
            return true;
        }


        protected void onPostExecute(Boolean found) {
            if (found) {
                LatLng latLng = new LatLng(mCurLocation.getLatitude(), mCurLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,Common.PATH_MAP);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

}
