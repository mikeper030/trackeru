package org.ultimatetoolsil.trackeru;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by mike on 16 Dec 2017.
 */

public class settings extends PreferenceActivity implements BillingProcessor.IBillingHandler{
    SwitchPreference autosaver;
    SwitchPreference savermode;
    String key;
    Preference ads;
    Preference policy;
    private InterstitialAd mInterstitialAd;
    BillingProcessor bp;
    static final String ITEM_SKU = "id1";
    boolean showads;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       if(showads) {
           if (mInterstitialAd.isLoaded()) {
               mInterstitialAd.show();
           } else {
               Log.d("TAG", "The interstitial wasn't loaded yet.");
           }
       }
       }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqyNcVY/2QYMK1cwsEb3iS2wco0utKHICnfTjKRtfCotPhhyFTpc4TwiD7Hvr6+4ywMhSh2dmsIun+Ms4yX2Bj2atKQJIBlFvEpIPsG8aAf7VyGH0SICuF7Yif7hinvXHIznfJfQa8ZHblt4nkFuJSvXjgNowD/HZ8NQg/dQRkm7BciMkk0fOO2kqKnmnRmRSq7ep3QZ/wa3WTdjV00SV4QFnpliSuHdVlMqpaNlESKo+G9vhnrCIUzTtNDa8IdCIQkTkKe7lMiZN2zXLkDRZC362YZT8aXUYCQ7pQOW7jc+8A9GWV7PkW0MYIKIQbKLlF1/eajPxDaOq/jmcYtNK9wIDAQAB";
        bp = new BillingProcessor(this, key, this);
        showads= PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ads", true);
        policy=(Preference)getPreferenceManager().findPreference("policy");
        if(showads) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/7069318342");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
        autosaver=(SwitchPreference) getPreferenceManager().findPreference("absaver");
         autosaver.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object o) {
              boolean isenabled=(boolean)o;
              if(isenabled){


              }
              return true;
          }
      });

        savermode=(SwitchPreference) getPreferenceManager().findPreference("bsaver");
        savermode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isenabled=(boolean)o;
                if(isenabled){
                   MapFragment.mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

                }else {
                    Log.d("false","false");
                    MapFragment.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                }




                return true;
            }
        });

     ads=(Preference)getPreferenceManager().findPreference("remove");
     ads.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
         @Override
         public boolean onPreferenceClick(Preference preference) {
//
             bp.purchase(settings.this,ITEM_SKU);
             return true;
         }
     });
     policy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
         @Override
         public boolean onPreferenceClick(Preference preference) {

             return true;
         }
     });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(@NonNull String s, @Nullable TransactionDetails transactionDetails) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean showads = prefs.edit().putBoolean("ads", false).commit();
        Intent main = new Intent(settings.this,MainActivity.class);
        startActivity(main);
        finish();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int i, @Nullable Throwable throwable) {

    }

    @Override
    public void onBillingInitialized() {

    }
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}
