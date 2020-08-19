package com.sfmap.api.location.client.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.sfmap.api.location.BuildConfig;
import com.sfmap.api.location.SfMapLocation;

import static android.content.ContentValues.TAG;
@SuppressLint("LogNotTimber")
public class OfflineCache {
    private static OfflineCache sInstance;
    private final Application mApplication;
    private RandomAccessFile mCacheFile;


    private OfflineCache(Context context) {
        mApplication = (Application) context.getApplicationContext();

        ensureCacheFile();

    }

    private void ensureCacheFile() {
        if(mCacheFile != null) {
            return;
        }
        String externalState = Environment.getExternalStorageState();
        boolean externalMounted = Environment.MEDIA_MOUNTED.equals(externalState);
        boolean externalPermission = externalPermissionGranted();
        File cacheDir;
        if (externalMounted && externalPermission) {
            File externalDir = Environment.getExternalStorageDirectory();
            cacheDir = new File(externalDir, "sflocation");
        } else {
            cacheDir = new File(mApplication.getCacheDir(), "sflocation");
        }

        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                try {
                    Log.w(TAG, String.format("Create location cache dir(%s) failed.", cacheDir.getCanonicalPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mCacheFile = null;
                return;
            }
        }

        try {
            mCacheFile = new RandomAccessFile(new File(cacheDir, "locCache.dat"), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean externalPermissionGranted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mApplication.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }


    /**
     * 类单例方法
     *
     * @param context android context
     * @return 缓存类单例
     */
    public static OfflineCache singleton(Context context) {
        if (sInstance != null) {
            return sInstance;
        }

        synchronized (OfflineCache.class) {
            if (sInstance == null) {
                sInstance = new OfflineCache(context);
            }
            return sInstance;
        }
    }

    public synchronized boolean  saveCache(SfMapLocation location) {
        if(location.isFromCache() || !location.isSuccessful()) {
            return false;
        }

        if(location.getLatitude() == 0 && location.getLongitude() == 0) {
            return false;
        }
        ensureCacheFile();
        if(mCacheFile != null) {
            try {
                mCacheFile.seek(0);
                byte[] parcelable = ParcelableUtils.marshall(location);
                mCacheFile.write(parcelable);
                return true;
            } catch (IOException e) {
                try {
                    mCacheFile.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                mCacheFile = null;
            }
        }
        return false;
    }

    public synchronized SfMapLocation getCachedLocation() {
        ensureCacheFile();
        if(mCacheFile != null) {
            try {
                mCacheFile.seek(0);
                int available = (int) mCacheFile.length();
                if(available > 0) {
                    byte[] parcelable = new byte[available];
                    mCacheFile.read(parcelable);
                    SfMapLocation location = ParcelableUtils.unmarshall(parcelable, SfMapLocation.CREATOR);
                    if(location.getLongitude() == 0 && location.getLatitude() == 0) {
                        return null;
                    }
                    location.setFromCache(true);
                    return location;
                }
            } catch (Exception e) {
                try {
                    mCacheFile.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                mCacheFile = null;
            }
        }
        return null;
    }

    public void clear() {
        try {
            ensureCacheFile();
            mCacheFile.setLength(0);
        } catch (IOException e) {
            if( BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}


