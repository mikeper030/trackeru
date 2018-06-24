package org.ultimatetoolsil.trackeru;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

import java.util.ArrayList;
import java.util.List;
public class NavigationActivity extends AppCompatActivity {
    private AutoCompleteTextView from;
    private AutoCompleteTextView to;
    private static final String PLACES_API_KEY = "AIzaSyCwM1V3-5If1N-gZuPS_h5jrPgm13GVg6w";
    private AdView mAdView1;

    private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    protected static final int RESULT_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean showads = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ads", true);
        if(showads) {
            setContentView(R.layout.activity_navigation_ads);
            mAdView1 = (AdView) findViewById(R.id.navigation_banner);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView1.loadAd(adRequest);
         }else {
            setContentView(R.layout.activity_navigation);
        }

        Button btnLoadDirections = (Button) findViewById(R.id.load_directions);
        btnLoadDirections.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("from", from.getText().toString());
                data.putExtra("to", to.getText().toString());
                NavigationActivity.this.setResult(RESULT_CODE, data);
                NavigationActivity.this.finish();
            }
        });

        from = (AutoCompleteTextView) findViewById(R.id.from);
        to = (AutoCompleteTextView) findViewById(R.id.to);

        //from.setText("Enter address");
       // to.setText("Enter address");

        from.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));
        to.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));
    }

private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
}
    private static final String PLACES_AUTOCOMPLETE_API = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

    private ArrayList<String> autocomplete(String input) {

        ArrayList<String> resultList = new ArrayList<String>();

        try {

            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                                                                                        @Override
                                                                                        public void initialize(HttpRequest request) {
                                                                                            request.setParser(new JsonObjectParser(JSON_FACTORY));
                                                                                        }
                                                                                    }
            );

            GenericUrl url = new GenericUrl(PLACES_AUTOCOMPLETE_API);
            url.put("input", input);
            url.put("key", PLACES_API_KEY);
            url.put("sensor",false);

            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            PlacesResult directionsResult = httpResponse.parseAs(PlacesResult.class);

            List<Prediction> predictions = directionsResult.predictions;
            for (Prediction prediction : predictions) {
                resultList.add(prediction.description);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultList;
    }

    public static class PlacesResult {

        @Key("predictions")
        public List<Prediction> predictions;

    }

    public static class Prediction {
        @Key("description")
        public String description;

        @Key("id")
        public String id;

    }
}