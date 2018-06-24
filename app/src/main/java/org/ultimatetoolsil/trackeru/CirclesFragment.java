package org.ultimatetoolsil.trackeru;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapters.CustomAdapter1;
import adapters.DrawerAdapter;
import models.User;
import models.drawermodel;
import models.usercircleModel;
import outlander.showcaseview.ShowcaseViewBuilder;


/**
 * Created by mike on 9 Dec 2017.
 */

public class CirclesFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;

    private String userID;
    private DatabaseReference myRef;
    private ImageView menuu;
    private FirebaseAuth auth;
    private FloatingActionMenu menuRed;
    ListView listView,drawerlist;
    String key = "empty";
     Set<drawermodel> hs = new HashSet<>();
    CustomAdapter1 customAdapter1;
    DrawerAdapter drawerAdapter;
    private ImageView statics,listimage;
    private TextView staticst,listtext;
    private AdView mAdView; static int objnum=0;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private InterstitialAd mInterstitialAd;
   private Button dum;
    ShowcaseViewBuilder showcaseViewBuilder;
    ArrayList<usercircleModel> circles = new ArrayList<usercircleModel>();
    public static ArrayList<drawermodel> keys =  new ArrayList<>();
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), Signups.class));

        } else {
            //fetch list of groups from database

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            listView = getView().findViewById(R.id.ListView);
            statics=(ImageView)getActivity().findViewById(R.id.noitems);
            staticst=(TextView)getActivity().findViewById(R.id.noitemstext);
            listimage=(ImageView)view.findViewById(R.id.listnoimage);
            listtext=(TextView)view.findViewById(R.id.listnotext);
            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            menuRed = (FloatingActionMenu) view.findViewById(R.id.menu_red);
            final boolean showads = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
            if(showads) {
                mAdView = getView().findViewById(R.id.circles_banner);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                mInterstitialAd = new InterstitialAd(getActivity());
                mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/7069318342");
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
           dum=(Button)getView().findViewById(R.id.dum2);
            fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharSequence options[] = new CharSequence[]{getResources().getString(R.string.feed_group), getResources().getString(R.string.auto_check)};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.choose);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the User clicked on colors[which]
                            switch (which) {
                                case 1:
                                    verifycirclekey();
                                    Log.d("r", "0");

                                    break;
                                case 0:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.type_code);
                                    // I'm using fragment here so I'm using getView() to provide ViewGroup
                                    // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                                    View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.text_inpu_password, (ViewGroup) getView(), false);
                                    // Set up the input
                                    final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    builder.setView(viewInflated);

                                    // Set up the buttons
                                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            String inputs = input.getText().toString();
                                            SearchGroup(inputs);
                                        }
                                    });
                                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                    Log.d("r", "1");
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            });
            fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);

            fab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showads) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            Intent i = new Intent(getActivity(), Createcircle.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getActivity(), Createcircle.class);
                            startActivity(i);
                        }
                    } else {
                        Intent i = new Intent(getActivity(), Createcircle.class);
                        startActivity(i);
                    }
                }
            });


            menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean firsttime =PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("first2", true);
                    if(firsttime){
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("first2", false).apply();
                    showcaseViewBuilder = ShowcaseViewBuilder.init(getActivity());
                    showcaseViewBuilder.setTargetView(fab2)
                            .setBackgroundOverlayColor(0xdd70d2cd)
                            .setRingColor(0xccb9e797)

                            .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()))
                            //.setMarkerDrawable(getResources().getDrawable(R.drawable.arrow_up), Gravity.LEFT)
                            .addCustomView(R.layout.fabs_description_view, Gravity.TOP)
                            .setCustomViewMargin((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

                    showcaseViewBuilder.show();

                    showcaseViewBuilder.setClickListenerOnView(R.id.btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showcaseViewBuilder.hide();
                            showcase2();
                        }
                    });

                        }

                    menuRed.toggle(true);
                }
            });


        }

        listView.setAdapter((ListAdapter) customAdapter1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
              String info =  circles.get(position).getKey();
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                groupDetailsFragment fragment = (groupDetailsFragment) getFragmentManager().
                        findFragmentById(R.id.circlesdetaiils);
                if (fragment==null || ! fragment.isInLayout()) {
                    // smaller device
                    FragmentTransaction trans = getFragmentManager()
                            .beginTransaction();

                    Bundle bundle = new Bundle();
                    bundle.putString("key",info);
                    groupDetailsFragment fr = new groupDetailsFragment();

                    fr.setArguments(bundle);
                    trans.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
                    trans.replace(R.id.mainf,fr);
                    trans.addToBackStack(null);
                    trans.commit();
                }
                else {
                    FragmentTransaction trans = getFragmentManager()
                            .beginTransaction();

                    Bundle bundle = new Bundle();
                    bundle.putString("key",info);
                    groupDetailsFragment fr = new groupDetailsFragment();

                    fr.setArguments(bundle);
                    trans.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
                    trans.replace(R.id.circlesdetaiils,fr);
                    trans.addToBackStack(null);
                    trans.commit();
                }









            }
        });


        //====================================== database real time listeners======================================================
       try {


           myRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                 if(!dataSnapshot.hasChild("circles")){
                     ShowNoItems();
                 }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
       }catch (Exception e){
           ShowNoItems();
       }
        myRef.child("users").child(userID).child("circles").child("listdata").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getupdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getupdates(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


                getupdates(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

   }

    private void showcase2() {
        showcaseViewBuilder = ShowcaseViewBuilder.init(getActivity());
        showcaseViewBuilder.setTargetView(fab3)
                .setBackgroundOverlayColor(0xdd70d2cd)
                .setRingColor(0xccb9e797)

                .setRingWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()))
                //.setMarkerDrawable(getResources().getDrawable(R.drawable.arrow_up), Gravity.LEFT)
                .addCustomView(R.layout.fabs_description_view, Gravity.TOP)
                .setCustomViewMargin((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

        showcaseViewBuilder.show();

        showcaseViewBuilder.setClickListenerOnView(R.id.btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseViewBuilder.hide();

            }
        });

    }

    private void ShowNoItems() {
        Log.d("items",String.valueOf(circles.size()));
        statics.setVisibility(View.VISIBLE);
        staticst.setVisibility(View.VISIBLE);
        listtext.setVisibility(View.VISIBLE);
        listimage.setVisibility(View.VISIBLE);
    }


    private boolean verifycirclekey() {
        ArrayList<String> smsbody = new ArrayList<>();
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);


        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for (int idx = 0; idx < 40; idx++) {
                    if (cursor.getColumnName(idx).equals("body")) {
                        smsbody.add(cursor.getString(idx));

                    }
                    msgData = " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx) + ",";


                }
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
        //Log.d("sd",smsbody.toString());
        //get the key from sms messages

        for (int i = 0; i < smsbody.size(); i++) {

            String body = smsbody.get(i);
            //Log.d("tt",body);
            Pattern pattern = Pattern.compile("(\\[)(.*?)(\\])");
            Matcher matcher = pattern.matcher(body);
            while (matcher.find()) {
                key = matcher.group(2);
                Log.d("to", String.valueOf(key));
            }


        }
        //need to search for message in smsbody arraylist
        if (!key.equals("empty")) {

           SearchGroup(key);


        }

        return true;
    }

    //=========================================================================
    // }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        boolean showads = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
        if (showads) {
            return inflater.inflate(R.layout.circles_fragment_ads, container, false);
        }
        return inflater.inflate(R.layout.circles_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_manu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Intent i = new Intent(getActivity(), settings.class);
                startActivity(i);
                // Not implemented here
                return true;


            default:
                break;
        }

        return true;
    }


    //==============loading data  from firebase===============================


    public void getupdates(DataSnapshot dataSnapshot){
        //get info on operation

         objnum=0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
             objnum+=400;
        }

        Log.d("num",String.valueOf(objnum));
        boolean add = true;

        //detect file deleted
        if (CustomAdapter1.position != -1) {
            circles.remove(CustomAdapter1.position);
            customAdapter1 = new CustomAdapter1(getActivity(), circles);
            listView.setAdapter((ListAdapter) customAdapter1);
            CustomAdapter1.position = -1;
            //detect file added
        } else {

            Log.d("ss", String.valueOf(dataSnapshot.getValue()));
            usercircleModel d = new usercircleModel();
            d.setName(dataSnapshot.getValue(usercircleModel.class).getName());
            d.setImgUrl(dataSnapshot.getValue(usercircleModel.class).getImgUrl());
            d.setKey(dataSnapshot.getValue(usercircleModel.class).getKey());
            try {
                d.setImgRotation(dataSnapshot.getValue(usercircleModel.class).getImgRotation());
            }catch (Exception e){
                d.setImgRotation(0);
            }


            for (int i = 0; i < circles.size(); i++) {
                if (circles.get(i).getKey().equals(d.getKey())) {
                    add = false;
                }

            }
            if (add) {
               circles.add(d);

            }


        }
      //main listview
        if (circles.size() > 0) {
            Log.d("items",String.valueOf(circles.size()));
            customAdapter1 = new CustomAdapter1(getActivity(), circles);
            listView.setAdapter((ListAdapter) customAdapter1);
            listView.invalidateViews();
     // drawer listview
            statics.setVisibility(View.INVISIBLE);
            staticst.setVisibility(View.INVISIBLE);
            listtext.setVisibility(View.INVISIBLE);
            listimage.setVisibility(View.INVISIBLE);
            drawerAdapter = new DrawerAdapter(getActivity(), circles);
            MapFragment.drawerlist.setAdapter((ListAdapter) drawerAdapter);
            Log.d("tr", String.valueOf(drawerAdapter.getCount()));



        } if(circles.size()==0) {
            Log.d("items",String.valueOf(circles.size()));
            statics.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
        }


    }
private void SearchGroup(final String key){
    try{
    //add circle to user's listview
    myRef.child("public").child("circles").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            usercircleModel usm;
            usm = dataSnapshot.getValue(usercircleModel.class);
            myRef.child("users").child(userID).child("circles").child("listdata").child(key).setValue(usm);


         //   Log.d("usm", usm.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
        //add user to public circle info
        myRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = new User();
                user.setFull_name(dataSnapshot.child("full_name").getValue(String.class));
                user.setPhone(dataSnapshot.child("phone").getValue(String.class));
                user.setPicLink(dataSnapshot.child("picLink").getValue(String.class));
                user.setEmail(dataSnapshot.child("email").getValue(String.class));
                user.setUsername(dataSnapshot.child("username").getValue(String.class));
                myRef.child("public").child("circles").child(key).child("users").child(user.getUsername()).setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    } catch (Exception e) {
        Toast.makeText(getActivity(), R.string.group_not_ex, Toast.LENGTH_LONG).show();
    }

}

}
