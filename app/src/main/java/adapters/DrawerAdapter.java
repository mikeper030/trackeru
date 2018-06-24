package adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.ultimatetoolsil.trackeru.MainActivity;
import org.ultimatetoolsil.trackeru.MapFragment;
import org.ultimatetoolsil.trackeru.R;

import java.util.ArrayList;

import models.LocationModel;
import models.usercircleModel;

/**
 * Created by mike on 24 Dec 2017.
 */

public class DrawerAdapter extends BaseAdapter{
    private String tag ="adapter";
    Context c;
    public static int position = -1;
    public static String key=null;
    ArrayList<usercircleModel> circlesnames;
    LayoutInflater inflater;
    String username;

    public DrawerAdapter(Context c, ArrayList<usercircleModel>  circlesnames) {
        this.c = c;
        username = PreferenceManager.getDefaultSharedPreferences(c).getString("username", null);

        this.circlesnames = circlesnames;


    }


    @Override
    public int getCount() {
        return circlesnames.size();
    }

    @Override
    public Object getItem(int i) {
        return circlesnames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertview, ViewGroup viewGroup) {

        if (inflater == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.drawer_item, viewGroup, false);

        }


        final MyHolder3 holder = new MyHolder3(convertview);
        holder.groupname.setText(circlesnames.get(i).getName());

        // will get  each  user info from database per clicked circle
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get users of group
                if(holder.check.isChecked()){




                final ArrayList<String> users= new ArrayList<>();
                    // fetch list of circles than
                   // each row of circles will set listener on users real time location

                   MainActivity.dbr.child("public").child("circles").child(circlesnames.get(i).getKey()).child("users").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                               Log.d("snap", snapshot.getKey());

                               if (!snapshot.getKey().equals(username)) {

                                   //will get list of relevant users of cycle
                                   users.add(snapshot.getKey().toString());

                               }
                           }
                           Log.d(tag, users.toString());

                           for (int i = 0; i < users.size(); i++) {
                               final int a = i;
                               final LocationModel lm = new LocationModel();
                               //adding real time location listener for each user in arraylist
                               MainActivity.dbr.child("public").child("UsersLocation").child(users.get(i)).addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {


                                       //get user profile pic

                                       MainActivity.dbr.child("public").child("UsersLocation").child(users.get(a)).child("url").addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                             String  userpic=dataSnapshot.getValue(String.class);
                                               lm.setUrl(userpic);

                                              // Log.d(users.get(a),lm.getUrl());
                                           }

                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {

                                           }
                                       });

                                       //each time a change is triggered create marker function is called with new location object

                                        try {


                                            lm.setUsername(users.get(a));
                                            lm.setLat(dataSnapshot.child("lat").getValue(double.class));
                                            lm.setLongt(dataSnapshot.child("longt").getValue(double.class));
                                        }catch(Exception e){
                                            e.printStackTrace();
                                            }
                                       //LatLng loc =new LatLng(dataSnapshot.child("latitude").getValue(double.class),(dataSnapshot.child("longitude").getValue(double.class)) );

                                       MapFragment.createMarker(lm,c);
                                       Log.d("loc", String.valueOf(lm.getLat() + " " + lm.getLongt() + lm.getUsername())+lm.getUrl());
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
                   //store each users location in array list of location model
               }else{
                 MapFragment.mMap.clear();
               }
        }
        });





        //   Log.d("url",circlesinfo.get(i).getImgUrl().toString());




        return convertview;

    }

}
