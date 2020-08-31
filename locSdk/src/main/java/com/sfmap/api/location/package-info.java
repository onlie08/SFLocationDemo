/**
 * <h2>概述</h2>
 * <p></p>
 * Android 定位 SDK 是一套简单的LBS服务定位接口，您可以使用这套定位API获取定位结果
 * <p></p>
 *
 * <h2>快速入门</h2>
 *
 * <p>Gradle引入maven库</p>
 *
 * <pre>
 * {@code
 *
 *  在 allprojects {
 *      repositories {
 *         .....
 *      }
 *  }
 *  配置对应的库
 *
 *  有SNAPSHOT 后缀使用 snapshots库
 *  snapshots 库
 *  maven {
 *     url 'http://nexus.sf-express.com/nexus/content/repositories/snapshots/'
 *  }
 *
 *  release 库
 *  maven {
 *     url 'http://nexus.sf-express.com/nexus/content/repositories/releases/'
 *  }
 *
 *  对应需要引入定位库的模块的build.gradle
 *  的
 *  dependencies {
 *     implementation 'com.sf.lss:sf-lss-nloc-android:0.0.1-SNAPSHOT'
 *      ......
 *  }
 * }
 * </pre>
 * <p>配置ak</p>
 * <pre>
 *     {@code
 *<application
 *              ····>
 *           <!-- sfmap api key -->
 *           <meta-data
 *              android:name="com.sfmap.location.api.apikey"
 *              android:value="c55e6757d8174a4a9857f5eba3720bc6" />
 *
 *     }
 * </pre>
 *
 * <p>定位代码实例 </p>
 * <pre>
 * {@code
 *      /初始定位客户端
 *       mSfMapLocationClient = new SfMapLocationClient(this);
 *
 *       //设置监听回调
 *       mSfMapLocationClient.setLocationListener(this);
 *
 *       //初始化定位参数
 *       mSfMapLocationClientOption = new SfMapLocationClientOption();
 *
 *       //设置定位间隔 或者设置单词定位
 *       mSfMapLocationClientOption.setInterval(10 * 1000);
 *       //mSfMapLocationClientOption.setOnceLocation(true);
 *
 *
 *       //设置参数
 *       mSfMapLocationClient.setLocationOption(mSfMapLocationClientOption);
 *
 *       //开始定位
 *       mSfMapLocationClient.startLocation();
 *
 *       //结束定位
 *       //mSfMapLocationClient.stopLocation();
 *
 *       //销毁客户端
 *       //mSfMapLocationClient.destroy();
 *
 *       //Android 6.0 之后版本需要动态申请定位权限
 *       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
 *          if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
 *          PackageManager.PERMISSION_GRANTED) {
 *          requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
 *          }
 *       }
 *     }
 * </pre>
 *
 *
 */

package com.sfmap.api.location;
