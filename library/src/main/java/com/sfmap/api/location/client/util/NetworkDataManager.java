package com.sfmap.api.location.client.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import com.sfmap.api.location.BuildConfig;
import com.sfmap.api.location.client.bean.CellInfoBean;
import com.sfmap.api.location.client.bean.CellTowerBean;
import com.sfmap.api.location.client.bean.RequestBean;
import com.sfmap.api.location.client.bean.WifiDeviceBean;
import com.sfmap.api.location.client.bean.WifiInfoBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkDataManager {


    private static final int MAX_WIFI_LIST_COUNT = 20;
    private static final int DEFAULT_MCC_IN_MAINLAND_CHINA = 460;
    private final Application mApplication;
    private final Handler mBackgroundHandler;
    private volatile RequestBean mLastRequestBean;

    public interface OnDataAvailableCallback {
        void onDataAvailable();
    }

    private static final String TAG = "NetworkDataManager";
    private static NetworkDataManager sNetworkDataManager;

    private final WifiManager mWifiManager;
    private final TelephonyManager mTelephonyManager;
    private final List<CellInfoBean> mCellInfoList = new CopyOnWriteArrayList<>();
    private final List<WifiInfoBean> mWifiScanList = new CopyOnWriteArrayList<>();
    private final List<OnDataAvailableCallback> mOnDataAvailableCallbacks = new CopyOnWriteArrayList<>();

    private volatile boolean mWifiScanPending;

    private void onWifiScanListReceive() {
        if(mWifiScanPending) {
            mWifiScanPending = false;
            onWifiScanResult();
        }
    }

    public static NetworkDataManager singleton(Context context) {
        if(sNetworkDataManager != null) {
            return sNetworkDataManager;
        }

        synchronized (NetworkDataManager.class) {
            if(sNetworkDataManager == null) {
                try {
                    sNetworkDataManager = new NetworkDataManager(context);
                    //Permission failed;
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            return sNetworkDataManager;
        }
    }

    public void addOnDataAvailableCallback(OnDataAvailableCallback callback) {
        if(callback == null) {
            return;
        }
        synchronized (mOnDataAvailableCallbacks) {
            if(mOnDataAvailableCallbacks.contains(callback)) {
                return;
            }

            mOnDataAvailableCallbacks.add(callback);
        }
    }

    public void removeOnDataAvailableCallback(OnDataAvailableCallback callback) {
        if(callback == null) {
            return;
        }

        synchronized (mOnDataAvailableCallbacks) {
            mOnDataAvailableCallbacks.remove(callback);
        }
    }

    private class CellInfoChangeListener extends PhoneStateListener {
        @Override
        public void onCellLocationChanged(CellLocation cellLocation) {
            collectCellInfoListBackground();
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            collectCellInfoListBackground();
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
        }
    }

    private NetworkDataManager(Context context) {
        mApplication = (Application) context.getApplicationContext();
        mWifiManager = (WifiManager) mApplication.getSystemService(Context.WIFI_SERVICE);
        mTelephonyManager = (TelephonyManager)  mApplication.getSystemService(Context.TELEPHONY_SERVICE);
        HandlerThread mBackgroundThread = new HandlerThread("SfNetworkDataManager");
        mBackgroundThread.start();
        Looper backgroundLooper = mBackgroundThread.getLooper();
        mBackgroundHandler = new Handler(backgroundLooper);

        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTelephonyManager != null) {
                    mTelephonyManager.listen(new CellInfoChangeListener(),
                            PhoneStateListener.LISTEN_CELL_LOCATION |
                                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                    collectCellInfoListBackground();
                }
            }
        });


        BroadcastReceiver wifiScanResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    onWifiScanListReceive();
                }
            }
        };
        mApplication.registerReceiver(wifiScanResultReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void collectCellInfoListBackground() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if(updateCellInfo()) {
                    notifyDataAvailableCallbacks();
                }
            }
        });
    }


    private void onWifiScanResult() {
        try {
            List<ScanResult> results = mWifiManager.getScanResults();
            if(results != null) {
                List<WifiInfoBean> wifis = new ArrayList<>();
                for (ScanResult result : results) {
                    WifiInfoBean wifiBean = new WifiInfoBean();
                    wifiBean.setMac(result.BSSID);
                    wifiBean.setName(result.SSID);
                    wifiBean.setSignal((short) result.level);
                    wifis.add(wifiBean);
                }

                if(wifis.size() > MAX_WIFI_LIST_COUNT) {
                    wifis = filterWifiList(wifis);
                }
                mWifiScanList.clear();
                mWifiScanList.addAll(wifis);
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, String.format("Wifi list all count(%d), usable count(%d)", results.size(), mWifiScanList.size()));
                }
            }
            notifyDataAvailableCallbacks();
        }catch (Exception e){
            return;
        }
    }

    private List<WifiInfoBean> filterWifiList(List<WifiInfoBean> wifis) {
        Collections.sort(wifis, new Comparator<WifiInfoBean>() {
            @Override
            public int compare(WifiInfoBean o1, WifiInfoBean o2) {
                if (o1 == o2) {
                    return 0;
                }

                if (o1 == null) {
                    return -1;
                }

                if (o2 == null) {
                    return 1;
                }

                return o1.getSignal() - o2.getSignal();
            }
        });
        int size = wifis.size();
        int startIndex = wifis.size() - MAX_WIFI_LIST_COUNT;
        //排序之后选取后面部分
        return wifis.subList(startIndex, size);
    }

    private void notifyDataAvailableCallbacks() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnDataAvailableCallback callback : mOnDataAvailableCallbacks) {
                    callback.onDataAvailable();
                }
            }
        });
    }

    /**
     * 获得当前基站及wifi定位数据
     * @return 可用于后台定位接口的RequestBean,没有变化返回null
     */
    public RequestBean getRequestData() {
        RequestBean requestBean = new RequestBean();

        if(!mCellInfoList.isEmpty()) {
            List<CellTowerBean> cells = new ArrayList<>();
            for(CellInfoBean cellInfo : mCellInfoList) {
                CellTowerBean cellTower = new CellTowerBean();
                cellTower.setMcc(cellInfo.getMcc());
                cellTower.setMnc(cellInfo.getMnc());
                cellTower.setLac(cellInfo.getLac());
                cellTower.setCell_id(cellInfo.getCellid());
                cellTower.setSignalstrength(cellInfo.getSignal());
                cells.add(cellTower);
                requestBean.setCelltowers(cells);
            }
        }


        List<WifiDeviceBean> wifis = new ArrayList<>();
        for(WifiInfoBean wifiBean: mWifiScanList) {
            WifiDeviceBean wifi = new WifiDeviceBean();
            wifi.setMacaddress(wifiBean.getMac());
            wifi.setSingalstrength(wifiBean.getSignal());
            wifis.add(wifi);
        }

        requestBean.setWifilist(wifis);

        if(requestBean.isSimilar(mLastRequestBean) && BuildConfig.CACHE_ENABLED) {
            return null;
        }
        requestBean.setAppPackageName(mApplication.getPackageName());
        requestBean.setAppCerSha1(getCertificateSHA1());

        mLastRequestBean = requestBean;
        return requestBean;

    }

    public void resetLastRequestBean() {
        mLastRequestBean = null;
    }

    @SuppressLint("PackageManagerGetSignatures")
    //这里只会去app的签名证书指纹给到后台接口做验证，不会直接根据本地证书校验就授权
    public String getCertificateSHA1() {
        PackageManager pm = mApplication.getPackageManager();
        String packageName = mApplication.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(packageInfo == null) {
            return null;
        }
        Signature[] signatures = packageInfo.signatures;
        if(signatures == null || signatures.length == 0) {
            return null;
        }
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        if(cf == null) {
            return null;
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        if(c == null) {
            return null;
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException | CertificateEncodingException e1) {
            e1.printStackTrace();
        }
        return hexString;
    }

    private String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase(Locale.US));
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }


    private boolean updateCellInfo() {
        if(mTelephonyManager == null) {
            return false;
        }

        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                List<CellInfo> cells = mTelephonyManager.getAllCellInfo();
                if(cells == null) {
                    return false;
                }
                return handleCellInfoChanged(cells);
            }

            CellLocation cellLocation = mTelephonyManager.getCellLocation();
            if(cellLocation instanceof GsmCellLocation) {
                return handleGsmCellLocationChanged((GsmCellLocation) cellLocation);
            }

            if(cellLocation instanceof CdmaCellLocation) {
                return handleCdmaCellLocationChanged((CdmaCellLocation) cellLocation);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean handleCellInfoChanged(List<CellInfo> cellInfos) {
        mCellInfoList.clear();
        boolean success = false;
        for(CellInfo cellInfo : cellInfos) {
            CellInfoBean cellInfoBean = transformCellInfo(cellInfo);
            if(cellInfoBean != null && !mCellInfoList.contains(cellInfoBean)) {
                success = true;
                mCellInfoList.add(cellInfoBean);
            }
        }
        return success;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private CellInfoBean transformCellInfo(CellInfo cellInfo) {

        if(cellInfo instanceof CellInfoLte) {
            return transformCellInfoLte((CellInfoLte) cellInfo);
        }

        if(cellInfo instanceof CellInfoGsm) {
            return transformCellInfoGsm((CellInfoGsm) cellInfo);
        }

        if(cellInfo instanceof CellInfoCdma) {
            return transformCellInfoCdma((CellInfoCdma)cellInfo);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if(cellInfo instanceof CellInfoWcdma) {
                return transformCellInfoWcdma((CellInfoWcdma) cellInfo);
            }
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private CellInfoBean transformCellInfoWcdma(CellInfoWcdma cellInfo) {
        CellIdentityWcdma cellIdentity = cellInfo.getCellIdentity();
        if(cellIdentity == null || cellIdentity.getMnc() == Integer.MAX_VALUE) {
            return null;
        }

        CellInfoBean cellInfoBean = new CellInfoBean();
        CellSignalStrengthWcdma signalStrength = cellInfo.getCellSignalStrength();
        if(signalStrength != null) {
            cellInfoBean.setSignal((short) signalStrength.getDbm());
        }

        cellInfoBean.setMcc(cellIdentity.getMcc());
        cellInfoBean.setMnc(cellIdentity.getMnc());
        cellInfoBean.setLac(cellIdentity.getLac());
        cellInfoBean.setCellid(cellIdentity.getCid());
        return cellInfoBean;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private CellInfoBean transformCellInfoLte(CellInfoLte cellInfo) {
        CellIdentityLte cellIdentity = cellInfo.getCellIdentity();
        if(cellIdentity == null || cellIdentity.getMnc() == Integer.MAX_VALUE) {
            return null;
        }

        CellInfoBean cellInfoBean = new CellInfoBean();
        CellSignalStrengthLte signalStrength = cellInfo.getCellSignalStrength();
        if(signalStrength != null) {
            cellInfoBean.setSignal((short) signalStrength.getDbm());
        }

        cellInfoBean.setMcc(cellIdentity.getMcc());
        cellInfoBean.setMnc(cellIdentity.getMnc());
        cellInfoBean.setCellid(cellIdentity.getCi());
        cellInfoBean.setLac(cellIdentity.getTac());
        return  cellInfoBean;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private CellInfoBean transformCellInfoGsm(CellInfoGsm cellInfo) {
        CellIdentityGsm cellIdentity = cellInfo.getCellIdentity();
        if(cellIdentity == null  || cellIdentity.getMnc() == Integer.MAX_VALUE) {
            return null;
        }
        CellInfoBean cellInfoBean = new CellInfoBean();
        CellSignalStrengthGsm signalStrength = cellInfo.getCellSignalStrength();
        if(signalStrength != null) {
            cellInfoBean.setSignal((short) signalStrength.getDbm());
        }
        cellInfoBean.setMcc(cellIdentity.getMcc());
        cellInfoBean.setMnc(cellIdentity.getMnc());
        cellInfoBean.setCellid(cellIdentity.getCid());
        cellInfoBean.setLac(cellIdentity.getLac());

        return cellInfoBean;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private CellInfoBean transformCellInfoCdma(CellInfoCdma cellInfo) {
        CellIdentityCdma cellIdentity = cellInfo.getCellIdentity();

        if(cellIdentity == null) {
            return null;
        }
        CellInfoBean cellInfoBean = new CellInfoBean();
        CellSignalStrengthCdma signalStrength = cellInfo.getCellSignalStrength();
        if(signalStrength != null) {
            cellInfoBean.setSignal((short) signalStrength.getDbm());
        }
        cellInfoBean.setMcc((short) DEFAULT_MCC_IN_MAINLAND_CHINA);
        cellInfoBean.setCellid(cellIdentity.getBasestationId());
        cellInfoBean.setMnc(cellIdentity.getSystemId());
        cellInfoBean.setLac(cellIdentity.getNetworkId());

        return cellInfoBean;
    }

    private boolean handleGsmCellLocationChanged(GsmCellLocation location) {
        String[] saMccMnc = Utils.getMccMnc(mTelephonyManager);
        if(TextUtils.isEmpty(saMccMnc[0]) || TextUtils.isEmpty(saMccMnc[1])) {
            Log.e(TAG, "Mcc or mnc code is not available");
            //mcc or mnc not available
            return false;
        }
        CellInfoBean cellInfoBean = new CellInfoBean();
        cellInfoBean.setMcc(Short.valueOf(saMccMnc[0]));
        cellInfoBean.setMnc(Short.valueOf(saMccMnc[1]));
        cellInfoBean.setLac(location.getLac());
        cellInfoBean.setCellid(location.getCid());

        if(!mCellInfoList.contains(cellInfoBean)) {
            mCellInfoList.add(cellInfoBean);
        }
        return true;
    }

    private boolean handleCdmaCellLocationChanged(CdmaCellLocation cellLocation) {
        CellInfoBean cellInfoBean = new CellInfoBean();
        //只支持中国大陆的网络定位
        cellInfoBean.setMcc((short) DEFAULT_MCC_IN_MAINLAND_CHINA);
        cellInfoBean.setMnc(cellLocation.getSystemId());
        cellInfoBean.setCellid(cellLocation.getBaseStationId());
        cellInfoBean.setLac(cellLocation.getNetworkId());
        cellInfoBean.setLng(cellLocation.getBaseStationLongitude());
        cellInfoBean.setLat(cellLocation.getBaseStationLatitude());
        if(mCellInfoList.contains(cellInfoBean)) {
            mCellInfoList.add(cellInfoBean);
        }

        return true;
    }

    public synchronized boolean startNetworkScan() {
        collectCellInfoListBackground();

        if(mWifiScanPending) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "WifiScan pending, bail out network scan request");
            }
            return false;
        }

        boolean wifiStartSuccess = startWifiScan();

        //Wifi扫描失败时需要直接通过onWifiScanResult()
        //通知回调函数wifi扫描失败了
        if(!wifiStartSuccess) {
            mBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    onWifiScanResult();
                }
            });
        }

        if(wifiStartSuccess) {
            mWifiScanPending = true;
            return true;
        }

        return false;
    }

    private boolean startWifiScan() {
        try {

            boolean okToScan = mWifiManager.isWifiEnabled();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                okToScan = okToScan || mWifiManager.isScanAlwaysAvailable();
            }

            if(okToScan) {
                //startScan可能导致anr的问题
                mBackgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!mWifiManager.startScan()) {
                            notifyDataAvailableCallbacks();
                        }
                    }
                });
                return true;

            } else {
                //wifi 无法扫描,使用缓存
                if(!mWifiScanList.isEmpty()) {
                    Log.v(TAG, "Use previous wifi scan list");
                    notifyDataAvailableCallbacks();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }
}
