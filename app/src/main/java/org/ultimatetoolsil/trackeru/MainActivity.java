package org.ultimatetoolsil.trackeru;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import adapters.PagerAdapter;
import utilities.PicassoClient;

import static org.ultimatetoolsil.trackeru.createprofile.checkIfAlreadyhavePermission;
import static org.ultimatetoolsil.trackeru.createprofile.requestForSpecificPermission;

/**
 * Created by mike on 9 Dec 2017.
 */

public class MainActivity extends AppCompatActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private ImageView img;
    private StorageReference storageRef;
    private FirebaseApp app;
    private String userID;
    private DatabaseReference myRef;
    private PagerAdapter mAdapter;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    public static String userid;
    public static DatabaseReference dbr;
    // Tab titles

    private String[] tabs = {"Map", "Circles"};


    @Override
    public void onBackPressed() {







    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {

            startActivity(new Intent(MainActivity.this, Signups.class));
            finish();
        } else {
            // Initilization


            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();

            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();

            //global variables
            //=============
            userid = userID;
            dbr = mFirebaseDatabase.getReference();


            //================
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            tabLayout.addTab(tabLayout.newTab().setText(R.string.map).setTag("map"));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.circles).setTag("circles"));


            final PagerAdapter adapter = new PagerAdapter
                    (getSupportFragmentManager(), tabLayout.getTabCount());
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(adapter);

            // AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            // params.setScrollFlags(0);  // clear all scroll flags

            // Adding Tabs

            /**
             * on swiping the viewpager make respective tab selected
             * */
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(" ");

                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        ImageView pr = (ImageView) findViewById(R.id.profile1);
        pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyAccount.class);
                startActivity(i);
            }
        });

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission(this)) {
                requestForSpecificPermission(this);
            }
        }
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Trackeru/";
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            Log.d("App", "Exception" + e.getMessage());
        }

        downloadprofileinfo();
    }


    private void downloadprofileinfo() {
        final ImageView profile = (ImageView) findViewById(R.id.profile1);
        final TextView prof = (TextView) findViewById(R.id.pname);
        //download the profile pic if profile pic is not present


            try {
                myRef.child("users").child(userID).child("picLink").addListenerForSingleValueEvent(new ValueEventListener() {

                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String url = dataSnapshot.getValue(String.class);
                          myRef.child("users").child(userid).child("rotation").addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(DataSnapshot dataSnapshot) {
                                  try {
                                     int rotation= dataSnapshot.getValue(int.class);
                                      PicassoClient.DownloadSingleImage(MainActivity.this,url,profile,rotation);

                                  }catch (Exception e){
                                      PicassoClient.DownloadSingleImage(MainActivity.this,url,profile,0);
                                  }
                              }

                              @Override
                              public void onCancelled(DatabaseError databaseError) {

                              }
                          });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
          //static data initialization


            } catch (Exception e) {
                e.printStackTrace();
            }




        //download profile name
        try {


            myRef.child("users").child(userID).child("full_name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    prof.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            myRef.child("users").child(userID).child("full_name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name =dataSnapshot.getValue(String.class);
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("full_name", name).apply();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            myRef.child("users").child(userID).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name =dataSnapshot.getValue(String.class);
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("phone", name).apply();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            myRef.child("users").child(userID).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name =dataSnapshot.getValue(String.class);
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("username", name).apply();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
           Intent i = new Intent(MainActivity.this,Signups.class);
           startActivity(i);
        }
    }





}
