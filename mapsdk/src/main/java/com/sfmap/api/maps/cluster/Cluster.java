package com.sfmap.api.maps.cluster;

import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合类，包含了聚合点列表，聚合经纬度
 * Created by 01377555 on 2018/10/22.
 */
public class Cluster {


    private LatLng mLatLng;
    private List<ClusterItem> mClusterItems;
    private Marker mMarker;


    Cluster( LatLng latLng) {

        mLatLng = latLng;
        mClusterItems = new ArrayList<ClusterItem>();
    }

    void addClusterItem(ClusterItem clusterItem) {
        mClusterItems.add(clusterItem);
    }

    public int getClusterCount() {
        return mClusterItems.size();
    }

    public LatLng getCenterLatLng() {
        return mLatLng;
    }

    void setMarker(Marker marker) {
        mMarker = marker;
    }

    Marker getMarker() {
        return mMarker;
    }

    public List<ClusterItem> getClusterItems() {
        return mClusterItems;
    }
}

