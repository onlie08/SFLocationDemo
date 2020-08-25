package com.sfmap.api.services.route;

import android.os.Parcel;

/**
 * 定义了公交换乘路径规划的一个换乘段的步行信息。
 * 
 */
public class RouteBusWalkItem extends WalkPath implements android.os.Parcelable {
    /**
     * 此路段步行导航信息的起点名称。
     */
    private String startName;

    /**
     * 此路段步行导航信息的终点名称。
     */
    private String endName;

	public static final Creator<RouteBusWalkItem> CREATOR = new RouteBusWalkItemCreator();

	/**
	 * RouteBusWalkItem 构造函数
	 */
	public RouteBusWalkItem() {

	}

    /**
     * 返回此路段步行导航信息的起点名称。
     * @return   此路段步行导航信息的起点名称。
     */
    public String getStartName() {
        return startName;
    }

    /**
     * 设置此路段步行导航信息的起点名称。
     * @param startName 此路段步行导航信息的起点名称。
     */
    public void setStartName(String startName) {
        this.startName = startName;
    }

    /**
     * 返回此路段步行导航信息的终点名称。
     * @return   此路段步行导航信息的终点名称。
     */
    public String getEndName() {
        return endName;
    }
    /**
     * 设置此路段步行导航信息的终点名称。
     * @param endName 此路段步行导航信息的终点名称。
     */
    public void setEndName(String endName) {
        this.endName = endName;
    }


	public RouteBusWalkItem(Parcel source) {
		super(source);
		this.startName = source.readString();
		this.endName = source.readString();

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.startName);
		dest.writeString(this.endName);
	}

}
