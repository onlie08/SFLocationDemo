package com.sfmap.api.maps.cluster;

import com.sfmap.api.maps.model.MarkerOptions;

/**
 * 聚合点Marker自定义渲染接口
 */
public interface ClusterRender {

    /**
     * 根据聚合点对象列表返回聚合点MarkerOption
     * @param cluster 聚合对象 里面包含了聚合点列表和经纬度信息
     * @return 用于显示的聚合点MarkerOption
     */
    MarkerOptions getClusterMarkerOptions(Cluster cluster);
}
