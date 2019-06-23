package yaseerfarah22.com.follow_me3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.internal.tasks.ags.n;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.geocode.LocatorSuggestionParameters;
import com.esri.core.tasks.geocode.LocatorSuggestionResult;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    AutoCompleteTextView mSearchEditText;
    MapView mMapView;
    GraphicsLayer mLocationLayer;
    Point mLocationLayerPoint;
    String mLocationLayerPointString;
    ImageButton search_button;
    ImageButton rout_button;
    ImageButton location_button;
    final String ClientID = "HNso5DbSmS3392fS";
    final String Token = "Q__EszKDMJ8duzbIK2XN-W8VOLSeIdAZAHzz0qxdkZkO2uQ0jxL61ZIt9G0BaHGlg6Y7KW8TS6r7uT9jKnG3xkmSlNe4-gDwhLspe759KMbVgoe6ogqcxrpP0vOol2BU";
    public SpatialReference ref;
    Spatial_Reference ref_map;
    Envelope mapExtent;
    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;
    LocationDisplayManager location;
    LocationManager manager;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    private int requestCode = 2;
    boolean flag=false;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_map);

        if(isOnline()) {

            Intent in = new Intent(MainActivity.this, Background_Service.class);
            //startService(in);

            mapExtent = new Envelope();
            notification = new NotificationCompat.Builder(this);
            notification.setAutoCancel(true);

            ref_map = new Spatial_Reference();
            mSearchEditText = (AutoCompleteTextView) findViewById(R.id.searchText);
            mSearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    new Locator_Suggestion_Result1().execute(charSequence);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            ArcGISRuntime.setClientId(ClientID);
            mMapView = (MapView) findViewById(R.id.map);
            mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
                public void onStatusChanged(Object source, STATUS status) {
                    if ((source == mMapView) && (status == OnStatusChangedListener.STATUS.INITIALIZED)) {
                        ref = mMapView.getSpatialReference();
                        mLocationLayer = new GraphicsLayer();
                        mMapView.addLayer(mLocationLayer);
                        mMapView.getExtent().queryEnvelope(mapExtent);
                        ref_map.set_ref_map(ref);
                        ref_map.set_point_map(mMapView.getCenter());
                        ref_map.set_Extent_map(mapExtent);
                        location = mMapView.getLocationDisplayManager();
                    }


                }
            });

            search_button = (ImageButton) findViewById(R.id.searchButton);
            rout_button = (ImageButton) findViewById(R.id.rout);
            location_button = (ImageButton) findViewById(R.id.location);

            search_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mSearchEditText.getText().toString().matches("")){
                        mSearchEditText.setError("Write the address");
                    }
                    else {

                        if (isOnline()) {
                            onSearchButtonClicked(mSearchEditText);
                        } else {
                            Toast.makeText(getApplicationContext(), "check your internet connection", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });


            rout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    } else {
                        boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) ==
                                PackageManager.PERMISSION_GRANTED;
                        boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) ==
                                PackageManager.PERMISSION_GRANTED;

                        if (!(permissionCheck1 && permissionCheck2)) {
                            // If permissions are not already granted, request permission from the user.
                            ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
                        } else {
                            diplay_loc();
                            flag=true;
                             i = new Intent(MainActivity.this, route.class);
                            i.putExtra("lat_y",location.getLocation().getLatitude());
                            i.putExtra("long_x",location.getLocation().getLongitude());
                            i.putExtra("ref", ref_map);
                            startActivity(i);
                        }


                    }


                }
            });


            location_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    } else {
                        boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) ==
                                PackageManager.PERMISSION_GRANTED;
                        boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) ==
                                PackageManager.PERMISSION_GRANTED;

                        if (!(permissionCheck1 && permissionCheck2)) {
                            // If permissions are not already granted, request permission from the user.
                            ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
                        } else {
                            diplay_loc();
                            flag=true;
                        }


                    }




               /* Polygon polygon = new Polygon();

                polygon.setXY(0,31.201531,30.026925);
                polygon.setXY(1,31.209856,30.029192);
                polygon.setXY(2,31.205436,30.014849);
                polygon.setXY(3,31.211616,30.015778);
               Point p=new Point(31.207309,30.022770);
                Envelope e=new Envelope(p,0.008,0.008);
                polygon.addEnvelope(e,false);
                GeometryEngine engine = new GeometryEngine();
                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(Color.BLUE,SimpleFillSymbol.STYLE.CROSS);
                Graphic g=new Graphic(engine.project(polygon,SpatialReference.create(4326),ref),fillSymbol);
                mLocationLayer.addGraphic(g);
                LocalGeofence local=new LocalGeofence();
                local.setFence(polygon,ref);

                LocalGeofence.FenceInformation fence=local.latestLocation(p);
                //Build the notification
                notification.setSmallIcon(R.drawable.follow_icon);

                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle("Here is the title");
                notification.setContentText(fence.change.toString());
/*
                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);
*/
                    //Builds notification and issues it
                    // NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // nm.notify(uniqueID, notification.build());


                }
            });
        }


        else {
            Toast.makeText(getApplicationContext(),"check your internet connection",Toast.LENGTH_SHORT).show();
        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Location permission was granted. This would have been triggered in response to failing to start the
            // LocationDisplay, so try starting this again.
            diplay_loc();
            flag=true;
            i.putExtra("lat_y",location.getLocation().getLatitude());
            i.putExtra("long_x",location.getLocation().getLongitude());
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    private  void  diplay_loc(){
        location.setAutoPanMode(LocationDisplayManager.AutoPanMode.NAVIGATION);
        if (!location.isStarted()){
            location.start();
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////
private void buildAlertMessageNoGps() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


    //////////////////////////////////////////////////////////////////////////////////////////
    public void onSearchButtonClicked(View view){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        String address = mSearchEditText.getText().toString();
        Toast.makeText(MainActivity.this,"search",Toast.LENGTH_LONG).show();
        executeLocatorTask(address);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void executeLocatorTask(String address) {
        // Create Locator parameters from single line address string
        LocatorFindParameters findParams = new LocatorFindParameters(address);

        // Use the centre of the current map extent as the find location point
        findParams.setLocation(mMapView.getCenter(), ref);

    /*// Calculate distance for find operation
    Envelope mapExtent = new Envelope();
    mMapView.getExtent().queryEnvelope(mapExtent);
    // assume map is in metres, other units wont work, double current envelope
    double distance = (mapExtent != null && mapExtent.getWidth() > 0) ? mapExtent.getWidth() * 2 : 10000;
    findParams.setDistance(distance);*/
        findParams.setMaxLocations(2);

        /*// Set address spatial reference to match map
        findParams.setOutSR(ref);*/

        // Execute async task to find the address
        new LocatorAsyncTask().execute(findParams);
        mLocationLayerPointString = address;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class LocatorAsyncTask extends AsyncTask<LocatorFindParameters, Void, List<LocatorGeocodeResult>> {
        private Exception mException;


        @Override
        protected List<LocatorGeocodeResult> doInBackground(LocatorFindParameters... params) {
            mException = null;
            List<LocatorGeocodeResult> results = null;
            Locator locator = Locator.createOnlineLocator();
            try {
                results = locator.find(params[0]);
            } catch (Exception e) {
                mException = e;
            }
            return results;
        }

        protected void onPostExecute(List<LocatorGeocodeResult> result) {
            if (mException != null) {
           /* Log.w("PlaceSearch", "LocatorSyncTask failed with:");
            mException.printStackTrace();*/
                Toast.makeText(MainActivity.this,mException.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            if (result.size() == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.noResultsFound), Toast.LENGTH_LONG).show();
            } else {
                // Use first result in the list
                LocatorGeocodeResult geocodeResult = result.get(0);
                final GeometryEngine engine = new GeometryEngine();
                // get return geometry from geocode result
                Point resultPoint = geocodeResult.getLocation();
                // create marker symbol to represent location
                SimpleMarkerSymbol resultSymbol = new SimpleMarkerSymbol(Color.RED, 16, SimpleMarkerSymbol.STYLE.CROSS);
                // create graphic object for resulting location
                Graphic resultLocGraphic = new Graphic(engine.project(resultPoint, SpatialReference.create(4326), ref), resultSymbol);
                // add graphic to location layer
                mLocationLayer.addGraphic(resultLocGraphic);

           /* // create text symbol for return address
            String address = geocodeResult.getAddress();
            TextSymbol resultAddress = new TextSymbol(20, address, Color.BLACK);
            // create offset for text
            resultAddress.setOffsetX(-4 * address.length());
            resultAddress.setOffsetY(10);
            // create a graphic object for address text
            Graphic resultText = new Graphic(resultPoint, resultAddress);
            // add address text graphic to location graphics layer
            mLocationLayer.addGraphic(resultText);*/

                mLocationLayerPoint = resultPoint;

                // Zoom map to geocode result location
                mMapView.setExtent(engine.project(geocodeResult.getLocation(), SpatialReference.create(4326), ref), 2);
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    private class Locator_Suggestion_Result1 extends AsyncTask<CharSequence, Void, List<LocatorSuggestionResult>> {

        Exception ex;

        @Override
        protected List<LocatorSuggestionResult> doInBackground(CharSequence... charSequences) {
            ex=null;
            List<LocatorSuggestionResult> results=null;
            try {


                Locator locator = Locator.createOnlineLocator();
                LocatorSuggestionParameters suggestionParameters = new LocatorSuggestionParameters(String.valueOf(charSequences[0]));
                //suggestionParameters.setLocation(map_point,ref);
                //suggestionParameters.setSearchExtent(mMapView.getMaxExtent(),ref);
                suggestionParameters.setCountryCode("EGY, EG");
                suggestionParameters.setMaxSuggestions(3);
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
                Toast.makeText(MainActivity.this,ex.toString(),Toast.LENGTH_LONG).show();
            }
            else {
                if(_results!=null){
                    ArrayList<String> suggestedAddresses = new ArrayList<String>(_results.size());

                    // Iterate the suggestions
                    for (LocatorSuggestionResult result : _results) {
                        suggestedAddresses.add(result.getText());

                    }
                   // Toast.makeText(MainActivity.this,String.valueOf(suggestedAddresses.size()),Toast.LENGTH_SHORT).show();
                   // mSearchEditText.clearListSelection();
                    mSearchEditText.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.drawer_layout_text2,suggestedAddresses));



                }
                else {
                    Toast.makeText(MainActivity.this,"no",Toast.LENGTH_LONG).show();
                }
            }



        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(flag){
        location.stop();}
    }

    /////////////////////////////////////////////////////////////////////////////////////////

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


         if (id == R.id.notification1) {


            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
                } else {
                    diplay_loc();
                    flag=true;
                }


            }


            //




            Polygon polygon = new Polygon();
            Point p=new Point(31.207309,30.022770);
            Envelope e=new Envelope(p,0.008,0.008);
            polygon.addEnvelope(e,false);
            GeometryEngine engine = new GeometryEngine();
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(Color.BLUE,SimpleFillSymbol.STYLE.CROSS);
            Graphic g=new Graphic(engine.project(polygon,SpatialReference.create(4326),ref),fillSymbol);
            mLocationLayer.addGraphic(g);
            LocalGeofence local=new LocalGeofence();
            local.setFence(polygon,ref);
            Point last_location=new Point(location.getLocation().getLongitude(),location.getLocation().getLatitude());

            LocalGeofence.FenceInformation fence=local.latestLocation(last_location);
            //Build the notification
            notification.setSmallIcon(R.drawable.follow_icon);

            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("The State is");
            notification.setContentText(fence.change.toString());
            //Builds notification and issues it
             NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
             nm.notify(uniqueID, notification.build());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}
