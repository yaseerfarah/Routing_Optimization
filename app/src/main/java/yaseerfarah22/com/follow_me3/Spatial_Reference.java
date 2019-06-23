package yaseerfarah22.com.follow_me3;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import java.io.Serializable;

/**
 * Created by DELL on 6/3/2018.
 */

public class Spatial_Reference implements Serializable {

    private SpatialReference ref_map;
    private Point map_point;
    Envelope mapExtent;

    public void  set_ref_map(SpatialReference ref){
        ref_map=ref;

    }

    public SpatialReference get_ref_map(){

        return ref_map;
    }


    public void  set_point_map(Point point){
        map_point=point;

    }

    public Point get_point_map(){

        return map_point;
    }

    public void  set_Extent_map(Envelope ex){
        mapExtent=ex;

    }

    public Envelope get_Extent_map(){

        return mapExtent;
    }

}
