package com.sfmap.api.location.client.bean;
 

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


public class LbsApiResultData implements Serializable {
	private static final long serialVersionUID = 559865666656588L;

	private static final int ERROR_CODE_SUCCESS = 0;
	//@JsonIgnore
	@SerializedName("src")
	protected String src;// 操作对象(暂只用于日志)
	@SerializedName("err")
	private Integer err;
	@SerializedName("msg")
	private String msg;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public boolean isSuccess() {
		return err == null || err == ERROR_CODE_SUCCESS;
	}
	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setErr(Integer err) {
		this.err = err;
	}

	public Integer getErr() {
		return err;
	}

	public boolean isAuthError() {
		if(err == null) {
			return false;
		}
		LbsResultCode code = LbsResultCode.getByCode(err);
		return code.isAuthError() || isAkFormatError();
	}

    private boolean isAkFormatError() {
        return err == LbsResultCode.UNKNOWN.getErr() && "ak格式不符合".equals(msg);
    }

    /**
	 * 用于日志的数据，有的服务是不需记录详细数据的，所以可覆盖此方法
	 * 
	 * @return
	 */
	
	public LbsApiResultData getLogData() {
		return this;
	}
	
	public void destroy(){
		
	}
}
