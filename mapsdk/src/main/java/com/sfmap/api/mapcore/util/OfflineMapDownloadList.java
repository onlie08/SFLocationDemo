package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.os.Handler;

import com.sfmap.api.maps.offlinemap.OfflineMapCity;
import com.sfmap.api.maps.offlinemap.OfflineMapProvince;
import com.sfmap.api.maps.offlinemap.OfflineMapStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OfflineMapDownloadList {
    public ArrayList<OfflineMapProvince> mapProvinceArrayList = new ArrayList<OfflineMapProvince>();
    private OfflineDBOperation offlineDBOperation;
    private Context context;
    private Handler handler;
    public OfflineMapDownloadList(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        this.offlineDBOperation = OfflineDBOperation.getInstance(context);
    }

    private void a(UpdateItem updateItem) {
        if ((this.offlineDBOperation != null) && (updateItem != null)) {
            this.offlineDBOperation.a(updateItem);
        }
    }

    private void d(String paramString) {
        if (this.offlineDBOperation != null) {
            this.offlineDBOperation.c(paramString);
        }
    }

    private boolean a(int paramInt1, int paramInt2) {
        return (paramInt2 != 1) || (paramInt1 <= 2) || (paramInt1 >= 98);
    }

    private boolean b(int paramInt) {
        if (paramInt == 4) {
            return true;
        }
        return false;
    }

    private boolean a(OfflineMapProvince paramOfflineMapProvince) {
        if (paramOfflineMapProvince == null) {
            return false;
        }
        ArrayList<OfflineMapCity> localArrayList = paramOfflineMapProvince.getCityList();
        for (OfflineMapCity localOfflineMapCity : localArrayList) {
            if (localOfflineMapCity.getState() != 4) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<OfflineMapProvince> getOfflineMapProvinceList() {
        ArrayList<OfflineMapProvince> localArrayList = new ArrayList<OfflineMapProvince>();
        for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
            localArrayList.add(localOfflineMapProvince);
        }
        return localArrayList;
    }

    public OfflineMapCity a(String paramString) {
        if ((paramString == null) || (paramString.equals(""))) {
            return null;
        }
        for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
            for (OfflineMapCity localOfflineMapCity : localOfflineMapProvince.getCityList()) {
                if (localOfflineMapCity.getCode().equals(paramString)) {
                    return localOfflineMapCity;
                }
            }
        }
        return null;
    }

    public OfflineMapCity b(String paramString) {
        if ((paramString == null) || (paramString.equals(""))) {
            return null;
        }
        for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
            for (OfflineMapCity localOfflineMapCity : localOfflineMapProvince.getCityList()) {
                String str = localOfflineMapCity.getCity();
                if (str.trim().equalsIgnoreCase(paramString.trim())) {
                    return localOfflineMapCity;
                }
            }
        }
        return null;
    }

    public OfflineMapProvince getProvinceByName(String provinceName) {
        if ((provinceName == null) || (provinceName.equals(""))) {
            return null;
        }
        for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
            String name = localOfflineMapProvince.getProvinceName();
            if (name.trim().equalsIgnoreCase(provinceName.trim())) {
                return localOfflineMapProvince;
            }
        }
        return null;
    }

    public ArrayList<OfflineMapCity> b() {
        ArrayList<OfflineMapCity> localArrayList = new ArrayList<OfflineMapCity>();
        for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
            for (OfflineMapCity localOfflineMapCity : localOfflineMapProvince.getCityList()) {
                localArrayList.add(localOfflineMapCity);
            }
        }
        return localArrayList;
    }

    /**
     * 更新内存城市列表信息：版本，URL
     *
     * @param citylist
     */
    public void updateAllCity(List<OfflineMapProvince> citylist) {
        OfflineMapProvince offlineMapProvince;
        //Iterator localIterator;
//    if (this.mapProvinceArrayList.size() > 0) {
//      for (int i = 0; i < this.mapProvinceArrayList.size(); i++)
//      {
//        offlineMapProvince = (OfflineMapProvince)this.mapProvinceArrayList.get(i);
//        OfflineMapProvince newOfflineMapProvince2 = (OfflineMapProvince)citylist.get(i);
//
//        updateProvinceInfo(offlineMapProvince, newOfflineMapProvince2);
//
//        ArrayList<OfflineMapCity> offlineMapCities = offlineMapProvince.getCityList();
//
//        ArrayList<OfflineMapCity> newOfflineMapCities = newOfflineMapProvince2.getCityList();
//        for (int j = 0; j < offlineMapCities.size(); j++)
//        {
//          OfflineMapCity offlineMapCity = (OfflineMapCity)offlineMapCities.get(j);
//          OfflineMapCity newOfflineMapCity = (OfflineMapCity)newOfflineMapCities.get(j);
//          updateCityInfo(offlineMapCity, newOfflineMapCity);
//        }
//      }
//    } else {
        if(mapProvinceArrayList.size()>0){
            mapProvinceArrayList.clear();
        }
        for (Iterator<OfflineMapProvince> localIterator = citylist.iterator(); localIterator.hasNext(); ) {
            offlineMapProvince = (OfflineMapProvince) localIterator.next();
            this.mapProvinceArrayList.add(offlineMapProvince);
        }
//    }
    }

    private void updateCityInfo(OfflineMapCity paramOfflineMapCity1, OfflineMapCity paramOfflineMapCity2) {
        paramOfflineMapCity1.setUrl(paramOfflineMapCity2.getUrl());
        paramOfflineMapCity1.setVersion(paramOfflineMapCity2.getVersion());
    }

    private void updateProvinceInfo(OfflineMapProvince paramOfflineMapProvince1, OfflineMapProvince paramOfflineMapProvince2) {
        paramOfflineMapProvince1.setUrl(paramOfflineMapProvince2.getUrl());
        paramOfflineMapProvince1.setVersion(paramOfflineMapProvince2.getVersion());
    }

    public ArrayList<OfflineMapCity> getDownloadOfflineMapCityList() {
        synchronized (this.mapProvinceArrayList) {
            ArrayList<OfflineMapCity> localArrayList1 = new ArrayList<OfflineMapCity>();
            for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
                ArrayList<OfflineMapCity> localArrayList2 = localOfflineMapProvince.getCityList();
                for (OfflineMapCity localOfflineMapCity : localArrayList2) {
                    if (localOfflineMapCity.getState() == 4) {
                        localArrayList1.add(localOfflineMapCity);
                    }
                }
            }
            return localArrayList1;
        }
    }

    public ArrayList<OfflineMapProvince> getDownloadOfflineMapProvinceList() {
        synchronized (this.mapProvinceArrayList) {
            ArrayList<OfflineMapProvince> localArrayList = new ArrayList<OfflineMapProvince>();
            for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
                if (localOfflineMapProvince.getState() == 4) {
                    localArrayList.add(localOfflineMapProvince);
                }
            }
            return localArrayList;
        }
    }

    public ArrayList<OfflineMapCity> getDownloadingCityList() {
        synchronized (this.mapProvinceArrayList) {
            ArrayList<OfflineMapCity> localArrayList1 = new ArrayList<OfflineMapCity>();
            for (OfflineMapProvince province : this.mapProvinceArrayList) {
                ArrayList<OfflineMapCity> localArrayList2 = province.getCityList();
                for (OfflineMapCity localOfflineMapCity : localArrayList2) {
                    if (localOfflineMapCity.getCity().equals("北京市")) {
                        int n = 0;
                    }
                    if (checkStatus(localOfflineMapCity.getState())) {
                        localArrayList1.add(localOfflineMapCity);
                    }
                }
            }
            return localArrayList1;
        }
    }

    public ArrayList<OfflineMapProvince> f() {
        synchronized (this.mapProvinceArrayList) {
            ArrayList<OfflineMapProvince> localArrayList = new ArrayList<OfflineMapProvince>();
            for (OfflineMapProvince localOfflineMapProvince : this.mapProvinceArrayList) {
                if (checkStatus(localOfflineMapProvince.getState())) {
                    localArrayList.add(localOfflineMapProvince);
                }
            }
            return localArrayList;
        }
    }

    public boolean checkStatus(int status) {
        return (status == OfflineMapStatus.LOADING) || (status == OfflineMapStatus.WAITING) || (status == OfflineMapStatus.PAUSE) || (status == OfflineMapStatus.UNZIP) || (status == OfflineMapStatus.ERROR);
    }

    public void a(CityObject cityObject) {
        String str = cityObject.getAdcode();
        //Iterator localIterator1;
        OfflineMapProvince newOfflineMapProvince;
        //label125:
        synchronized (this.mapProvinceArrayList) {
            for (Iterator<OfflineMapProvince> localIterator1 = this.mapProvinceArrayList.iterator(); localIterator1.hasNext(); ) {
                newOfflineMapProvince = (OfflineMapProvince) localIterator1.next();
                ArrayList<OfflineMapCity> localArrayList = newOfflineMapProvince.getCityList();
                for (OfflineMapCity localOfflineMapCity : localArrayList) {
                    if (localOfflineMapCity.getAdcode().trim().equals(str.trim())) {
                        a(cityObject, localOfflineMapCity);

                        a(cityObject, newOfflineMapProvince);

                        localOfflineMapCity = null;
                        //break label125;
                    }
                }
            }
        }
    }

    private void a(CityObject cityObject, OfflineMapCity offlineMapCity) {
        int i = cityObject.getCityStateImp().getState();
        if (cityObject.getCityStateImp().equals(cityObject.defaultState)) {
            d(cityObject.getAdcode());
        } else if ((!cityObject.getCityStateImp().equals(cityObject.completeState)) ||

                (a(cityObject.getcompleteCode(), cityObject.getCityStateImp().getState()))) {
            a(cityObject.v());
        }
        offlineMapCity.setState(i);
        offlineMapCity.setCompleteCode(cityObject.getcompleteCode());
    }

    private void a(CityObject cityObject, OfflineMapProvince mapProvince) {
        int i = cityObject.getCityStateImp().getState();
        if (i == 6) {
            mapProvince.setState(i);
            d(mapProvince.getProvinceCode());
            try {
                Utility.a(mapProvince.getProvinceCode(), this.context);
            } catch (IOException localIOException) {
                localIOException.printStackTrace();
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        } else if ((b(i)) &&
                (a(mapProvince))) {
            mapProvince.setState(i);
            UpdateItem localr = new UpdateItem(mapProvince, this.context);
            localr.saveJSONObjectToFile();
            a(localr);
        }
    }

    public void g() {
        h();
        this.handler = null;
        this.offlineDBOperation = null;
        this.context = null;
    }

    public void h() {
        this.mapProvinceArrayList.clear();
    }
}
