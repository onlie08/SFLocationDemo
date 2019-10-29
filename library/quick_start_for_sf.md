## 1、SDK集成

如果需要使用，顺丰定位SDK，配置公司内部maven库之后
```groovy
    maven {
        url "http://artifactory.sf-express.com/artifactory/maven/"
    }
```

再配置依赖及可使用， 
```groovy
implementation 'com.sf.lss:sf-lss-nloc-android:1.2.3' 
```
最新版本可以通过
http://nexus.sf-express.com/nexus/content/groups/public/com/sf/lss/sf-lss-nloc-android/
查找


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

        locationOption.setNeedAddress(true);
        locationOption.setOnceLocation(true);
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