package com.sfmap.api.maps.offlinemap;

import android.content.Context;
import android.os.Handler;

import com.sfmap.api.mapcore.util.CityObject;
import com.sfmap.api.mapcore.util.OfflineDownloadManager;
import com.sfmap.api.mapcore.util.OfflineMapDownloadList;
import com.sfmap.api.mapcore.util.SDKLogHandler;
import com.sfmap.api.mapcore.util.Util;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapException;

import java.util.ArrayList;

/**
 * 离线地图下载管理类，支持3D矢量地图下载。
 */
public final class OfflineMapManager {
    /**
     * 上下文。
     */
    private Context context;
    /**
     * 地图对象。
     */
    private MapController map;
    /**
     * 离线地图下载监听。
     */
    private OfflineMapDownloadListener listener;
    private Handler handler1 = new Handler();
    private Handler handler = new Handler();
    OfflineMapDownloadList offlineMapDownloadList;
    OfflineDownloadManager downloadManager;

    /**
     * 根据给定的参数来构造OfflineMapManager对象。
     *
     * @param context  - 上下文。
     * @param listener - 下载事件监听。
     */
    public OfflineMapManager(Context context, OfflineMapDownloadListener listener) {
        this.listener = listener;
        init(context);
    }

    /**
     * 根据给定的参数来构造OfflineMapManager对象。
     *
     * @param context  - 上下文。
     * @param listener - 下载事件监听。
     * @param map      - 当离线下载和地图在同一个页面，或者离线地图是后台下载时需要传入这个对象 可以保证缓存和离线数据不冲突，而且可以使得离线地图下载可直接使用。
     */
    public OfflineMapManager(Context context, OfflineMapDownloadListener listener, MapController map) {
        this.listener = listener;
        this.map = map;
        init(context);
    }

    private void init(Context context) {
        this.context = context.getApplicationContext();

        this.downloadManager = OfflineDownloadManager.getInstance(context);

        this.offlineMapDownloadList = this.downloadManager.downloadList;

        this.downloadManager.setDownloadListener(new OfflineDownloadManager.DownloadListener() {

            public void updateState(final CityObject city) {
                if ((OfflineMapManager.this.listener != null) && (city != null)) {
                    OfflineMapManager.this.handler1.post(new Runnable() {
                        public void run() {
                            OfflineMapManager.this.listener.onDownload(city.getCityStateImp().getState(), city.getcompleteCode(), city.getCity());
                        }

                    });
                }

                if ((OfflineMapManager.this.map != null) && (city.getCityStateImp().a(city.completeState))) {
                    OfflineMapManager.this.map.setLoadOfflineData(false);
                    OfflineMapManager.this.map.setLoadOfflineData(true);
                }
            }

            public void postCheckUpdateRes(final CityObject cityObject) {
                if ((OfflineMapManager.this.listener != null) && (cityObject != null)) {
                    OfflineMapManager.this.handler1.post(new Runnable() {
                        public void run() {
                            if (cityObject.getCityStateImp().equals(cityObject.newVersionState))
                                OfflineMapManager.this.listener.onCheckUpdate(true, cityObject.getCity());
                            else
                                OfflineMapManager.this.listener.onCheckUpdate(false, cityObject.getCity());
                        }
                    });
                } else {
                    //更新异常
                    OfflineMapManager.this.handler1.post(new Runnable() {
                        public void run() {
                            OfflineMapManager.this.listener.onCheckUpdate(false, null);
                        }
                    });
                }
            }

            public void c(final CityObject city) {
                if ((OfflineMapManager.this.listener != null) && (city != null))
                    OfflineMapManager.this.handler1.post(new Runnable(/*paramg*/) {
                        public void run() {
                            if (city.getCityStateImp().equals(city.defaultState))
                                OfflineMapManager.this.listener.onRemove(true, city.getCity(), "");
                            else
                                OfflineMapManager.this.listener.onRemove(false, city.getCity(), "");
                        }
                    });
            }

            @Override
            public void reLoadCityList() {
                OfflineMapManager.this.offlineMapDownloadList = OfflineMapManager.this.downloadManager.downloadList;
                if (OfflineMapManager.this.listener != null)
                    OfflineMapManager.this.handler1.post(new Runnable(/*paramg*/) {
                        public void run() {
                            OfflineMapManager.this.listener.reloadCityList();
                        }
                    });
            }
        });
    }


    /**
     * 根据给定的城市名称下载该城市的离线地图包.异步方法，如果有注册OfflineMapDownloadListener监听，下载状态会回调onDownload方法
     *
     * @param cityName - 城市名称。
     * @throws MapException MapException异常。
     */
    public void downloadByCityName(String cityName)
            throws MapException {
        this.downloadManager.downloadByCityName(cityName);
    }

    /**
     * 异步方法，如果有注册OfflineMapDownloadListener监听，下载状态会回调onDownload方法。
     * <p>根据省份名称下载一个省份的离线地图包。省份离线地图包为改省下各个城市离线数据的合集</p>
     *
     * @param name - 省份名称。
     * @throws MapException MapException异常。
     */
    public void downloadByProvinceName(String name)
            throws MapException {
        try {
            a();
            OfflineMapProvince localOfflineMapProvince = null;
            localOfflineMapProvince = getItemByProvinceName(name);
            if (localOfflineMapProvince == null) {
                throw new MapException("无效的参数 - IllegalArgumentException");
            }
            ArrayList<OfflineMapCity> localArrayList = localOfflineMapProvince.getCityList();
            for (OfflineMapCity localOfflineMapCity : localArrayList) {
                final String cityName = localOfflineMapCity.getCity();
                this.handler.post(new Runnable() {
                    public void run() {
                        OfflineMapManager.this.downloadManager.downloadByCityName(cityName);
                    }
                });
            }
        } catch (Throwable localThrowable) {
            if ((localThrowable instanceof MapException)) {
                throw ((MapException) localThrowable);
            }
            SDKLogHandler.exception(localThrowable, "OfflineMapManager", "downloadByProvinceName");
        }
    }

    /**
     * 根据给定的城市名称删除该城市的离线地图包。
     * <p>可以删除通过OfflineManager下载的数据;在下载离线数据之后，再次滑动地图有些时候会加载一些缓存数据，这部分不属于离线数据，没有进行删除如果传入参数是省份，则会删除该省份以下的所有城市,异步方法，删除操作会比较耗时，删除状态会在 OfflineMapManager.OfflineMapDownloadListener.onRemove(boolean, String, String) (boolean, String)} 中进行回调返回</p>
     *
     * @param name - 城市或省份的名称
     */
    public void remove(String name) {
        if (this.downloadManager.b(name)) {
            this.downloadManager.c(name);
        } else {
            OfflineMapProvince localOfflineMapProvince = this.offlineMapDownloadList.getProvinceByName(name);
            if ((localOfflineMapProvince == null) || (localOfflineMapProvince.getCityList() == null)) {
                if (this.listener != null) {
                    this.listener.onRemove(false, name, "没有该城市");
                }
                return;
            }
            ArrayList<OfflineMapCity> localArrayList = localOfflineMapProvince.getCityList();
            for (OfflineMapCity localOfflineMapCity : localArrayList) {
                final String cityname = localOfflineMapCity.getCity();
                this.handler.post(new Runnable() {
                    public void run() {
                        OfflineMapManager.this.downloadManager.c(cityname);
                    }
                });
            }
        }
    }

    /**
     * 获取所有存在有离线地图的省的列表。同步方法。
     *
     * @return ArrayList。
     */
    public ArrayList<OfflineMapProvince> getOfflineMapProvinceList() {
        return this.offlineMapDownloadList.getOfflineMapProvinceList();
    }

    /**
     * 根据城市编码获取OfflineMapCity对象 会一次从已下载、下载中、本地以后的离线数据信息。同步方法。
     *
     * @param cityCode - 想查找城市编码。
     * @return 返回对应的OfflineMapCity对象。
     */
    private OfflineMapCity getItemByCityCode(String cityCode) {
        return this.offlineMapDownloadList.a(cityCode);
    }

    /**
     * 根据城市名称获取OfflneMapCity对象 会一次从已下载、下载中、本地以后的离线数据信息。同步方法。
     *
     * @param cityName - 想查找城市名称。
     * @return 对应的OfflneMapCity对象。
     */
    public OfflineMapCity getItemByCityName(String cityName) {
        return this.offlineMapDownloadList.b(cityName);
    }

    /**
     * 根据省份名称获取OfflineMapProvince对象 会一次从已下载、下载中、本地以后的离线数据信息。
     * 同步方法。
     *
     * @param provinceName - 省份名称。
     * @return 对应的OfflineMapProvince对象。
     */
    public OfflineMapProvince getItemByProvinceName(String provinceName) {
        return this.offlineMapDownloadList.getProvinceByName(provinceName);
    }

    /**
     * 获取所有存在有离线地图的城市列表。
     *
     * @return ArrayList。
     */
    public ArrayList<OfflineMapCity> getOfflineMapCityList() {
        return this.offlineMapDownloadList.b();
    }

    /**
     * 所有正在下载或等待下载离线地图的城市列表。
     *
     * @return ArrayList。
     */
    public ArrayList<OfflineMapCity> getDownloadingCityList() {
        return this.offlineMapDownloadList.getDownloadingCityList();
    }

    /**
     * 所有正在下载或等待下载离线地图的省份列表。
     *
     * @return List。
     */
    public ArrayList<OfflineMapProvince> getDownloadingProvinceList() {
        return this.offlineMapDownloadList.f();
    }

    /**
     * 返回已经下载完成离线地图的城市列表。
     *
     * @return ArrayList。
     */
    public ArrayList<OfflineMapCity> getDownloadOfflineMapCityList() {
        return this.offlineMapDownloadList.getDownloadOfflineMapCityList();
    }

    /**
     * 返回已经下载完成离线地图的省份列表。
     *
     * @return ArrayList。
     */
    public ArrayList<OfflineMapProvince> getDownloadOfflineMapProvinceList() {
        return this.offlineMapDownloadList.getDownloadOfflineMapProvinceList();
    }

    private void updateOfflineMapByName(String paramString1, String paramString2)
            throws MapException {
        this.downloadManager.updateOfflineMapByName(paramString1);
    }


    /**
     * 判断传入的城市（城市名称）是否有更新的离线数据包。
     *
     * @param cityName - 城市名称。
     * @throws MapException 地图操作异常信息。
     */
    public void updateOfflineCityByName(String cityName)
            throws MapException {
        updateOfflineMapByName(cityName, "cityname");
    }

    /**
     * 判断传入的省份名称是否有更新的离线数据包。
     *
     * @param name - 省份名称。
     * @throws MapException 地图操作异常信息。
     */
    public void updateOfflineMapProvinceByName(String name)
            throws MapException {
        updateOfflineMapByName(name, "cityname");
    }

    private void a()
            throws MapException {
        if (!Util.checkNet(this.context)) {
            throw new MapException("http连接失败 - ConnectionException");
        }
    }

    /**
     * 重新开始任务,开始下载队列中的第一个为等待中的任务。
     */
    public void restart() {
    }

    /**
     * 停止离线地图下载。暂停所有下载城市或省份，包括队列中的。
     */
    public void stop() {
        this.downloadManager.stop();
    }

    /**
     * 暂停离线地图下载。
     * <p>暂停正在下载城市或省份，不包括队列中。每次开启下载任务，都会存放在队列中，且只能同时下载一个城市或省份的离线数据。</p>
     */
    public void pause() {
        this.downloadManager.pause();
    }

    /**
     * 销毁offlineManager中的资源。
     */
    public void destroy() {
        this.downloadManager.destroy();

        b();

        this.map = null;

        this.handler1.removeCallbacksAndMessages(null);
        this.handler1 = null;

        this.handler.removeCallbacksAndMessages(null);
        this.handler = null;
    }

    private void b() {
        this.listener = null;
    }

    /**
     * 离线地图的下载监听接口。
     */
    public static abstract interface OfflineMapDownloadListener {
        /**
         * 下载状态回调，在调用downloadByCityName 等下载方法的时候会启动。
         *
         * @param status       - 参照 OfflineMapStatus{@link OfflineMapStatus}。
         * @param completeCode - 下载进度，下载完成之后为解压进度。
         * @param name         - - 当前所下载的城市的名字。
         */
        public abstract void onDownload(int status, int completeCode, String name);

        /**
         * 当调用updateOfflineMapCity 等检查更新函数的时候会被调用。
         *
         * @param hasNew - true表示有更新，说明官方有新版或者本地未下载。
         * @param name   - 被检测更新的城市的名字。
         */
        public abstract void onCheckUpdate(boolean hasNew, String name);

        /**
         * 当调用OfflineMapManager.remove(String)方法时，如果有设置监听，会回调此方法。当删除省份时，该方法会被调用多次，返回省份内城市删除情况。
         *
         * @param success  - ture：删除成功。
         * @param name     - 所删除的城市的名字。
         * @param describe - 删除描述，如 删除成功 "本地无数据"。
         */
        public abstract void onRemove(boolean success, String name, String describe);

        /**
         * 基础地图数据升级前，通知用户更新城市列表。
         */
        public abstract void reloadCityList();
    }
}
