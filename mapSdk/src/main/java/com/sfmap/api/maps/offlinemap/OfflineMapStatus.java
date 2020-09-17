package com.sfmap.api.maps.offlinemap;

/**
 * 地图下载的相关状态码。
 * 状态说明
 * ERROR(< 0): 即需要重新下载进行处理,再次开始下载就会自动删除并下载
 * EXCEPTION(> 100): 暂时出现了问题，还可以继续
 * 其他均为正常状态
 */
public class OfflineMapStatus
{
  /**
   * 默认状态。
   */
  public static final int CHECKUPDATES = 6;
  /**
   * 解压失败错误，数据有可能有问题，所以重新下载。
   */
  public static final int ERROR = -1;
  /**
   * 停止下载。
   */
  public static final int STOP = 5;
  /**
   * 正在下载。
   */
  public static final int LOADING = 0;
  /**
   * 正在解压。
   */
  public static final int UNZIP = 1;
  /**
   * 等待下载。
   */
  public static final int WAITING = 2;
  /**
   * 暂停下载。
   */
  public static final int PAUSE = 3;
  /**
   * 下载成功。
   */
  public static final int SUCCESS = 4;
  /**
   * 有新版本。
   */
  public static final int NEW_VERSION = 7;
  /**
   * 下载过程中网络问题，不属于错误，下次还可以继续下载。
   */
  public static final int EXCEPTION_NETWORK_LOADING = 101;
  /**
   * Map认证等异常，请检查key，下次还可以继续下载。
   */
  public static final int EXCEPTION_AUTH = 102;
  /**
   * SD卡读写异常,下载过程有写入文件，解压过程也有写入文件即出现IOexception。
   */
  public static final int EXCEPTION_SDCARD = 103;
  /**
   * 下载数据MD5值错误；
   */
  public static final int EXCEPTION_MD5 = 104;
  /**
   * 开始下载失败，已下载该城市地图。
   */
  public static final int START_DOWNLOAD_FAILD = 1002;
}
