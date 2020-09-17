package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sfmap.api.maps.offlinemap.OfflineMapCity;
import com.sfmap.api.maps.offlinemap.OfflineMapStatus;

import java.io.File;

public class CityObject//g
        extends OfflineMapCity
        implements IUnZipListener, IDownloadListener, TaskItem {
    public CityStateImp defaultState = new DefaultState(OfflineMapStatus.CHECKUPDATES, this);
    public CityStateImp waitingState = new WaitingState(OfflineMapStatus.WAITING, this);
    public CityStateImp loadingState = new LoadingState(OfflineMapStatus.LOADING, this);
    public CityStateImp pauseState = new PauseState(OfflineMapStatus.PAUSE, this);
    public CityStateImp unzipState = new UnzipState(OfflineMapStatus.UNZIP, this);
    public CityStateImp completeState = new CompleteState(OfflineMapStatus.SUCCESS, this);
    public CityStateImp newVersionState = new NewVersionState(OfflineMapStatus.NEW_VERSION, this);
    public CityStateImp errorState = new ErrorState(OfflineMapStatus.ERROR, this);
    CityStateImp cityStateImp;
    Context context;
    private String k = null;
    private String l = "";

    public String a() {
        return this.l;
    }

    public String getMyUrl() {
        return getUrl();
    }

    public CityObject(Context context, OfflineMapCity offlineMapCity) {
        this(context, offlineMapCity.getState());
        setCity(offlineMapCity.getCity());
        setUrl(offlineMapCity.getUrl());
        setState(offlineMapCity.getState());
        setCompleteCode(offlineMapCity.getcompleteCode());
        setAdcode(offlineMapCity.getAdcode());
        setVersion(offlineMapCity.getVersion());
        setSize(offlineMapCity.getSize());
        setMd5(offlineMapCity.getMd5());
        initPath();
    }

    public CityObject(Context paramContext, int paramInt) {
        this.context = paramContext;
        a(paramInt);
    }

    public void a(int paramInt) {
        switch (paramInt) {
            case OfflineMapStatus.ERROR:
                this.cityStateImp = this.errorState;
                break;
            case OfflineMapStatus.LOADING:
                this.cityStateImp = this.loadingState;
                break;
            case OfflineMapStatus.UNZIP:
                this.cityStateImp = this.unzipState;
                break;
            case OfflineMapStatus.WAITING:
                this.cityStateImp = this.waitingState;
                break;
            case OfflineMapStatus.PAUSE:
                this.cityStateImp = this.pauseState;
                break;
            case OfflineMapStatus.SUCCESS:
                this.cityStateImp = this.completeState;
                break;
            case OfflineMapStatus.CHECKUPDATES:
                this.cityStateImp = this.defaultState;
                break;
            case OfflineMapStatus.NEW_VERSION:
                this.cityStateImp = this.newVersionState;
                break;
            default:
                if (paramInt < 0) {
                    this.cityStateImp = this.errorState;
                } else {
                    Utility.b("this kind stateId is not found !! " + paramInt);
                }
                break;
        }
    }

    public void setCityState(CityStateImp paramal) {
        this.cityStateImp = paramal;

        setState(paramal.getState());
    }

    public CityStateImp getCityStateImp() {
        return this.cityStateImp;
    }

    public void d() {
        OfflineDownloadManager.getInstance(this.context).b(this); //上一句翻译
    }

    public void removeTask() {
        //h.a(this.j).d(this);
        OfflineDownloadManager.getInstance(this.context).removeTask(this);//上一句翻译

        d();
    }

    public void pauseDownloadTask() {
        Utility.log("CityOperation current State==>" + getCityStateImp().getState());
        if (this.cityStateImp.equals(this.pauseState)) {
            this.cityStateImp.continueDownload();
        } else if (this.cityStateImp.equals(this.loadingState)) {
            this.cityStateImp.pause();
        } else if ((this.cityStateImp.equals(this.newVersionState)) || (this.cityStateImp.equals(this.errorState))) {
            this.cityStateImp.start();
        } else {
            getCityStateImp().c();
        }
    }

    public void g() {
        this.cityStateImp.fail();
    }

    public void h() {
        this.cityStateImp.delete();
    }

    public void i() {
        if (!this.cityStateImp.equals(this.completeState)) {
            Utility.b("state must be COMPLETE_STATE when CheckUpdate ~ hasNewVersion");
        }
        this.cityStateImp.hasNew();
    }

    public void download() {
        //h.a(this.j).a(this);
        OfflineDownloadManager.getInstance(this.context).addDownloadTask(this);//上一句翻译
    }

    public void k() {
        //h.a(this.j).c(this);
        OfflineDownloadManager.getInstance(this.context).stopDownloadCity(this);//上一句翻译
    }

    public void startDownload() {
        this.m = 0L;
        setCompleteCode(0);
        if (!this.cityStateImp.equals(this.waitingState)) {
            Log.e("state", "state must be waiting when download onStart");
        }
        this.cityStateImp.start();
    }

    public void onProgress(long paramLong1, long paramLong2) {
        long l1 = paramLong2 * 100L / paramLong1;

        Utility.b("onProgress " + (int) l1);
        if ((int) l1 > getcompleteCode()) {
            setCompleteCode((int) l1);
            d();
        }
    }

    public void downloadFinish() {
        if (!this.cityStateImp.equals(this.loadingState)) {
            Log.e("state", "state must be Loading when download onFinish");
        }

        this.cityStateImp.complete();
    }

    public void onError(ExceptionType type) {
        if ((!this.cityStateImp.equals(this.loadingState)) && (!this.cityStateImp.equals(this.waitingState))) {
            Utility.b("state must be loading or waiting  when download onError");
            return;
        }
        if (type == ExceptionType.md5_exception) {
            errorState = new ErrorState(OfflineMapStatus.EXCEPTION_MD5, this);
        }
        this.cityStateImp.fail();
    }

    public void onStopDownload() {
        removeTask();
    }

    public void onUnZipStart() {
        this.m = 0L;

        setCompleteCode(0);
        if (!this.cityStateImp.equals(this.unzipState)) {
            Utility.b("state must be UNZIP_STATE when download onUnZipStart");
        }
        this.cityStateImp.start();
    }

    public void p() {
        if (!this.cityStateImp.equals(this.unzipState)) {
            Utility.b("state must be UNZIP_STATE when download onUnZipStart");
        }
        this.cityStateImp.fail();
    }

    /**
     * 解压缩过程中完成百分比
     *
     * @param paramLong
     */
    public void onUnzipSchedule(long paramLong) {
        long l1 = System.currentTimeMillis();
        if (l1 - this.m > 500L) {
            if ((int) paramLong > getcompleteCode()) {
                setCompleteCode((int) paramLong);
                d();
            }
            Utility.b("onUnzipSchedule " + paramLong);
            this.m = l1;
        }
    }

    public void onUnzipFinish(String paramString) {
        if (!this.cityStateImp.equals(this.unzipState)) {
            Utility.b("state must be UNZIP_STATE when download onUnzipFinish");
        }
        this.l = paramString;

        String str1 = s();

        String str2 = t();
        if ((TextUtils.isEmpty(str1)) ||
                (TextUtils.isEmpty(str2))) {
            p();
            return;
        }
        File localFile1 = new File(str2 + "/");
        File localFile2 = new File(Util.getMapRoot(this.context) + "data/vmap/");

        File localFile3 = new File(Util.getMapRoot(this.context));
        if (!localFile3.exists()) {
            localFile3.mkdir();
        }
        if (!localFile2.exists()) {
            localFile2.mkdir();
        }
        a(localFile1, localFile2, str1);
    }

    public void q() {
        removeTask();
    }

    protected void initPath() {
        this.k = (OfflineDownloadManager.dataPath + getAdcode() + ".zip" + ".tmp");
    }

    public String s() {
        if (TextUtils.isEmpty(this.k)) {
            return null;
        }
        return this.k.substring(0, this.k.lastIndexOf("."));
    }

    public String t() {
        if (TextUtils.isEmpty(this.k)) {
            return null;
        }
        String str = s();
        return str.substring(0, str.lastIndexOf('.'));
    }

    private long m = 0L;

    private void a(final File file, File file1, final String paramString) {
        FileCopy localab = new FileCopy();
        long l1 = Utility.a(file);
        localab.copyFile(file, file1, -1L, l1, new FileCopy.a() {
            public void a(String paramAnonymousString1, String paramAnonymousString2, float paramAnonymousFloat) {
                int i = CityObject.this.getcompleteCode();

                int j = (int) (60.0D + paramAnonymousFloat * 0.39D);
                //if ((j - i > 0) && (System.currentTimeMillis() - g.a(g.this) > 1000L))
                if ((j - i > 0) && (System.currentTimeMillis() - m > 1000L)) //上一句翻译
                {
                    CityObject.this.setCompleteCode(j);
                    //g.a(g.this, System.currentTimeMillis());
                    m = System.currentTimeMillis(); //上一句翻译
                }
            }

            public void a(String paramAnonymousString1, String paramAnonymousString2) {
            }

            public void b(String paramAnonymousString1, String paramAnonymousString2) {
                try {
                    new File(paramString).delete();

                    Utility.deleteFiles(file);

                    CityObject.this.setCompleteCode(100);

                    CityObject.this.cityStateImp.complete();
                } catch (Exception localException) {
                    CityObject.this.cityStateImp.fail();
                }
            }

            public void a(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt) {
                CityObject.this.cityStateImp.fail();
            }
        });
    }

    public boolean u() {
        if (Utility.a() < getSize() * 2.5D - getcompleteCode() * getSize()) {
            return false;
        }
        return false;
    }

    public UpdateItem v() {
        setState(this.cityStateImp.getState());
        UpdateItem localr = new UpdateItem(this, this.context);
        localr.setVMapFileName(a());
        return localr;
    }

    public void a(UpdateItem item) {
        a(item.state);
        setCity(item.getCity());
        setSize(item.getSize());
        setVersion(item.getVersion());
        setCompleteCode(item.getCompleteCode());
        setAdcode(item.getAdcode());
        setUrl(item.getUrl());
    }
}
