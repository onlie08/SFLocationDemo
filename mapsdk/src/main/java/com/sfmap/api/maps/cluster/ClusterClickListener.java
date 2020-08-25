package com.sfmap.api.maps.cluster;

import com.sfmap.api.maps.model.Marker;

import java.util.List;

/**
 * Created by 01377555 on 2018/10/22.
 */
public interface ClusterClickListener{
    /**
     * 点击聚合点的回调处理函数
     *
     * @param marker
     *            点击的聚合点
     * @param clusterItems
     *            聚合点所包含的元素
     */
    public void onClick(Marker marker, List<ClusterItem> clusterItems);
}

