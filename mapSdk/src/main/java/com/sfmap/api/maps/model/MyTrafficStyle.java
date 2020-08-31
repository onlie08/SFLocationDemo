package com.sfmap.api.maps.model;

/**
 * 路况拥堵情况对应的颜色
 * 默认颜色分布为：
 * 畅通： 0xff3CB371
 * 缓慢： 0xffFFA500
 * 拥堵： 0xffFF0000
 * 严重拥堵： 0xffEEE8AA
 */
public class MyTrafficStyle
{
  private int smoothColor = 0xff3CB371;
  private int slowColor = 0xffFFA500;
  private int congestedColor = 0xffFF0000;
  private int seriousCongestedColor = 0xffEEE8AA;

    /**
     * 返回行驶畅通路段的标记颜色。默认颜色为0xff3CB371。
     * @return 行驶畅通路段的标记颜色。
     */
  public int getSmoothColor()
  {
    return this.smoothColor;
  }

    /**
     *设置行驶畅通路段的标记颜色。
     * @param smoothColor - 颜色值。
     */
  public void setSmoothColor(int smoothColor)
  {
    this.smoothColor = smoothColor;
  }

    /**
     *返回行驶缓慢路段的标记颜色。默认颜色为0xffFFA500。
     * @return 行驶缓慢路段的标记颜色。
     */
  public int getSlowColor()
  {
    return this.slowColor;
  }

    /**
     *设置行驶缓慢路段的标记颜色。
     * @param slowColor  - 颜色值。
     */
  public void setSlowColor(int slowColor)
  {
    this.slowColor = slowColor;
  }

    /**
     *返回行驶拥堵路段的标记颜色。默认颜色为0xffFF0000。
     * @return  行驶拥堵路段的标记颜色。
     */
  public int getCongestedColor()
  {
    return this.congestedColor;
  }

    /**
     *设置行驶拥堵路段的标记颜色。
     * @param congestedColor  - 颜色值。
     */
  public void setCongestedColor(int congestedColor)
  {
    this.congestedColor = congestedColor;
  }

    /**
     *返回行驶严重拥堵路段的标记颜色。默认颜色为0xffEEE8AA。
     * @return 行驶严重拥堵路段的标记颜色。
     */
  public int getSeriousCongestedColor()
  {
    return this.seriousCongestedColor;
  }

    /**
     *设置行驶严重拥堵路段的标记颜色。
     * @param seriousCongestedColor  - 颜色值。
     */
  public void setSeriousCongestedColor(int seriousCongestedColor)
  {
    this.seriousCongestedColor = seriousCongestedColor;
  }
}
