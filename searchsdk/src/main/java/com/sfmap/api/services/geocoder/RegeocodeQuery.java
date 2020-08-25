package com.sfmap.api.services.geocoder;

import com.sfmap.api.services.core.LatLonPoint;

import java.util.List;

/**
 * 此类定义了逆地理编码查询的地理坐标点、查询范围、坐标类型
 */
public class RegeocodeQuery {
	/**
	 * 地理坐标点集合, 支持多个点，格式为经度,纬度;经度,纬度。
	 */
    private List<LatLonPoint> points;
    /**
     * 地理坐标点。
     */
    private  LatLonPoint point;

	/**
	 * 搜索半径。
	 */
	private float radius;
    /**
     * 显示指定位置周边的poi。0：不显示；1：显示。
     */
    private boolean isShowPois = false;

	/**
	 * 坐标类型。
	 */
	private String latLonType = "gcj02";


    /**
     * RegeocodeQuery构造函数。
     *
     * @param points
     *  地理坐标点, 支持多个点，格式为经度,纬度;经度,纬度。
	 *  后台接口暂时没有计划支持批量的接口
     */
    private RegeocodeQuery(List<LatLonPoint> points) {
        this.points = points;
        setLatLonType(latLonType);
    }

    /**
     * RegeocodeQuery构造函数。
     * @param point 地理坐标点。
     */
    public RegeocodeQuery(LatLonPoint point) {
    	if(point == null) {
    		throw new IllegalArgumentException("ReGeo point is null");
		}
        this.point = point;
        setLatLonType(latLonType);
    }



    /**
     * RegeocodeQuery构造函数。
     *
     * @param points
     *            地理坐标点, 支持多个点，格式为经度,纬度;经度,纬度。
     * @param latLonType
     *            坐标类型。
     */
    private RegeocodeQuery(List<LatLonPoint> points, java.lang.String latLonType) {
        this.points = points;
        setLatLonType(latLonType);
    }


	/**
	 * 返回逆地理编码的地理坐标点。
	 * 
	 * @return 逆地理编码的地理坐标点。
	 */
	public LatLonPoint getPoint() {

        return this.point;
	}


    /**
     * 返回逆地理编码的地理坐标点集合。
     *
     * @return 逆地理编码的地理坐标点集合。
     */
    protected List<LatLonPoint> getPoints() {

        return this.points;
    }

	/**
	 * 设置逆地理编码的地理坐标点。
	 * 
	 * @param point
	 *            逆地理编码的地理坐标点。
	 */
	public void setPoint(LatLonPoint point) {
		this.point = point;
	}

	/**
	 * 返回查找范围。
	 * 
	 * @return 查找范围。
	 */
	private float getRadius() {
		return this.radius;
	}

	/**
	 * 设置查找范围。
	 * 
	 * @param radius
	 *            查找范围。
	 */
    private void setRadius(float radius) {
		this.radius = radius;
	}

	/***
	 * 返回参数坐标类型。
	 * 
	 * @return 参数坐标类型 :gcj02。
	 */
	public String getLatLonType() {
		return this.latLonType;
	}

	/**
	 * 设置参数坐标类型。
	 * 
	 * @param latLonType
	 *            坐标类型:gcj02。
	 */
	public void setLatLonType(String latLonType) {
		if ((latLonType != null)
				&& ((latLonType.equals("gcj02")) || (latLonType.equals("wgs84")))) {
			this.latLonType = latLonType;
		}
	}

    /**
     * 设置是否显示指定位置周边的poi。
     * @param isShowPois - true为显示，false为不显示。
     */
    public void setShowPoi(boolean isShowPois) {
        this.isShowPois = isShowPois;
    }

    /**
     * 返回是否显示指定位置周边的poi。
     * @return  true显示，false不显示。
     */
    public boolean isShow(){
        return  isShowPois;
    }

	public int hashCode() {
		int i = 31;
		int j = 1;
		j = i * j + (this.latLonType == null ? 0 : this.latLonType.hashCode());
		j = i * j + (this.points == null ? 0 : this.points.hashCode());
		j = i * j + Float.floatToIntBits(this.radius);
		return j;
	}

	public boolean equals(Object paramObject) {
		if (this == paramObject)
			return true;
		if (paramObject == null)
			return false;
		if (getClass() != paramObject.getClass())
			return false;
		RegeocodeQuery localRegeocodeQuery = (RegeocodeQuery) paramObject;
		if (this.latLonType == null) {
			if (localRegeocodeQuery.latLonType != null)
				return false;
		} else if (!this.latLonType.equals(localRegeocodeQuery.latLonType))
			return false;
		if (this.points == null) {
			if (localRegeocodeQuery.points != null)
				return false;
		} else if (!this.points.equals(localRegeocodeQuery.points))
			return false;
		if (Float.floatToIntBits(this.radius) != Float
				.floatToIntBits(localRegeocodeQuery.radius))
			return false;
		return true;
	}
}
