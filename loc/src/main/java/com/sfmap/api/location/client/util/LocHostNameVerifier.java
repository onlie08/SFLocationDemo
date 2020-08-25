package com.sfmap.api.location.client.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

class LocHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return hostname.endsWith("sf-express.com") || hostname.endsWith("baidu.com");
    }
}
