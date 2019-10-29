package com.sfmap.api.location.client.util;

public interface NetLocator {
    void setNetLocationListener(NetLocationListener listener);
    void stopLocation();
    void startLocation(long intervalMs);
    void setIsOnce(boolean onceLocation);
    void setApiKey(String apiKey);

    void destroy();
    void setNeedAddress(boolean needAddress);

    void setUseGcj02(boolean useGcj02);
}
