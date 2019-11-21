package com.sfmap.api.location.client.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sfmap.api.location.BuildConfig;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.client.bean.AddressBean;
import com.sfmap.api.location.client.bean.DesUtil;
import com.sfmap.api.location.client.bean.LocationBean;
import com.sfmap.api.location.client.bean.RegeoApiResult;
import com.sfmap.api.location.client.bean.RegeoEncryptData;
import com.sfmap.api.location.client.bean.RegeoRequest;
import com.sfmap.api.location.client.bean.RegeoResultBean;
import com.sfmap.api.location.client.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

public class GpsRegeoHandler {

    private static final String TAG = "GpsRegeoHandler";
    private ResultCallback mResultCallback;
    private String mApiKey;
    private boolean traceEnable;
    private Context mContext;
    private final  Gson mGson = new Gson();
    public void setResultCallback(ResultCallback resultCallback) {
        mResultCallback = resultCallback;
    }

    public interface ResultCallback {
        void onRegeoResult(SfMapLocation location);
    }


    public void regeoLocation(SfMapLocation location) {
        new RegeoTask().execute(location);
    }

    public String getApiKey() {
        return mApiKey;
    }

    public void setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
    }

    public void setTraceEnable(Boolean traceEnable) {
        this.traceEnable = traceEnable;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private class RegeoTask extends AsyncTask<SfMapLocation, Void, SfMapLocation> {

        @Override
        protected SfMapLocation doInBackground(SfMapLocation... sfMapLocations) {
            if(sfMapLocations != null && sfMapLocations.length > 0) {
                SfMapLocation location = sfMapLocations[0];
                if(location != null) {
                    doRegeoTask(location);
                }
                return location;
            }
            return null;
        }

        private void doRegeoTask(SfMapLocation location) {
            try {
                RegeoRequest regeoRequest = new RegeoRequest();
                regeoRequest.latitude = location.getLatitude();
                regeoRequest.longitude = location.getLongitude();
                regeoRequest.appPackageName = mContext.getPackageName();
                regeoRequest.appCerSha1 = getCertificateSHA1();

                String jsonRequest = mGson.toJson(regeoRequest);
                if(traceEnable){
                    String log = "请求参数-GPS：jsonRequest:"+jsonRequest;
                    Utils.saveGpsInfo(log);
                }
                String encryptParams = new DesUtil().encrypt(jsonRequest);
                String urlBase = BuildConfig.LOCATION_BACKEND_HOST + BuildConfig.REGEO_API_PATH;
                String url = urlBase + "?param=" + encryptParams+"&ak=" + mApiKey;
                String jsonResult = Utils.get(url, null, "UTF-8");
                if(TextUtils.isEmpty(jsonResult)) {
                    return;
                }

                RegeoApiResult apiResult = mGson.fromJson(jsonResult, RegeoApiResult.class);
                RegeoEncryptData encryptResult = apiResult.regeoEncryptData;
                if(encryptResult == null) {
                    return;
                }

                if(!apiResult.isSuccess()) {
                    if(BuildConfig.DEBUG) {
                        Log.v(TAG, "Regeo return error result");
                        Log.v(TAG, encryptResult.message);
                    }
                    return;
                }


                String encryptMsg = encryptResult.message;
                if(TextUtils.isEmpty(encryptMsg)) {
                    return;
                }

                String decryptMsg = new DesUtil().decrypt(
                        encryptMsg, "UTF-8");
                if(TextUtils.isEmpty(decryptMsg)) {
                    return;
                }


                RegeoResultBean resultBean = mGson.fromJson(decryptMsg, RegeoResultBean.class);
                if(resultBean == null || resultBean.locationBean == null) {
                    return;
                }

                LocationBean locationBean = resultBean.locationBean;
                location.setAddress(locationBean.getAddressDescription());

                AddressBean addressBean = locationBean.getAddress();
                if(addressBean == null) {
                    return;
                }

                location.setRegion(addressBean.getRegion());
                location.setCountry(addressBean.getCountry());
                location.setCounty(addressBean.getCounty());
                location.setCity(addressBean.getCity());
                location.setStreet(addressBean.getStreet());
                location.setStreetNumber(addressBean.getStreet_number());
                location.setmAdcode(addressBean.getAdcode());

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPostExecute(SfMapLocation location) {
            if(mResultCallback != null) {
                mResultCallback.onRegeoResult(location);
            }
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    //这里只会去app的签名证书指纹给到后台接口做验证，不会直接根据本地证书校验就授权
    public String getCertificateSHA1() {
        PackageManager pm = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
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

}
