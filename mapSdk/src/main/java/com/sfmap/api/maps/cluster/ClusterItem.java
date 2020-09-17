package com.sfmap.api.maps.cluster;

import com.sfmap.api.maps.model.LatLng;

/**
 * Created by 01377555 on 2018/10/22.
 */
public interface ClusterItem {
    /**
     * 返回聚合元素的地理位置
     *
     * @return
     */
    LatLng getPosition();
}
