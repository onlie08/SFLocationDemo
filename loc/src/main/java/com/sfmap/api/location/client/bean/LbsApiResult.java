package com.sfmap.api.location.client.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * 对外接口的统一返回结果对象
 * 
 * @author 01373280 2018
 */

public class LbsApiResult implements Serializable {
	private static final long serialVersionUID = 1535964835070227503L;
	public final static Integer SUCCESS = 0;
	public final static Integer ERROR = 1;
	@SerializedName("status")
	protected int status;
	@SerializedName("result")
	protected LbsApiResultData result;
	@SerializedName("src")
	protected String src;// 标识数据是哪里的
	@SerializedName("rtncode")
	protected String rtncode;


	public boolean isSuccess() {
		return SUCCESS == this.status;
	}


	public String buildRtnCode() {
		return "";
	}



	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getRtncode() {
		return rtncode;
	}

	public void setRtncode(String rtncode) {
		this.rtncode = rtncode;
	}

	public LbsApiResultData getResult() {
		return result;
	}

	public void setResult(LbsApiResultData result) {
		this.result = result;
	}

	public void destroy(){
		if(null != result){
			result.destroy();
			result = null;
		}
	}
}
