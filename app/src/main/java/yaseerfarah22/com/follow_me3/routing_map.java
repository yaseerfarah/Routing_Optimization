package yaseerfarah22.com.follow_me3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Graphic;
import com.esri.core.portal.Portal;
import com.esri.core.portal.WebMap;
import com.esri.core.symbol.CompositeSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.graphics.Color.BLACK;

public class routing_map extends AppCompatActivity {

    String sourec_address;
    ArrayList<String> destination_address;
    ArrayList<Point> destination_points;
    MapView map_view;
    SpatialReference ref;
    GraphicsLayer mGraphicsLayer;
    GraphicsLayer mGraphicsLayer_road;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    final String ClientID = "HNso5DbSmS3392fS";
    final String Token = "Q__EszKDMJ8duzbIK2XN-W8VOLSeIdAZAHzz0qxdkZkO2uQ0jxL61ZIt9G0BaHGlg6Y7KW8TS6r7uT9jKnG3xkmSlNe4-gDwhLspe759KMbVgoe6ogqcxrpP0vOol2BU";
    private ProgressDialog mProgressDialog;
    private static final String URL = "http://arcgis.com";
    String id ="c8f704b7339b4902ac655ad592f5e862";
    Portal mPortal;
   FrameLayout f;
    double lat_y;
    double long_x;
    boolean flag;
    boolean is_fast;
    TextView arrive;
    TextView distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ArcGISRuntime.setClientId("HNso5DbSmS3392fS");

        if (isOnline()) {
            f = (FrameLayout) findViewById(R.id.content_frame);
            destination_address = new ArrayList<String>();


            destination_address = getIntent().getStringArrayListExtra("destination_add");
            destination_points = new ArrayList<>();
            flag=getIntent().getBooleanExtra("flag",false);
            if(flag){
                lat_y=getIntent().getDoubleExtra("lat_y",0);
                long_x=getIntent().getDoubleExtra("long_x",0);
            }
            else {
                sourec_address = getIntent().getStringExtra("sourc_add");
            }

            String route=getIntent().getStringExtra("routing");
            if(route.matches("fast")){
                is_fast=true;
            }
            else {
                is_fast=false;
            }

            arrive=(TextView)findViewById(R.id.arraivel);
            distance=(TextView)findViewById(R.id.distance);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Finding Route");
            mProgressDialog.setMessage("Please wait…");
            // mProgressDialog.show();

            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setUserAccount("yaseer_farah", "y010195101700");
            userCredentials.setUserToken(Token, ClientID);
            mPortal = new Portal(URL, userCredentials);
            // Create a new instance of WebMap
            WebMap.newInstance(id, mPortal, new CallbackListener<WebMap>() {

                @Override
                public void onError(Throwable e) {
                    Log.w("Place_Error", e);
                    Toast.makeText(routing_map.this, e.toString(), Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCallback(final WebMap webmap) {

                    // The WebMap has been created - switch to UI thread to create MapView
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Create a MapView from the WebMap
                            if (webmap != null) {
                                map_view = new MapView(routing_map.this, webmap, null, null);

                                map_view.setOnStatusChangedListener(new OnStatusChangedListener() {

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void onStatusChanged(Object source, STATUS status) {
                                        switch (status) {
                                            case INITIALIZED:
                                                // MapView initialization complete so dismiss the progress dialog
                                                ref = map_view.getSpatialReference();
                                                mGraphicsLayer = new GraphicsLayer();
                                                mGraphicsLayer_road = new GraphicsLayer();
                                                map_view.addLayer(mGraphicsLayer);
                                                map_view.addLayer(mGraphicsLayer_road);
                                                new find_route().execute();

                                                break;
                                            case INITIALIZATION_FAILED:
                                                Toast.makeText(routing_map.this, "i_e",
                                                        Toast.LENGTH_LONG).show();
                                                break;
                                            case LAYER_LOADED:
                                            case LAYER_LOADING_FAILED:
                                                break;
                                        }
                                    }
                                });

                                // Display the MapView
                                f.addView(map_view);
                            }

                        }
                    });

                }
            });


        }
        else {
            Toast.makeText(getApplicationContext(),"check your internet connection",Toast.LENGTH_SHORT).show();

        }








        /*
        map_view = (MapView) findViewById(R.id.map);
        destination_address=new ArrayList<String>();

       sourec_address= getIntent().getStringExtra("sourc_add");
       destination_address=getIntent().getStringArrayListExtra("destination_add");
       destination_points=new ArrayList<>();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Finding Route");
        mProgressDialog.setMessage("Please wait…");
       // mProgressDialog.show();
        map_view.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {

                if (status == OnStatusChangedListener.STATUS.INITIALIZED && o == map_view) {

                    ref = map_view.getSpatialReference();
                    mGraphicsLayer = new GraphicsLayer();
                    mGraphicsLayer_road=new GraphicsLayer();
                    map_view.addLayer(mGraphicsLayer);
                    map_view.addLayer(mGraphicsLayer_road);
                    //Toast.makeText(routing_map.this,destination_address.get(0).toString(),Toast.LENGTH_SHORT).show();
                    new find_route().execute();


                }

            }

        });**/


    }


    private class find_route extends AsyncTask<Void, Void, Route> {

Exception ex=null;


        @Override
        protected Route doInBackground(Void... params) {
            Route mRoute = null;


if(destination_address.size()==0){
    Toast.makeText(routing_map.this,"0000",Toast.LENGTH_LONG);
}
else {
            for(int i=-1;i<destination_address.size();i++) {
                LocatorFindParameters findParams = null;
                if (i == -1) {
                    if(flag){
                        Point p=new Point(long_x,lat_y);
                        destination_points.add(p);
                        continue;
                    }else {
                        // Create Locator parameters from single line source address string
                        findParams = new LocatorFindParameters(sourec_address);
                    }

                }
                if (i != -1) { // Create Locator parameters from single line destination address string
                    findParams = new LocatorFindParameters(destination_address.get(i));
                }

                findParams.setLocation(map_view.getCenter(), ref);


                findParams.setMaxLocations(2);



                List<LocatorGeocodeResult> results = null;
                Locator locator = Locator.createOnlineLocator();
                try {
                    results = locator.find(findParams);
                } catch (Exception e) {

                }
                if (results.size() == 0) {
                    Toast.makeText(routing_map.this, getString(R.string.noResultsFound), Toast.LENGTH_LONG).show();
                } else {
                    // Use first result in the list
                    LocatorGeocodeResult geocodeResult = results.get(0);

                    // get return geometry from geocode result
                    destination_points.add(geocodeResult.getLocation());

                }

            }

            }


            // Routing the point




            try {
                UserCredentials userCredentials = new UserCredentials();
                userCredentials.setUserAccount("yaseer_farah", "y010195101700");
                userCredentials.setUserToken(Token, ClientID);
                String routeTaskURL = "http://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
                RouteTask routeTask = RouteTask.createOnlineRouteTask(routeTaskURL, userCredentials);

                RouteParameters routeParameters = routeTask.retrieveDefaultRouteTaskParameters();
                NAFeaturesAsFeature naFeatures = new NAFeaturesAsFeature();

                StopGraphic[] graphic_points=new StopGraphic[destination_points.size()];

                for(int i=0;i<destination_points.size();i++) {
                StopGraphic point = new StopGraphic(destination_points.get(i));

                graphic_points[i]=point;

                }


                naFeatures.setFeatures(graphic_points);

                routeParameters.setStops(naFeatures);
                routeParameters.setReturnDirections(true);
                if(is_fast) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat hour = new SimpleDateFormat("HH");
                    SimpleDateFormat minute = new SimpleDateFormat("mm");

                    String fullhour = hour.format(calendar.getTime());
                    String fullminute = minute.format(calendar.getTime());

                    fullhour = fullhour.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5").replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9");
                    fullminute = fullminute.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5").replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9");


                    routeParameters.setImpedanceAttributeName("TravelTime");
                    routeParameters.setStartTime(new Date(1990, 1, day(calendar), Integer.valueOf(fullhour), Integer.valueOf(fullminute)));
                }
                else {
                    routeParameters.setImpedanceAttributeName("Kilometers");

                }

                RouteResult mResults = routeTask.solve(routeParameters);



                List<Route> routes = mResults.getRoutes();


                mRoute = routes.get(0);


            } catch (Exception e) {


              ex=e;

                return null;
            }
            return mRoute;
        }

        @Override
        protected void onPostExecute(Route route) {
            if(ex!=null) {
                Log.w("PlaceSearch",ex.toString());
                Toast.makeText(routing_map.this, ex.toString(), Toast.LENGTH_LONG).show();
            }
            else {

                if (route != null) {


                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.BLUE, 5, SimpleLineSymbol.STYLE.SOLID);

                    final GeometryEngine engine = new GeometryEngine();

                    Polyline poly = (Polyline) engine.project(route.getRouteGraphic().getGeometry(), SpatialReference.create(4326), ref);

                    Graphic routeGraphic = new Graphic(poly, lineSymbol);

                    mGraphicsLayer.addGraphic(routeGraphic);

                    map_view.setExtent(routeGraphic.getGeometry(), 15);

                    int i = 0;
                    final List<RouteDirection> directions = route.getRoutingDirections();
                    String[] directionsArray = new String[directions.size()];
                    for (RouteDirection dm : directions) {
                        directionsArray[i++] = dm.getText();
                    }
                    mDrawerList.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                            R.layout.drawer_layout_text, directionsArray));



                    for(int b=0;b<destination_points.size();b++){
                        CompositeSymbol sympols=new CompositeSymbol();
                        SimpleMarkerSymbol s=new SimpleMarkerSymbol(Color.RED,12, SimpleMarkerSymbol.STYLE.CIRCLE);
                        TextSymbol t=new TextSymbol(12, String.valueOf(b+1), Color.BLACK,
                                TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
                        sympols.add(s);
                        sympols.add(t);
                        Graphic symbolGraphic = new Graphic( engine.project(destination_points.get(b), SpatialReference.create(4326), ref),sympols);
                        mGraphicsLayer.addGraphic(symbolGraphic);

                    }
/*
                Point startPoint = new Point(31.211401, 30.022244);
                Point stop1Point = new Point(31.22861210997098, 29.961444921036428);
                Point stopPoint = new Point(31.315241, 29.866922);



                sympol( engine.project(startPoint, SpatialReference.create(4326), ref),getResources().getDrawable(R.drawable.s_flag3));
                sympol( engine.project(stop1Point, SpatialReference.create(4326), ref),getResources().getDrawable(R.drawable.c_flag));
                sympol( engine.project(stopPoint, SpatialReference.create(4326), ref),getResources().getDrawable(R.drawable.d_flag));

/*
                Graphic symbolGraphic = new Graphic( engine.project(startPoint, SpatialReference.create(4326), ref), new SimpleMarkerSymbol(Color.GREEN,22, SimpleMarkerSymbol.STYLE.DIAMOND));
                mGraphicsLayer.addGraphic(symbolGraphic);
                Graphic symbolGraphic1 = new Graphic( engine.project(stop1Point, SpatialReference.create(4326), ref), new SimpleMarkerSymbol(Color.MAGENTA,22, SimpleMarkerSymbol.STYLE.DIAMOND));
                mGraphicsLayer.addGraphic(symbolGraphic1);
                Graphic symbolGraphic2 = new Graphic( engine.project(stopPoint, SpatialReference.create(4326), ref), new SimpleMarkerSymbol(Color.RED,22, SimpleMarkerSymbol.STYLE.DIAMOND));
                mGraphicsLayer.addGraphic(symbolGraphic2);
*/


                    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mGraphicsLayer_road.getNumberOfGraphics() > 0) {
                                mGraphicsLayer_road.removeAll();
                            }
                            mDrawerLayout.closeDrawers();
                            RouteDirection dm = directions.get(position);
                            Geometry gm = dm.getGeometry();
                            Polyline poly = (Polyline) engine.project(gm, SpatialReference.create(4326), ref);

                            map_view.setExtent(poly, 10);
                            SimpleLineSymbol selectedRouteSymbol = new SimpleLineSymbol(Color.GREEN, 5, SimpleLineSymbol.STYLE.SOLID);
                            Graphic selectedRouteGraphic = new Graphic(poly,
                                    selectedRouteSymbol);
                            mGraphicsLayer_road.addGraphic(selectedRouteGraphic);
                        }
                    });


                }

            }

            String time=String.format("%.2f",route.getTotalMinutes());
            String dis=String.format("%.2f",route.getTotalKilometers());
            arrive.setText("Arrival time ="+time+"Minutes");


        }

        private void sympol(Geometry point,Drawable bitmap){
            PictureMarkerSymbol marker=new PictureMarkerSymbol(bitmap);
            marker.setOffsetY(0);
            Graphic symbolGraphic = new Graphic( point,marker);
            mGraphicsLayer.addGraphic(symbolGraphic);
        }


        private int day(Calendar cal){
            int x=cal.get(Calendar.DAY_OF_WEEK);
            int day_number=0;
            switch (x){
                case 1:
                    day_number=7;
                    break;
                case 2:
                    day_number=1;
                    break;
                case 3:
                    day_number=2;
                    break;
                case 4:
                    day_number=3;
                    break;
                case 5:
                    day_number=4;
                    break;
                case 6:
                    day_number=5;
                    break;
                case 7:
                    day_number=6;
                    break;

            }

            return day_number;

        }




    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}
