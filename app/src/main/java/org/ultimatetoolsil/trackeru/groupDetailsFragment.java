package org.ultimatetoolsil.trackeru;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import adapters.CustomAdapter2;
import models.User;



/**
 * Created by mike on 9 Dec 2017.
 */

public class groupDetailsFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;

    private String userID;
    private DatabaseReference myRef;
    private ImageView menuu;
    private FirebaseAuth auth;
    private String key2;
    private FloatingActionMenu menuRed;
    ListView listView;
    String key = "empty";
    CustomAdapter2 customAdapter2;
    int deleteposition = -1;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private TextView statics;
   private AdView mAdView;
    ArrayList<User> circlesinfo = new ArrayList<User>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle =getArguments();
        if(bundle!=null){
            key2=bundle.getString("key");
            statics.setVisibility(View.INVISIBLE);
         Log.d("bundle",key2);
        //key value is passed from circlesfragment onclick
            myRef.child("public").child("circles").child(key2).child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    getupdates(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }else {
           // Toast.makeText(getActivity(),"Error please try again later",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), login.class));

        } else {
            //fetch list of groups from database
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            listView = getView().findViewById(R.id.ListView2);
            statics = (TextView)getView().findViewById(R.id.stat);

            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            boolean showads = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
            if(showads) {
                mAdView = getView().findViewById(R.id.group_banner);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }

            listView.setAdapter((ListAdapter) customAdapter2);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                }
            });


            //====================================== database real time listeners======================================================

        }
    }




    //=========================================================================
    // }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view=null;
        boolean showads = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("ads", true);
        if(showads) {
            view = inflater.inflate(R.layout.groupdetails_fragment_ads, container, false);
        }else {
            view = inflater.inflate(R.layout.groupdetails_fragment, container, false);
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();

       // handle back click
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("df", "keyCode: " + keyCode);
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i("df", "onKey Back listener is working!!!");
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
        return view;

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


    //==============loading users data  from firebase public directory===============================


    public void getupdates(DataSnapshot dataSnapshot) {
        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
       Log.d("group2",dataSnapshot.toString());


        while (items.hasNext()){
            User user= new User();
            DataSnapshot item = items.next();
            if(user.getPicLink()!=null){
                user.setPicLink(item.child("picLink").getValue().toString());
            }

            user.setUsername(item.child("username").getValue().toString());
            user.setStatus("online");
        circlesinfo.add(user);
        }



        if (circlesinfo.size() > 0) {
            customAdapter2 = new CustomAdapter2(getActivity(), circlesinfo);
            listView.setAdapter((ListAdapter) customAdapter2);
            listView.invalidateViews();
        } else {
            Toast.makeText(getActivity(), R.string.no_data, Toast.LENGTH_SHORT).show();
        }


    }


}
