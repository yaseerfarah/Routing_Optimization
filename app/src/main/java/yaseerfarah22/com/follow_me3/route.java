package yaseerfarah22.com.follow_me3;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.geocode.LocatorSuggestionParameters;
import com.esri.core.tasks.geocode.LocatorSuggestionResult;

import java.util.ArrayList;
import java.util.List;

public class route extends AppCompatActivity {

    EditText source;
    EditText distination;
    ImageButton plus_des;
    ImageButton locat;
    Button routing_fast;
    Button routing_short;

    String sourec_address;
    ArrayList<String> destination_address;
    SpatialReference ref;
    Point map_point;
    Spatial_Reference ref_map;
    Envelope mapExtent;
    ListView suggestion;
    double lat_y;
    double long_x;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);


        source = (EditText) findViewById(R.id.editText_source);
        distination = (EditText) findViewById(R.id.editText_destination);
        plus_des = (ImageButton) findViewById(R.id.add_des);
        routing_fast = (Button) findViewById(R.id.button_routing_fastest);
        routing_short = (Button) findViewById(R.id.button_routing_shortest);
        suggestion=(ListView)findViewById(R.id.list_sugg);
        locat=(ImageButton) findViewById(R.id.location_lat);

        ref_map=(Spatial_Reference) getIntent().getSerializableExtra("ref");
        ref=ref_map.get_ref_map();
        map_point=ref_map.get_point_map();
        mapExtent=ref_map.get_Extent_map();


        locat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long_x=getIntent().getDoubleExtra("long_x",0);
                lat_y=getIntent().getDoubleExtra("lat_y",0);
                if(long_x==0&&lat_y==0){
                    Toast.makeText(route.this,"Error",Toast.LENGTH_LONG).show();
                }else {
                    source.setText("User Location");

                }

            }
        });




        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence!="User Location"){
                    new Locator_Suggestion_Result().execute(charSequence,"source");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        distination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new Locator_Suggestion_Result().execute(charSequence,"des");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        destination_address = new ArrayList<String>();

        plus_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (distination.getText().toString().matches("")) {

                    distination.setError(getString(R.string.empty_des));
                    //Toast.makeText(route.this,getString(R.string.empty_des),Toast.LENGTH_SHORT).show();
                } else {
                    destination_address.add(distination.getText().toString());
                    distination.setText("");
                    Toast.makeText(route.this, "hi", Toast.LENGTH_SHORT).show();

                }
            }
        });

        routing_fast.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (!source.getText().toString().matches("") && !distination.getText().toString().matches("")) {
                    sourec_address = source.getText().toString();
                    destination_address.add(distination.getText().toString());

                    if(isOnline()){
                        Intent i = new Intent(route.this, routing_map.class);
                        if(sourec_address.matches("User Location")){
                            i.putExtra("flag",true);
                            i.putExtra("lat_y",lat_y);
                            i.putExtra("long_x",long_x);
                        }
                        else {
                            i.putExtra("flag",false);
                            i.putExtra("sourc_add", sourec_address);
                        }


                        i.putExtra("routing","fast");
                    i.putExtra("destination_add", destination_address);
                    startActivity(i);}
                    else {
                        Toast.makeText(getApplicationContext(),"check your internet connection",Toast.LENGTH_SHORT).show();

                    }
                } else {
                    source.setError("write");
                    distination.setError(getString(R.string.empty_des));

                }
            }
        });

        routing_short.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (!source.getText().toString().matches("") && !distination.getText().toString().matches("")) {
                    sourec_address = source.getText().toString();
                    destination_address.add(distination.getText().toString());

                    if(isOnline()){
                        Intent i = new Intent(route.this, routing_map.class);
                        if(sourec_address.matches("User Location")){
                            i.putExtra("flag",true);
                            i.putExtra("lat_y",lat_y);
                            i.putExtra("long_x",long_x);
                        }
                        else {
                            i.putExtra("flag",false);
                            i.putExtra("sourc_add", sourec_address);
                        }


                        i.putExtra("routing","short");
                        i.putExtra("destination_add", destination_address);
                        startActivity(i);}
                    else {
                        Toast.makeText(getApplicationContext(),"check your internet connection",Toast.LENGTH_SHORT).show();

                    }
                } else {
                    source.setError("write");
                    distination.setError(getString(R.string.empty_des));

                }
            }
        });



    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    private class Locator_Suggestion_Result extends AsyncTask<CharSequence, Void, List<LocatorSuggestionResult>> {

        Exception ex;
        String so_or_des;

        @Override
        protected List<LocatorSuggestionResult> doInBackground(CharSequence... charSequences) {
            ex=null;
            List<LocatorSuggestionResult> results=null;
            try {

                so_or_des=charSequences[1].toString();
                Locator locator = Locator.createOnlineLocator();
                LocatorSuggestionParameters suggestionParameters = new LocatorSuggestionParameters(String.valueOf(charSequences[0]));
                //suggestionParameters.setLocation(map_point,ref);
                //suggestionParameters.setSearchExtent(mapExtent,ref);
                suggestionParameters.setCountryCode("EGY, EG");
                suggestionParameters.setMaxSuggestions(10);
                results=locator.suggest(suggestionParameters);

            }
            catch (Exception e){
                ex=e;

            }


            return results;

        }


        @Override
        protected void onPostExecute(List<LocatorSuggestionResult> _results) {
           if(ex!=null){
               Toast.makeText(route.this,ex.toString(),Toast.LENGTH_LONG).show();
           }
           else {
               if(_results!=null){
                   final ArrayList<String> suggestedAddresses = new ArrayList<String>(_results.size());

                   // Iterate the suggestions
                   for (LocatorSuggestionResult result : _results) {
                       suggestedAddresses.add(result.getText());

                   }
                  // Toast.makeText(route.this,String.valueOf(suggestedAddresses.size()),Toast.LENGTH_SHORT).show();
                   //source.clearListSelection();
                   suggestion.setAdapter(new ArrayAdapter(route.this, R.layout.drawer_layout_text2,suggestedAddresses));
                   suggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                           String text=suggestedAddresses.get(i);
                           if(so_or_des=="source"){
                               source.setText(text);
                           }
                           else {
                               distination.setText(text);
                           }
                       }
                   });




               }
               else {
                   Toast.makeText(route.this,"no",Toast.LENGTH_LONG).show();
               }
           }



        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }



}