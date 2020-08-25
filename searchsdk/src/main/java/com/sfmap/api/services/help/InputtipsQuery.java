package com.sfmap.api.services.help;

/**
 * 此类定义了输入提示的关键字，类别及城市。
 */
public class InputtipsQuery implements java.lang.Cloneable {
	/**
	 * 输入的关键字,或关键字的首字母、拼音。
	 */
	private String keyword;
	/**
	 * 检索区域名称，可输入城市名、省份名或全国。
	 */
	private String citycode;
	/**
	 * 定位点与屏幕中心点同城--使用定位点  不同城--使用屏幕中心点
	 */
	private String location;
	/**
	 * 搜索类型
	 */
	private String datasource;
	/**
	 * 搜索半径
	 */
	private String radius;

	public String getAdcode() {
		return adcode;
	}

	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}

	/**
	 * 检索区域名称，可输入城市名、省份名或全国。
	 */
	private String adcode;
	/**
	 * 检索结果的详细程度，1：返回基本信息; 2：返回POI详细信息 ;默认值为1。
	 */
	private int scope = 1;

	/**
	 * 根据给定的参数来构造一个InputtipsQuery的对象。
	 * 
	 * @param keyword
	 *            输入的关键字。
	 * @param adcode
	 *            检索区域名称，可输入城市名、省份名或全国。
	 */
	public InputtipsQuery(String keyword, String adcode) {
		super();
		this.keyword = keyword;
		this.adcode = adcode;
	}

	/**
	 * 根据给定的参数来构造一个InputtipsQuery的对象。
	 * 
	 * @param keyword
	 *            输入的关键字。
	 * @param adcode
	 *            检索区域名称，可输入城市名、省份名或全国。
	 * @param scope
	 *            返回信息的详细程度。
	 */
	public InputtipsQuery(String keyword, String adcode, int scope) {
		this.keyword = keyword;
		this.adcode = adcode;
		this.scope = scope;
	}

	/**
	 * 返回查询关键字。
	 * 
	 * @return 查询关键字。
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * 设置查询关键字。
	 * 
	 * @param keyword
	 *            查询关键字。
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * 返回待查城市（地区）的电话区号。
	 * 
	 * @return 返回待查城市（地区）的电话区号。
	 */
	public String getCity() {
		return citycode;
	}

	/**
	 * 设置待查城市（地区）的电话区号。
	 * 
	 * @param city
	 *            待查城市（地区）的电话区号。
	 */
	public void setCity(String city) {
		this.citycode = city;
	}

	/**
	 * 返回检索结果的详细程度值。
	 * 
	 * @return 检索结果的详细程度值。
	 */
	public int getScope() {
		return scope;
	}

	/**
	 * 设置检索结果的详细程度，1：返回基本信息; 2：返回POI详细信息 ;默认值为1。
	 * 
	 * @param scope
	 *            检索结果的详细程度。
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return new InputtipsQuery(this.keyword, this.adcode, this.scope);
	}

}
