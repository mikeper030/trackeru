package org.ultimatetoolsil.trackeru;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import adapters.DrawerAdapter;
import models.LocationModel;
import outlander.showcaseview.ShowcaseViewBuilder;
import utilities.GoogleMapUtis;

import static org.ultimatetoolsil.trackeru.MapFragment.Animator.ANIMATE_SPEEED_TURN;
import static org.ultimatetoolsil.trackeru.MapFragment.Animator.BEARING_OFFSET;

/**
 * Created by mike on 9 Dec 2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private FirebaseDatabase mFirebaseDatabase;
    private static HashSet<LocationModel> allusersundp = new HashSet<>();
    private static ArrayList<LocationModel> allusers = new ArrayList<>();
    static HashSet<Marker> markers = new HashSet<>();
    private static HashMap<String, Polyline> polys = new HashMap<>();
    private String userID;
    private DatabaseReference myRef;
    private boolean animate = true;
    private FirebaseAuth auth;
    private boolean batterysavermode;
    private ProgressBar pb;
    static ListView drawerlist;
    DrawerAdapter drawerAdapter;
    static String username;
    static HashMap<String, LocationModel> locations = new HashMap();
    FusedLocationProviderClient mFusedLocationClient;
    private BroadcastReceiver batteryReceiver;
    public static GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    public static GoogleMap mMap;
    public static LocationRequest mLocationRequest;
    public ShowcaseViewBuilder showcaseViewBuilder;

    int i=1;
    private String url;
    private static CheckBox polyline,sattelite,terrain,hybrid,normal;
    private Polyline gpsTrack;
    private static HashMap<String, List<LatLng>> pols = new HashMap<>();
    private Button nav;
    private static HashMap<String, LocationModel> polylines = new HashMap<>();
    static List<LatLng> points = new ArrayList<>();
    private static int firsttime = 0;
    private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private List<Marker> markers2 = new ArrayList<Marker>();
    private Handler handler = new Handler();
    int addelay=100000;
    private Random random = new Random();
    private AdView mAdView1;
    private InterstitialAd mInterstitialAd;
    boolean operate=false;
    boolean sp;
    Button b;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //custom marker bitmap and canvas
        final boolean showads =PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
        if(showads) {
            MobileAds.initialize(getActivity());
            mAdView1 = getView().findViewById(R.id.map_banner);
            AdRequest adRequest = new AdRequest.Builder().build();

            mAdView1.loadAd(adRequest);
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/7069318342");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }
                        }
                    }, addelay);
                }

            });
            addelay+=100000;
        }
        nav=(Button)getView().findViewById(R.id.button1);
        boolean firsttime =PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("first", true);
        if(firsttime){
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("first", false).apply();
            showcaseViewBuilder = ShowcaseViewBuilder.init(getActivity());
            showcaseViewBuilder.setTargetView(nav)
                    .setBackgroundOverlayColor(0xdd70d2cd)
                    .setRingColor(0xccb9e797)

                    .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()))
                    //.setMarkerDrawable(getResources().getDrawable(R.drawable.arrow_up), Gravity.LEFT)
                    .addCustomView(R.layout.fab_description_view, Gravity.TOP)
                    .setCustomViewMargin((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));

            showcaseViewBuilder.show();

            showcaseViewBuilder.setClickListenerOnView(R.id.btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showcaseViewBuilder.hide();
                    showcasecircles();
                }
            });

       }
        polyline = (CheckBox) getView().findViewById(R.id.polyline);

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showads) {
                    startActivityForResult(new Intent(getActivity(), NavigationActivity.class), NavigationActivity.RESULT_CODE);
                    if (mInterstitialAd.isLoaded()) {
                        Log.d("showing","show");
                        mInterstitialAd.show();
                    } else {
                        Log.d("inter", "The interstitial wasn't loaded yet.");
                    }
                }else{
                    startActivityForResult(new Intent(getActivity(), NavigationActivity.class), NavigationActivity.RESULT_CODE);
                }

            }
        });

        //=============================================================== map mode initializers  =======================================================

        normal = (CheckBox)getView().findViewById(R.id.normal);
        sattelite = (CheckBox)getView().findViewById(R.id.satellite);
        terrain = (CheckBox)getView().findViewById(R.id.terrain);
        hybrid=(CheckBox)getView().findViewById(R.id.hybrid) ;
        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(normal.isChecked())
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("hybrid", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("sattelite", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("terrain", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("normal", true).apply();
                terrain.setChecked(false);
                sattelite.setChecked(false);
                hybrid.setChecked(false);
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
            }
        });
        hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hybrid.isChecked())
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("hybrid", true).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("sattelite", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("terrain", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("normal", false).apply();
                terrain.setChecked(false);
                sattelite.setChecked(false);
                normal.setChecked(false);
                mMap.setMapType(mMap.MAP_TYPE_HYBRID);
            }
        });
        terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(terrain.isChecked())
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("terrain", true).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("sattelite", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("hybrid", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("normal", false).apply();
                sattelite.setChecked(false);
                hybrid.setChecked(false);
                normal.setChecked(false);
                mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
            }
        });

        sattelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sattelite.isChecked())
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("sattelite", true).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("terrain", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("hybrid", false).apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("normal", false).apply();
                hybrid.setChecked(false);
                terrain.setChecked(false);
                normal.setChecked(false);
                mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
            }
        });
        boolean setchecked =PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("polyline", false);
        if(setchecked){
            polyline.setChecked(true);
        }else{
            polyline.setChecked(false);
        }
        polyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(polyline.isChecked()){
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("polyline", true).apply();
                }
                if (!polyline.isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("polyline", false).apply();
                    mMap.clear();
                }
            }
        });

        //============================================================================================================================

        b= (Button)getView().findViewById(R.id.dum);
        pb = (ProgressBar) getView().findViewById(R.id.drawerpb);
        pb.setVisibility(View.VISIBLE);
        drawerlist = (ListView) view.findViewById(R.id.drawerelist);
        Handler defaultt = new Handler();
        defaultt.postDelayed(new Runnable() {
            @Override
            public void run() {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        pb.setVisibility(View.INVISIBLE);
                    }
                }, CirclesFragment.objnum);


            }
        }, 1500);
        final LocationManager manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        try {


        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void showcasecircles() {

        showcaseViewBuilder.setTargetView(b)
                .setBackgroundOverlayColor(0xdd70d2cd)
                .setRingColor(0xccb9e797)

                .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()))
                //.setMarkerDrawable(getResources().getDrawable(R.drawable.arrow_up), Gravity.LEFT)
                .addCustomView(R.layout.circles_description_view, Gravity.TOP)
                .setCustomViewMargin((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -100, getResources().getDisplayMetrics()));

        showcaseViewBuilder.show();

        showcaseViewBuilder.setClickListenerOnView(R.id.btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseViewBuilder.hide();

            }
        });

    }


    @Override
    public void onStart() {

        getActivity().registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }

    @Override
    public void onStop() {

        getActivity().unregisterReceiver(batteryReceiver);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean showads =PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
        View view=null;
        if(showads) {
            view  = inflater.inflate(R.layout.map_fragment_ads, null, false);
        }else{
            view = inflater.inflate(R.layout.map_fragment,null,false);
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (operate) {
                    Log.d("key", "keyCode: " + keyCode);
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        Log.d("key", "onKey Back listener is working!!!");
                        if (animator.isAnimating()) {
                            animator.stopAnimation();
                        }
                        operate = false;
                        LatLng current = mMap.getCameraPosition().target;
                        CameraPosition cameraPosition =
                                new CameraPosition.Builder()
                                        .target(current)

                                        .tilt(0)
                                        .zoom(15)
                                        .build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }

                } else {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getContext());
                        }
                        builder.setTitle("")
                                .setMessage(R.string.background)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_HOME);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        // continue with delete
                                    }
                                })
                                .setNegativeButton(R.string.no_exit, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (mGoogleApiClient != null)
                                            mGoogleApiClient.disconnect();

                                        System.exit(0);


                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
                return false;
            }

        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), Signups.class));

        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        myRef = mFirebaseDatabase.getReference();
        username = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username", null);
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("bsaver", false);
        //monitor battery level and change battery consumption accordingly

        batteryReceiver = new BatteryBroadcastReceiver();

        myRef.child("users").child(userID).child("picLink").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String piclink = dataSnapshot.getValue(String.class);
                url = piclink;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
// add marker to Map


        return view;

    }
    private Menu menu;
    private boolean directionsFetched = false;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_manu, menu);
        updateNavigationStopStart();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Intent i = new Intent(getContext(), settings.class);
                startActivity(i);
                // Not implemented here
                return true;


            default:
                break;
        }
        if (item.getItemId() == R.id.action_bar_directions) {
            startActivityForResult(new Intent(getActivity(), NavigationActivity.class), NavigationActivity.RESULT_CODE);

        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);




        //================================ MAP MODE INITIALIZERS =====================================================
        Boolean ter,hyb,sat,nor;
        nor=PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("normal", false);
        ter=PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("terrain", false);
        hyb=PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("hybrid", false);
        sat=PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("sattelite", false);
        Log.d("booleans",(nor.toString()+ter.toString()+hyb.toString()+sat.toString()));

        if(sat){

            sattelite.setChecked(true);
            mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
            terrain.setChecked(false);
            hybrid.setChecked(false);
            normal.setChecked(false);
        }

        if(ter) {
            sattelite.setChecked(false);
            hybrid.setChecked(false);
            normal.setChecked(false);
            terrain.setChecked(true);
            mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
        }
        if(hyb){
            terrain.setChecked(false);
            sattelite.setChecked(false);
            normal.setChecked(false);
            hybrid.setChecked(true);
            mMap.setMapType(mMap.MAP_TYPE_HYBRID);
        }
        if(nor) {
            mMap.setMapType(mMap.MAP_TYPE_NORMAL);
            normal.setChecked(true);
            terrain.setChecked(false);
            sattelite.setChecked(false);
            hybrid.setChecked(false);
        }

        //==================================================================================


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.CYAN);
        polylineOptions.width(5);
        gpsTrack = mMap.addPolyline(polylineOptions);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTag()!=null)
                if(marker.getTag().equals(username)){
                    Intent i = new Intent(getActivity(),createprofile.class);
                    i.putExtra("from","settings");
                    startActivity(i);

                }
                return false;
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {




        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        if(sp){
            Log.d("battery","saver mode activated");
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        }else{
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Log.d("battery","saver mode disabled");
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {


            for (Location location : locationResult.getLocations()) {
                //Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());


                //Place current location marker
                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                //add user's polyline
                if (polyline.isChecked()) {
                    List<LatLng> points = gpsTrack.getPoints();
                    points.add(latLng);
                    gpsTrack.setPoints(points);

                }


                //upload location to firebase public
                LocationModel lm = new LocationModel();
                lm.setLat(location.getLatitude());
                lm.setLongt(location.getLongitude());
                if(url!=null)
                lm.setUrl(url);
                if(url==null)
                    lm.setUrl("android.resource://org.ultimatetoolsil.trackeru/drawable/prof");
                try {
                    myRef.child("public").child("UsersLocation").child(username).setValue(lm);

                }catch (Exception e){
                    e.printStackTrace();
                    if(i==1) {
                        Intent in = new Intent(getContext(), createprofile.class);
                        startActivity(in);
                        i++;
                    }
                }
                if(url==null){
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    Picasso.with(getContext())

                            .load(R.drawable.prof)

                            .resize(250, 250)
                            .transform(new markerImageResizer(20))
                            .centerInside()
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Marker driver_marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

                                    );
                                    driver_marker.setTag(username);
                                    driver_marker.showInfoWindow();

                                    mCurrLocationMarker = driver_marker;
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                }else {
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    Picasso.with(getContext())

                            .load(url)

                            .resize(250, 250)
                            .transform(new markerImageResizer(20))
                            .centerInside()
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Marker driver_marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

                                    );
                                    driver_marker.setTag(username);
                                    driver_marker.showInfoWindow();
                                    mCurrLocationMarker = driver_marker;
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                }
                if (animate) {
                    animate = false;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
                //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,mMap.getCameraPosition().zoom));
            }
        }


    };


    //move map camera
    //




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the User *asynchronously* -- don't block
                // this thread waiting for the User's response! After the User
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.permission)
                        .setMessage(R.string.need_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the User once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), R.string.denied, Toast.LENGTH_LONG).show();
                }
                return;
            }

            // othe
        }
    }

    public static void createMarker(final LocationModel model, Context ctx) {
        //first time started called

        if (!model.getUsername().equals(username)) {
            locations.put(model.getUsername(), model);

            if (pols.get(model.getUsername()) == null) {
                pols.put(model.getUsername(), new ArrayList<LatLng>());
            } else {
                List<LatLng> temp = pols.get(model.getUsername());

                pols.put(model.getUsername(), new ArrayList<LatLng>(temp));
            }


            polylines.put(model.getUsername(), model);
        }


        //first delete old markers
        for (Marker temp : markers) {

            temp.remove();

        }
        markers.clear();
        Log.d("users", model.getUsername() + " " + model.getUrl());

        //now add fresh location markers
        for (Map.Entry pair : locations.entrySet()) {
            //iterates  as number of users in circle
            final LocationModel mod;

            mod = (LocationModel) pair.getValue();
            //  Log.d("ur",mod.getUrl());
            final LatLng lt = new LatLng(mod.getLat(), mod.getLongt());
           // Log.d("url",mod.getUrl());
            Picasso.with(ctx)
                    .load(mod.getUrl())
                    .resize(250, 250)
                    .transform(new markerImageResizer(20))
                    .centerInside()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            MarkerOptions mo = new MarkerOptions();
                            mo.position(lt);


                            mo.anchor(0.5f, 0.5f);
                            mo.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            Marker mr = null;

                             mr = mMap.addMarker(mo);


                            markers.add(mr);

                            Log.d("markers", markers.toString());


                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
        }
        if (polyline.isChecked()) {
            Log.d("working", "");


            //create polyline objects for each of circle user and store in hashmap


            for (Map.Entry pair2 : pols.entrySet()) {
                Log.d("polys", pols.toString());
                if (model.getUsername().equals(pair2.getKey())) {
                    Log.d("check", model.getUsername() + " " + pair2.getKey());

                    pols.get(model.getUsername()).add((new LatLng(model.getLat(), model.getLongt())));


                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.GRAY);
                    polylineOptions.width(5);
                    Polyline po = mMap.addPolyline(polylineOptions);
                    po.setPoints((List<LatLng>) pols.get(model.getUsername()));
                }


            }


        }

    }
    /**
     * Adds a list of markers to the map.
     */
    public void addPolylineToMap(List<LatLng> latLngs) {
        PolylineOptions options = new PolylineOptions();
        for (LatLng latLng : latLngs) {
            options.add(latLng);
        }
        mMap.addPolyline(options);
    }

    /**
     * Clears all markers from the map.
     */
    public void clearMarkers() {
        mMap.clear();
        markers.clear();
    }


    private void updateNavigationStopStart() {
//        MenuItem startAnimation = this.menu.findItem(R.id.action_bar_start_animation);
//        startAnimation.setVisible(!animator.isAnimating() && directionsFetched);
//        MenuItem stopAnimation = this.menu.findItem(R.id.action_bar_stop_animation);
//        stopAnimation.setVisible(animator.isAnimating());
    }
    private List<LatLng> latLngs = new ArrayList<LatLng>();

    private class DirectionsFetcher extends AsyncTask<URL, Integer, Void> {


        private String origin;
        private String destination;

        public DirectionsFetcher(String origin,String destination) {
            this.origin = origin;
            this.destination = destination;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearMarkers();
            getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);

        }

        protected Void doInBackground(URL... urls) {
            try {
                HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });

                GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
                url.put("origin", origin);
                url.put("destination", destination);
                url.put("sensor",false);

                HttpRequest request = requestFactory.buildGetRequest(url);
                HttpResponse httpResponse = request.execute();
                DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);

                String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
                latLngs = PolyUtil.decode(encodedPoints);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Void result) {
            directionsFetched = true;
            System.out.println("Adding polyline");
            addPolylineToMap(latLngs);
            System.out.println("Fix Zoom");
            GoogleMapUtis.fixZoomForLatLngs(mMap, latLngs);
            System.out.println("Start anim");
            // animator.startAnimation(false, latLngs);
            operate=true;
            updateNavigationStopStart();
            getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
            float mapZoom = mMap.getCameraPosition().zoom >= 16 ? mMap.getCameraPosition().zoom : 16;
            LatLng markerPos = latLngs.get(0);
            LatLng secondPos = latLngs.get(1);
            float bearing = GoogleMapUtis.bearingBetweenLatLngs(markerPos, secondPos);

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(mMap.getCameraPosition().target)
                            .bearing(bearing + BEARING_OFFSET)
                            .tilt(90)
                            .zoom(mapZoom)
                            .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");


                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }

            );
            final boolean showads =PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
            if (showads) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

            }
        }
    }


    public static class DirectionsResult {

        @Key("routes")
        public List<Route> routes;

    }

    public static class Route {
        @Key("overview_polyline")
        public OverviewPolyLine overviewPolyLine;

    }

    public static class OverviewPolyLine {
        @Key("points")
        public String points;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==NavigationActivity.RESULT_CODE) {
            String from = data.getExtras().getString("from");
            String to = data.getExtras().getString("to");
            new DirectionsFetcher(from,to).execute();
        }
    }

    private Animator animator = new Animator();
    private final Handler mHandler = new Handler();

    public class Animator implements Runnable {

        public static final int ANIMATE_SPEEED = 1500;
        public static final int ANIMATE_SPEEED_TURN = 1500;
        public static final int BEARING_OFFSET = 20;

        private final Interpolator interpolator = new LinearInterpolator();

        private boolean animating = false;

        private List<LatLng> latLngs = new ArrayList<LatLng>();

        int currentIndex = 0;

        float tilt = 90;
        float zoom = 15.5f;
        boolean upward=true;

        long start = SystemClock.uptimeMillis();

        LatLng endLatLng = null;
        LatLng beginLatLng = null;

        boolean showPolyline = false;

        private Marker trackingMarker;

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();

        }

        public void stopAnimation() {
            animating=false;
            mHandler.removeCallbacks(animator);

        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;

            highLightMarker(0);

            if (showPolyLine) {
                polyLine = initializePolyLine();
            }

            // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
            LatLng markerPos = latLngs.get(0);
            LatLng secondPos = latLngs.get(1);

            setInitialCameraPosition(markerPos, secondPos);

        }

        private void setInitialCameraPosition(LatLng markerPos,
                                              LatLng secondPos) {

            float bearing = GoogleMapUtis.bearingBetweenLatLngs(markerPos,secondPos);

            trackingMarker = mMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet"));

            float mapZoom = mMap.getCameraPosition().zoom >=16 ? mMap.getCameraPosition().zoom : 16;

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(markerPos)
                            .bearing(bearing + BEARING_OFFSET)
                            .tilt(90)
                            .zoom(mapZoom)
                            .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");
                            animator.reset();
                            Handler handler = new Handler();
                            handler.post(animator);
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }
            );
        }

        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();


        private Polyline initializePolyLine() {
            //polyLinePoints = new ArrayList<LatLng>();
            rectOptions.add(latLngs.get(0));
            return mMap.addPolyline(rectOptions);
        }

        /**
         * Add the marker to the polyline.
         */
        private void updatePolyLine(LatLng latLng) {
            List<LatLng> points = polyLine.getPoints();
            points.add(latLng);
            polyLine.setPoints(points);
        }

        public void startAnimation(boolean showPolyLine,List<LatLng> latLngs) {
            if (trackingMarker!=null) {
                trackingMarker.remove();
            }
            this.animating = true;
            this.latLngs=latLngs;
            if (latLngs.size()>2) {
                initialize(showPolyLine);
            }

        }

        public boolean isAnimating() {
            return this.animating;
        }


        @Override
        public void run() {

            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
            LatLng intermediatePosition = SphericalUtil.interpolate(beginLatLng, endLatLng, t);

            Double mapZoomDouble = 18.5-( Math.abs((0.5- t))*5);
            float mapZoom =  mapZoomDouble.floatValue();

            System.out.println("mapZoom = " + mapZoom);

            trackingMarker.setPosition(intermediatePosition);

            if (showPolyline) {
                updatePolyLine(intermediatePosition);
            }

            if (t< 1) {
                mHandler.postDelayed(this, 16);
            } else {

                System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + latLngs.size());
                // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
                if (currentIndex<latLngs.size()-2) {

                    currentIndex++;

                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();


                    start = SystemClock.uptimeMillis();

                    Double heading = SphericalUtil.computeHeading(beginLatLng, endLatLng);

                    highLightMarker(currentIndex);

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(endLatLng)
                                    .bearing(heading.floatValue() /*+ BEARING_OFFSET*/) // .bearing(bearingL  + BEARING_OFFSET)
                                    .tilt(tilt)
                                    .zoom(mMap.getCameraPosition().zoom)
                                    .build();

                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );

                    //start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(this, 16);

                } else {
                    currentIndex++;
                    highLightMarker(currentIndex);
                    stopAnimation();
                }

            }
        }



        private LatLng getEndLatLng() {
            return latLngs.get(currentIndex+1);
        }

        private LatLng getBeginLatLng() {
            return latLngs.get(currentIndex);
        }

    };

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {
        if (markers.size()>=index+1) {
            highLightMarker(markers2.get(index));
        }
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {

        if (marker!=null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            marker.showInfoWindow();
        }

    }

    private void resetMarkers() {
        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }
    public static void displayPromptForEnablingGPS(final Activity activity)
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "your Gps is turned off Do you want open GPS setting?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
}


