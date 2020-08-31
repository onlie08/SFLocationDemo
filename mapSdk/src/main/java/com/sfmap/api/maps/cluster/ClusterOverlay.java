package com.sfmap.api.maps.cluster;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.sfmap.api.mapcore.util.Util;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapUtils;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/*
 * Created by 01377555 on 2018/10/22.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 */
public class ClusterOverlay {
    private static final String TAG = "ClusterOverlay";

    private MapController mAMap;
    private Context mContext;
    private List<Cluster> mClusters;
    private int mClusterSize;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private double mClusterDistance;
    private float mPXInMeters;
    private LruCache<Integer, BitmapDescriptor> mLruCache;
    private final List<ClusterItem> mClusterItems = new ArrayList<>();
    private final List<Marker> mAddMarkers = new ArrayList<>();
    private final HandlerThread mMarkerHandlerThread = new HandlerThread("SFMapAddClusterMarker");
    private final HandlerThread mAssignClusterThread = new HandlerThread("SFMapAssignCluster");
    private Handler mMarkerHandler;
    private Handler mSignClusterHandler;
    private volatile boolean mIsDestroyed = false;
    private boolean mClusterVisibleOnly = true;
    private volatile boolean mCenterMapToClickedMarker = true;

    /**
     * 构造函数
     *
     * @param mapController 地图控制器
     * @param clusterSize 聚合范围的大小（指点像素单位距离内的点会聚合到一个点显示）
     * @param context Android context对象
     */
    public ClusterOverlay(MapController mapController, int clusterSize, Context context) {
        this(mapController, null, clusterSize, context);
    }

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param mapController 地图控制器
     * @param clusterItems 聚合元素
     * @param clusterSize 聚合范围的大小（指点像素单位距离内的点会聚合到一个点显示）
     * @param context Android context对象
     */
    public ClusterOverlay(MapController mapController, List<ClusterItem> clusterItems,
                          int clusterSize, Context context) {
//默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        mLruCache = new LruCache<Integer, BitmapDescriptor>(80) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };

        if (clusterItems != null) {
            mClusterItems.addAll(clusterItems);
        }
        mContext = context;
        mClusters = new ArrayList<>();
        this.mAMap = mapController;
        mClusterSize = clusterSize;
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        initThreadHandler();
        assignClusters();
    }

    /*
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /*
     * 添加一个聚合点
     *
     * @param item
     */
    public void addClusterItem(ClusterItem item) {
        if(item == null) {
            throw new NullPointerException("ClusterItem can not be null");
        }
        Message message = Message.obtain();
        message.what = SignClusterHandler.ADD_SINGLE_CLUSTER_ITEM;
        message.obj = item;
        mSignClusterHandler.sendMessage(message);
    }

    /**
     * 更新所有聚合点
     * @param items 新的聚合点对象列表
     */
    public void updateClusterItems(List<ClusterItem> items) {
        if(items.contains(null)) {
            throw new NullPointerException("Cluster item list contains null object");
        }
        mSignClusterHandler
                .obtainMessage(SignClusterHandler.UPDATE_ALL_CLUSTERS, items)
                .sendToTarget();
    }

    /**
     * 是否只聚合地图视图可见范围内的聚合点，默认为true;
     * 如果设置为false，无论视图范围，可能造成较多的计算任务和显示点
     * @param clusterVisibleOnly 是否只对视图范围的点进行聚合
     */
    public void setClusterVisibleOnly(boolean clusterVisibleOnly) {
        mClusterVisibleOnly = clusterVisibleOnly;
        assignClusters();
    }

    /**
     * 是否在响应Cluster Marker时，自动移动地图中心点到Marker位置，
     *
     * @param centerMapToClickedMarker 是否移动，默认true
     */
    public void setCenterMapToClickedMarker(boolean centerMapToClickedMarker) {
        mCenterMapToClickedMarker = centerMapToClickedMarker;
    }

    /**
     * 返回所有聚合对象列表（浅拷贝）
     * @return 浅拷贝的聚合对象列表
     */
    public List<Cluster> getClusters() {
        List<Cluster> clusters = new ArrayList<>(mClusters == null ? 0 : mClusters.size());
        if(mClusters != null) {
            clusters.addAll(mClusters);
        }
        return clusters;
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render 自定义渲染样式生产器
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    public void onDestroy() {
        mIsDestroyed = true;
        mSignClusterHandler.removeCallbacksAndMessages(null);
        mMarkerHandler.removeCallbacksAndMessages(null);
        mAssignClusterThread.quit();
        //防止同步修改，让Marker线程自己清理完数据之后退出
        mMarkerHandler.sendEmptyMessage(MarkerHandler.CLEAR_AND_QUIT);
    }

    //初始化Handler
    private void initThreadHandler() {
        mMarkerHandlerThread.start();
        mAssignClusterThread.start();
        mMarkerHandler = new MarkerHandler(mMarkerHandlerThread.getLooper());
        mSignClusterHandler = new SignClusterHandler(mAssignClusterThread.getLooper());
    }

    /*
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {

        ArrayList<Marker> removeMarkers = new ArrayList<>(mAddMarkers);
        for(Marker marker:removeMarkers){
            marker.remove();
        }
        mAddMarkers.clear();
        removeMarkers.clear();
        for (Cluster cluster : clusters) {
            addSingleClusterToMap(cluster);
        }
    }

    /*
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster) {
        MarkerOptions markerOptions = null;
        if(mClusterRender  != null) {
            markerOptions = mClusterRender.getClusterMarkerOptions(cluster);
        }

        if(markerOptions == null) {
            LatLng latlng = cluster.getCenterLatLng();
            markerOptions = new MarkerOptions();
            markerOptions
                    .anchor(0.5f, 0.5f)
                    .icon(getBitmapDes(cluster.getClusterCount()))
                    .position(latlng);
        }

        Marker marker = mAMap.addMarker(markerOptions);
        marker.setObject(cluster);
        cluster.setMarker(marker);
        mAddMarkers.add(marker);

    }

    /*
     * 计算聚合
     */
    private void calculateClusters() {
        mClusters.clear();
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        for (ClusterItem clusterItem : mClusterItems) {
            if (mIsDestroyed) {
                return;
            }

            if(clusterItem == null) {
                continue;
            }
            LatLng latlng = clusterItem.getPosition();

            boolean ignoreClusterItem = mClusterVisibleOnly && !visibleBounds.contains(latlng);
            if(!ignoreClusterItem) {
                Cluster cluster = getCluster(latlng, mClusters);
                if (cluster != null) {
                    cluster.addClusterItem(clusterItem);
                } else {
                    cluster = new Cluster(latlng);
                    mClusters.add(cluster);
                    cluster.addClusterItem(clusterItem);
                }
            }
        }

        //复制一份数据，规避同步
        List<Cluster> clusters = new ArrayList<>(mClusters);
        Message message = Message.obtain();
        message.what = MarkerHandler.ADD_CLUSTER_LIST;
        message.obj = clusters;
        if (mIsDestroyed) {
            return;
        }
        mMarkerHandler.sendMessage(message);
    }

    /*
     * func:刷新聚合点
     */
    public void updateClusters(){
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        assignClusters();
    }

    /*
     * 对点进行聚合
     */
    private void assignClusters() {
        mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
        mSignClusterHandler.sendEmptyMessage(SignClusterHandler.CALCULATE_CLUSTER);
    }

    /*
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     *
     * @param clusterItem
     */
    private void calculateSingleCluster(ClusterItem clusterItem) {
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng latlng = clusterItem.getPosition();
        if (!visibleBounds.contains(latlng) && mClusterVisibleOnly) {
            return;
        }
        Cluster cluster = getCluster(latlng, mClusters);
        if (cluster != null) {
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.UPDATE_SINGLE_CLUSTER;

            message.obj = cluster;
            mMarkerHandler.removeMessages(MarkerHandler.UPDATE_SINGLE_CLUSTER);
            mMarkerHandler.sendMessageDelayed(message, 5);


        } else {

            cluster = new Cluster(latlng);
            mClusters.add(cluster);
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.ADD_SINGLE_CLUSTER;
            message.obj = cluster;
            mMarkerHandler.sendMessage(message);

        }
    }

    /*
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param latLng
     * @return
     */
    private Cluster getCluster(LatLng latLng,List<Cluster>clusters) {
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            double distance = MapUtils.calculateLineDistance(latLng, clusterCenterPoint);
            if (distance < mClusterDistance) {
                return cluster;
            }
        }

        return null;
    }

    /*
     * 默认聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(int num) {
        BitmapDescriptor bitmapDescriptor = mLruCache.get(num);
        if (bitmapDescriptor == null) {
            TextView textView = new TextView(mContext);
            if (num > 1) {
                String tile = String.valueOf(num);
                textView.setText(tile);
            }
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            textView.setBackground(
                    new BitmapDrawable(
                            mContext.getResources(),
                            Util.fromAsset(mContext, "marker_color_red.png"
                            )
                    )
            );
            bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
            mLruCache.put(num, bitmapDescriptor);

        }
        return bitmapDescriptor;
    }

    /*
     * 更新已加入地图聚合点的样式
     */
    private void updateCluster(Cluster cluster) {

        Marker marker = cluster.getMarker();
        MarkerOptions newMarkerOptions = null;
        if(mClusterRender != null) {
            newMarkerOptions = mClusterRender.getClusterMarkerOptions(cluster);
        }

        if(newMarkerOptions == null) {
            marker.setIcon(getBitmapDes(cluster.getClusterCount()));
            marker.setPosition(cluster.getCenterLatLng());
        } else {
            marker.setTitle(newMarkerOptions.getTitle());
            marker.setPosition(newMarkerOptions.getPosition());
            marker.setIcon(newMarkerOptions.getIcon());
        }

    }

    /**
     * func:响应聚合点的点击事件
     * @param marker 聚合点marker
     */
    public void responseClusterClickEvent(Marker marker) {
        Cluster cluster = (Cluster) marker.getObject();
        if(mCenterMapToClickedMarker) {
            LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        }

        if (cluster != null && mClusterClickListener != null) {
            mClusterClickListener.onClick(marker, cluster.getClusterItems());
        }
    }

    /*
     * 处理market添加，更新等操作
     */
    class MarkerHandler extends Handler {

        static final int ADD_CLUSTER_LIST = 0;

        static final int ADD_SINGLE_CLUSTER = 1;

        static final int UPDATE_SINGLE_CLUSTER = 2;

        static final int CLEAR_AND_QUIT = 3;

        MarkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {

            switch (message.what) {
                case ADD_CLUSTER_LIST:
                    if(mIsDestroyed) {
                        return;
                    }
                    if (message.obj instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<Cluster> clusters = (List<Cluster>) message.obj;
                        addClusterToMap(clusters);
                    }
                    break;
                case ADD_SINGLE_CLUSTER:
                    if(mIsDestroyed) {
                        return;
                    }
                    Cluster cluster = (Cluster) message.obj;
                    addSingleClusterToMap(cluster);
                    break;
                case UPDATE_SINGLE_CLUSTER:
                    if(mIsDestroyed) {
                        return;
                    }
                    Cluster updateCluster = (Cluster) message.obj;
                    updateCluster(updateCluster);
                    break;
                case CLEAR_AND_QUIT:
                    for (Marker marker : mAddMarkers) {
                        marker.remove();
                    }
                    mAddMarkers.clear();
                    mLruCache.evictAll();
                    mMarkerHandlerThread.quit();
                    break;
            }
        }
    }

    /*
     * 处理聚合点算法线程
     */
    class SignClusterHandler extends Handler {
        static final int CALCULATE_CLUSTER = 0;
        static final int ADD_SINGLE_CLUSTER_ITEM = 1;
        static final int UPDATE_ALL_CLUSTERS = 2;

        SignClusterHandler(Looper looper) {
            super(looper);
        }
        @SuppressWarnings("unchecked")
        public void handleMessage(Message message) {
            if(mIsDestroyed) {
                Log.w(TAG, "Try to calculate cluster after destroy");
                return;
            }

            switch (message.what) {
                case CALCULATE_CLUSTER:
                    calculateClusters();
                    break;
                case ADD_SINGLE_CLUSTER_ITEM:
                    ClusterItem item = (ClusterItem) message.obj;
                    mClusterItems.add(item);
                    calculateSingleCluster(item);
                    break;
                case UPDATE_ALL_CLUSTERS:
                    mClusterItems.clear();
                    List<ClusterItem> newItems = (List<ClusterItem>) message.obj;
                    if(newItems != null) {
                        mClusterItems.addAll(newItems);
                    }
                    calculateClusters();
                    break;
            }
        }
    }
}