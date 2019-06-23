package yaseerfarah22.com.follow_me3;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.CallbackListener;
import com.esri.core.portal.Portal;
import com.esri.core.portal.WebMap;

/**
 * Created by DELL on 6/19/2018.
 */

public class test extends AppCompatActivity {
    private static final String URL = "http://arcgis.com";
    String id ="c8f704b7339b4902ac655ad592f5e862";
    Portal mPortal;
    final String ClientID = "HNso5DbSmS3392fS";
    final String Token = "Q__EszKDMJ8duzbIK2XN-W8VOLSeIdAZAHzz0qxdkZkO2uQ0jxL61ZIt9G0BaHGlg6Y7KW8TS6r7uT9jKnG3xkmSlNe4-gDwhLspe759KMbVgoe6ogqcxrpP0vOol2BU";

    MapView map_view;
    SpatialReference ref;
    GraphicsLayer mGraphicsLayer;
    GraphicsLayer mGraphicsLayer_road;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArcGISRuntime.setClientId("HNso5DbSmS3392fS");
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUserAccount("yaseer_farah", "y010195101700");
        userCredentials.setUserToken(Token, ClientID);
        mPortal = new Portal(URL, userCredentials);
        // Create a new instance of WebMap
        WebMap.newInstance(id, mPortal, new CallbackListener<WebMap>() {

            @Override
            public void onError(Throwable e) {
                Log.w("Place_Error", e);
                Toast.makeText(test.this,e.toString(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCallback(final WebMap webmap) {

                // The WebMap has been created - switch to UI thread to create MapView
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Create a MapView from the WebMap
                        if (webmap != null) {
                            map_view = new MapView(test.this, webmap, null, null);

                            map_view.setOnStatusChangedListener(new OnStatusChangedListener() {

                                private static final long serialVersionUID = 1L;

                                @Override
                                public void onStatusChanged(Object source, STATUS status) {
                                    switch (status) {
                                        case INITIALIZED:
                                            // MapView initialization complete so dismiss the progress dialog
                                            ref = map_view.getSpatialReference();
                                            mGraphicsLayer = new GraphicsLayer();
                                            mGraphicsLayer_road=new GraphicsLayer();
                                            map_view.addLayer(mGraphicsLayer);
                                            map_view.addLayer(mGraphicsLayer_road);
                                           // new find_route().execute();

                                            break;
                                        case INITIALIZATION_FAILED:
                                            Toast.makeText(test.this, "i_e",
                                                    Toast.LENGTH_LONG).show();
                                            break;
                                        case LAYER_LOADED:
                                        case LAYER_LOADING_FAILED:
                                            break;
                                    }
                                }
                            });

                            // Display the MapView
                            setContentView(map_view);
                        }

                    }
                });

            }
        });








    }







}
