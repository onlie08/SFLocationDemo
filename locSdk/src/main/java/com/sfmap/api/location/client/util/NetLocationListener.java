package com.sfmap.api.location.client.util;

import com.sfmap.api.location.SfMapLocation;

public interface NetLocationListener {
    void onNetLocationChanged(SfMapLocation location);
    void onError(int errorCode);
}
