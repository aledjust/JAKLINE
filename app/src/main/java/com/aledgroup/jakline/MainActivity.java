package com.aledgroup.jakline;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aledgroup.jakline.utility.Constants;
import com.aledgroup.jakline.utility.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback, OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        TouchableWrapper.UpdateMapAfterUserInterection, TouchableWrapper.UpdateMapBeforeUserInterection,
        TouchableWrapper.UpdateMapMoveUserInterection, LocationListener,GoogleMap.OnCameraChangeListener {

    private LatLng CUR_POSITION;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;

    private NavigationView navigationView;

    private String filterAddress = "";
    private String destAddress = "";
    private String realdestAddress = "";
    private String realFilterAddress = "";

    private TextView txtCurLoc, txtSetDest;

    private boolean mPermissionDenied = false;

    // Session Manager Class
    private SessionManager session ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        RelativeLayout layoutDestination = (RelativeLayout)findViewById(R.id.LayoutDestination);
        layoutDestination.setVisibility(View.INVISIBLE);

        txtCurLoc = (TextView) findViewById(R.id.txtCurrentLocation);
        txtSetDest = (TextView) findViewById(R.id.txtDestLocation);

        txtCurLoc.addTextChangedListener(new MyTextWatcher(txtCurLoc));
        txtSetDest.addTextChangedListener(new MyTextWatcher(txtSetDest));

        mApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Custom Font
        TextView tx = (TextView)findViewById(R.id.main_toolbar_title);
        Typeface custom_font = Typeface.createFromAsset(this.getAssets(), "fonts/strasua.ttf");
        tx.setTypeface(custom_font);

        session = new SessionManager();
        double getpickupLat = Double.parseDouble(session.getKeyPickupLat(getApplicationContext()));
        double getpickupLong = Double.parseDouble(session.getKeyPickupLong(getApplicationContext()));
        double getdropoffLat = Double.parseDouble(session.getKeyDropoffLat(getApplicationContext()));
        double getdropoffLong = Double.parseDouble(session.getKeyDropoffLong(getApplicationContext()));

        if(getpickupLat != 0 && getpickupLong != 0)
        {
            CUR_POSITION = new LatLng(getpickupLat,getpickupLong);
        }

        if(getdropoffLat != 0 && getdropoffLong != 0)
        {
            layoutDestination.setVisibility(View.VISIBLE);
            LatLng newDest = new LatLng(getdropoffLat,getdropoffLong);
            SetDestPosition(newDest);
        }

        if(txtSetDest.getText().toString() == "" )
        {
            RelativeLayout imgBook = (RelativeLayout)findViewById(R.id.layoutBook);
            imgBook.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        session = new SessionManager();
        session.ClearSession(getApplicationContext());
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mApiClient.isConnected()) {
            mApiClient.disconnect();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_promo) {

        }
        else if (id == R.id.nav_notification) {

        }
        else if (id == R.id.nav_moment) {

        }
        else if (id == R.id.nav_support) {

        }
        else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "JAKLINE!");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Mari bergabung bersama JAKLINE. Jakarta Online, bepergian jadi lebih mudah.");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, R.drawable.jakline);

            startActivity(Intent.createChooser(sharingIntent, "Bagikan ke..."));
        }
        else if (id == R.id.nav_mail) {
            Intent intent = new Intent(MainActivity.this, Email.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mCurrentLocation;

        if(CUR_POSITION == null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            }
            else
                mCurrentLocation = null;
        }
        else
        {
            Location loc = new Location("getLoc");
            loc.setLatitude(CUR_POSITION.latitude);
            loc.setLongitude(CUR_POSITION.longitude);
            mCurrentLocation = loc;
        }

        // Note that this can be NULL if last location isn't already known.
        if (mCurrentLocation != null) {
            handleNewLocation(mCurrentLocation);
            InitializeMaps(bundle);
        }

        // Begin polling for new location updates.
        startLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Connection Failed", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO connection suspended implement
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onCameraChange(CameraPosition position) {

        TextView lblPinLoc = (TextView)findViewById(R.id.lblPinLoc);
        lblPinLoc.setVisibility(View.VISIBLE);

        txtCurLoc.setText("");

        if(txtSetDest.getText().toString() == "" ) {
            RelativeLayout layoutDestination = (RelativeLayout) findViewById(R.id.LayoutDestination);
            layoutDestination.setVisibility(View.INVISIBLE);
            txtSetDest.setText("");
        }

        if(position != null) {
            CUR_POSITION = position.target;
            SetLabelPosition(CUR_POSITION);
            session = new SessionManager();
            session.SET_PICKUP_LAT(getApplicationContext(),String.valueOf(CUR_POSITION.latitude));
            session.SET_PICKUP_LONG(getApplicationContext(),String.valueOf(CUR_POSITION.longitude));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        /* Return false so that we don't consume the event and the default behavior still occurs
         (the camera animates to the user's current position).*/
        TextView cLoc = (TextView) findViewById(R.id.lblPinLoc);
        cLoc.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    public void onUpdateMapMoveUserInterection() {
        hidePin();
    }

    public void onUpdateMapAfterUserInterection() {
        showViews();
    }

    public void onUpdateMapBeforeUserInterection() {
        // TODO Update the map now
        hideViews();
    }


    private void InitializeMaps(final Bundle savedInstanceState)
    {
        final LatLng markerPosition;
        if (savedInstanceState == null) {
            markerPosition = CUR_POSITION;
        } else {
            markerPosition = savedInstanceState.getParcelable(Constants.MARKER_POSITION_KEY);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        resetMyPositionButton(mapFragment);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (mMap != null) {
                    mMap.clear();
                }
                mMap = googleMap;
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.setOnCameraChangeListener(MainActivity.this);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.DEFAULT_ZOOM));

                mMap.setOnMyLocationButtonClickListener(MainActivity.this);
                enableMyLocation();
            }
        });
    }

    // Trigger new location updates at interval
    private void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.UPDATE_INTERVAL)
                .setFastestInterval(Constants.FASTEST_INTERVAL);

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient,
                    mLocationRequest, this);
        }
    }

    private void handleNewLocation(Location location) {
        CUR_POSITION = new LatLng(location.getLatitude(),
                location.getLongitude());
    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, Constants.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void hideViews() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.AppTolbar);
        RelativeLayout layoutSearch = (RelativeLayout) findViewById(R.id.LayoutSearch);
        RelativeLayout layoutDestination = (RelativeLayout)findViewById(R.id.LayoutDestination);

        layoutSearch.animate().translationY(-appBarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        layoutDestination.animate().translationY(-appBarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        appBarLayout.animate().translationY(-appBarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        hidePin();;
    }
    private void hidePin(){
        TextView cLoc = (TextView) findViewById(R.id.lblPinLoc);
        cLoc.setVisibility(View.INVISIBLE);
    }
    private void showViews() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.AppTolbar);
        RelativeLayout layoutSearch = (RelativeLayout) findViewById(R.id.LayoutSearch);
        RelativeLayout layoutDestination = (RelativeLayout)findViewById(R.id.LayoutDestination);
        layoutDestination.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        layoutSearch.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

        TextView cLoc = (TextView) findViewById(R.id.lblPinLoc);
        cLoc.setVisibility(View.VISIBLE);
    }

    public void SearchPickup(View v) {
        // does something very interesting
        Intent intent = new Intent(MainActivity.this, SearchPickup.class);
        intent.putExtra("pickupLoc", realFilterAddress);
        intent.putExtra("pickupLat", CUR_POSITION.latitude);
        intent.putExtra("pickupLong", CUR_POSITION.longitude);
        startActivity(intent);
    }
    public void SearchDest(View v) {
        // does something very interesting
        Intent intent = new Intent(MainActivity.this, SearchDest.class);
        intent.putExtra("pickupLat", CUR_POSITION.latitude);
        intent.putExtra("pickupLong", CUR_POSITION.longitude);
        startActivity(intent);
    }
    public void SetLoc(View v) {
        TextView lblPinLoc = (TextView)findViewById(R.id.lblPinLoc);
        lblPinLoc.setVisibility(View.INVISIBLE);
        RelativeLayout layoutDestination = (RelativeLayout)findViewById(R.id.LayoutDestination);
        layoutDestination.setVisibility(View.VISIBLE);
    }
    public void BookNow(View v) {

    }

    private void SetLabelPosition(LatLng latLang)
    {
        LatLng position = latLang;

        filterAddress = "";
        Geocoder geoCoder = new Geocoder(
                getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    position.latitude,
                    position.longitude, 1);

            if (addresses.size() > 0) {
                filterAddress = addresses.get(0).getAddressLine(0);
            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }catch (Exception e2) {
            e2.printStackTrace();
        }
//        Log.d(getClass().getSimpleName(),
//                String.format("Dragging to %f:%f", position.latitude,
//                        position.longitude));
        TextView cLoc = (TextView) findViewById(R.id.txtCurrentLocation);

        realFilterAddress = filterAddress;
        if(filterAddress.length() >= 35)
        {
            filterAddress = filterAddress.substring(0,34) + " ...";
            cLoc.setText(filterAddress);
        }
        else {
            cLoc.setText(filterAddress);
        }
    }
    private void SetDestPosition(LatLng latLang)
    {
        LatLng position = latLang;

        destAddress = "";
        Geocoder geoCoder = new Geocoder(
                getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    position.latitude,
                    position.longitude, 1);

            if (addresses.size() > 0) {
                destAddress = addresses.get(0).getAddressLine(0);
            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }catch (Exception e2) {
            e2.printStackTrace();
        }

        TextView cLoc = (TextView) findViewById(R.id.txtDestLocation);

        realdestAddress = destAddress;
        if(destAddress.length() >= 35)
        {
            destAddress = destAddress.substring(0,34) + " ...";
            cLoc.setText(destAddress);
        }
        else {
            cLoc.setText(destAddress);
        }
    }

    private void resetMyPositionButton(SupportMapFragment mapFragment)
    {
        View locationButton = ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(2);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           /*if(txtSetDest.getText() != "")
           {
               ImageView imgBook = (ImageView)findViewById(R.id.BookNow);
               imgBook.setVisibility(View.VISIBLE);
           }
            else
           {
               ImageView imgBook1 = (ImageView)findViewById(R.id.BookNow);
               imgBook1.setVisibility(View.INVISIBLE);
           }*/
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

            }
        }
    }

}
