package org.ultimatetoolsil.trackeru;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by mike on 6 Jan 2018.
 */

public class BatteryBroadcastReceiver extends BroadcastReceiver {
    private final static String BATTERY_LEVEL = "level";

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BATTERY_LEVEL, 0);
        SharedPreferences prerfs = PreferenceManager.getDefaultSharedPreferences(context);
        if(level<25){

            //MapFragment.mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
           //activate battery saver mode
            Boolean statusLocked = prerfs.edit().putBoolean("bsaver", true).commit();
            //Toast.makeText(context,R.string.battery,Toast.LENGTH_LONG).show();
            Log.d("low power","saver mode activated");
         }if(level>=25){
            Boolean statusLocked = prerfs.edit().putBoolean("bsaver", false).commit();
        }
    }
}
