package com.sfmap.api.location.demo;

import android.app.Activity;
import android.location.GpsStatus;
import android.util.Log;

public class TestNmea extends Activity {

    GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
        public void onNmeaReceived(long timestamp, String nmea) {
            //check nmea's checksum
            Log.d("GPS-NMEA", nmea);

        }
    };
}
