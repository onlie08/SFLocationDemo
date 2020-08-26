package com.sfmap.api.location.demo.controllor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sfmap.api.location.demo.constants.KeyConst;


/**
 *Gool
 */
public class RestartServiceReceiver extends BroadcastReceiver {


        public RestartServiceReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(KeyConst.GPS_GET_SERVICE_RESTART)){
                Intent i=new Intent();
                i.setClass(context, RealGPSGetService.class);
                context.startService(i);
            }
        }

    }