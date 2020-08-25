package com.sfmap.api.services.poisearch;

import android.content.Context;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.SearchException;

import org.json.JSONObject;

import java.net.Proxy;
import java.net.URLEncoder;
import java.util.List;

class PoiIdSearchServerHandler extends JsonResultHandler<String, List<PoiItem>> {

    private String poiId;
    private String region;

	public PoiIdSearchServerHandler(Context context, String  poiId, Proxy prx, String device, String adcode) {
		super(context, poiId, prx, device);
        this.poiId = poiId;
        this.region = adcode;
	}

	@Override
	protected String[] getRequestLines() {
        if (task != null) {
            String[] str = new String[1];
            StringBuilder sb = new StringBuilder();
			sb.append("ak=").append(this.mKey);
			sb.append("&region=").append(this.region);
//			sb.append("&scode=").append(this.scode);
            if(!isNullString(poiId)){
                sb.append("&ids=").append(this.poiId);
            }else {
                try {
                    throw new SearchException(  SearchException.ERROR_INVALID_PARAMETER);
                } catch (SearchException e) {
                    e.printStackTrace();
                }
            }
            str[0] = sb.toString();
            return str;
        }
        return null;
	}

	private boolean isNullString(String str) {
		if (null == str || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	protected int getServiceCode() {
		return 20;
	}

	@Override
	protected String getUrl() {
		String url = AppInfo.getSearchPoiidUrl(this.context)+ "?";
		return url;
	}

	@Override
	protected boolean getRequestType() {
		return true;
	}

	@Override
	protected List<PoiItem> parseJson(JSONObject obj) throws SearchException {
        List<PoiItem> result = null;
        processServerErrorcode(getJsonStringValue(obj, "status", ""),  getJsonStringValue(obj, "message", ""));
        // 解析结果
        result = new PoiJsonParser().PoiIdparser(obj);

        return result;
	}
}
