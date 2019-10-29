## 1、SDK集成

在编译依赖配置中增加对sdk aar包的依赖
```groovy
    implementation files('path_to_aar_file')
```

定位库依赖google gson库，需要额外加上

## 2、初始化定位客户端

```java
    private void initSfLocationClient() {
        //初始定位客户端
        mSfMapLocationClient = new SfMapLocationClient(this);

        //设置监听回调
        mSfMapLocationClient.setLocationListener(this);

        //初始化定位参数
        SfMapLocationClientOption locationOption = new SfMapLocationClientOption();

        //设置定位间隔 或者设置单词定位
        locationOption.setInterval(5 * 1000);

        locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.High_Accuracy);
        //mSfMapLocationClientOption.setOnceLocation(true);
        
        //是否单次定位
        locationOption.setOnceLocation(false);
        
        //是否使用默认的gjc02坐标系，false则使用WGS84坐标系
        locationOption.setUseGjc02(true);
        //是否使用离线缓存定位（旧的定位数据）
        locationOption.setLocationCacheEnabled(false);
        //设置参数
        mSfMapLocationClient.setLocationOption(locationOption);
        
    }
```

## 3、开始定位
```java
    //开始定位
    mSfMapLocationClient.startLocation();
```

## 4、结束定位
```java
    //开始定位
    mSfMapLocationClient.stopLocation();
```

## 5、注销定位客户端

```java
    mSfMapLocationClient.destroy();
```