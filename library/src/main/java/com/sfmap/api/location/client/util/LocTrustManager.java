package com.sfmap.api.location.client.util;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import android.annotation.SuppressLint;
import android.util.Log;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LocTrustManager implements X509TrustManager {
    private final X509TrustManager defaultTrustManager;
    private static final String TAG = "LocTrustManager";

    LocTrustManager() throws KeyStoreException, NoSuchAlgorithmException {
        this.defaultTrustManager = createTrustManager(null);//create default TrustManager
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkCertificateChain(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkCertificateChain(chain, authType);
    }

    @SuppressLint("LogNotTimber")
    private void checkCertificateChain(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            Log.e(TAG, "Certificate chain list:");
            for(X509Certificate crt : chain) {
                Log.e(TAG, crt.getSubjectX500Principal().getName());
            }
            Log.e(TAG, "Certificate exception failed", e);
            throw e;
        }
    }


    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private X509TrustManager createTrustManager(KeyStore store) throws NoSuchAlgorithmException, KeyStoreException {
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(store);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        return (X509TrustManager) trustManagers[0];
    }
}
