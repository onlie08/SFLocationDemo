package com.sfmap.api.services.help;

import android.content.Context;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.core.SearchException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 提示查询控制类。
 */
class InputtipsServerHandler extends
        JsonResultHandler<InputtipsQuery, List<Tip>> {

    private boolean isSearchNearBy;
    private double latiude;
    private double longitude;

    public InputtipsServerHandler(Context context, InputtipsQuery tsk,
                                  Proxy prx, String device, boolean isSearchNearBy, double latitude, double longitude) {
        super(context, tsk, prx, device);
        this.isSearchNearBy = isSearchNearBy;
        this.latiude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected String[] getRequestLines() {
        if (task != null) {
            String[] str = new String[1];
            StringBuilder sb = new StringBuilder();
            sb.append("ak=").append(this.mKey);
//            sb.append("&scode=").append(this.scode);
            try {
                sb.append("&query=").append(URLEncoder.encode(this.task.getKeyword(), "UTF-8"));
                sb.append("&region=").append(URLEncoder.encode(this.task.getAdcode(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            sb.append("&scope=").append(this.task.getScope());
            sb.append("&radius=").append(this.task.getRadius());
            sb.append("&datasource=").append(this.task.getDatasource());
            if(isSearchNearBy){//如果是在附近界面查询输入提示，则传入poi的位置信息
                sb.append("&location=").append(longitude+","+latiude);
            }else{
                sb.append("&location=").append(this.task.getLocation());
            }
            sb.append("&sort=0");

            str[0] = sb.toString();
            return str;
        }
        return null;
    }

    @Override
    protected boolean getRequestType() {
        return true;
    }

    @Override
    protected int getServiceCode() {
        return 20;
    }

    @Override
    protected String getUrl() {
        String url = null;
		url = AppInfo.getSearchSugUrl(this.context) + "?";
//        url = "http://10.82.248.148:8080/msp/v3/sug?";
        return url;
    }

    /**
     * json解析
     *
     * @throws SearchException
     */
    @Override
    protected List<Tip> parseJson(JSONObject obj) throws SearchException {
        List<Tip> tipList = new ArrayList<Tip>();
        processServerErrorcode(getJsonStringValue(obj, "status", ""),
                getJsonStringValue(obj, "message", ""));
        try {
            boolean tag = obj.has("result");
            if (tag == false) {
                return null;
            }
            JSONArray resultArray = obj.getJSONArray("result");
            if (resultArray != null) {
                int lenth = resultArray.length();
                for (int i = 0; i < lenth; i++) {
                    JSONObject jsonObject = resultArray.getJSONObject(i);
                    Tip tip = new Tip();
                    parseTip(jsonObject, tip);

                    tipList.add(tip);
                }
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException("json解析失败");// e.getLocalizedMessage()
        }
        return tipList;
    }


    private void parseTip(JSONObject jsonObject, Tip tip) throws JSONException {
        tip.setPoiID(getJsonStringValue(jsonObject, "uid", ""));
        tip.setName(getJsonStringValue(jsonObject, "name", ""));
        tip.setAdcode(getJsonStringValue(jsonObject, "adcode", ""));
        tip.setDistrict(getJsonStringValue(jsonObject, "district", ""));
        tip.setType(getJsonStringValue(jsonObject, "type", ""));
        tip.setBusiness(getJsonStringValue(jsonObject, "business", ""));
        tip.setCity(getJsonStringValue(jsonObject, "city", ""));
        tip.setDatasource(getJsonStringValue(jsonObject, "datasource", ""));
        tip.setCitycode(getJsonStringValue(jsonObject, "city_code", ""));
        tip.setAddress(getJsonStringValue(jsonObject, "address", ""));
        tip.setExttype(getJsonStringValue(jsonObject, "exttype", ""));
        tip.setExtinfo(getJsonStringValue(jsonObject, "extinfo", ""));
        tip.setDeepinfos(getJsonStringValue(jsonObject, "deep_infos", ""));
        parseLocationAndDetail(jsonObject, tip);
    }

    private void parseLocationAndDetail(JSONObject jsonObject, Tip tip) throws JSONException {
        if (jsonObject.has("location")) {
            JSONObject json = jsonObject.getJSONObject("location");
            if (json != null) {
                String lonstr = getJsonStringValue(json, "lng", "");
                String latstr = getJsonStringValue(json, "lat", "");
                if (lonstr.length() > 0 && latstr.length() > 0) {
                    tip.setPoint(new LatLonPoint(Double.parseDouble(latstr), Double.parseDouble(lonstr)));
                } else {
                    tip.setPoint(null);
                }
            } else {
                tip.setPoint(null);
            }

        }

        if (jsonObject.has("detail_info")) {
            JSONObject json = jsonObject.getJSONObject("detail_info");
            if (json != null) {
                tip.setAddress(getJsonStringValue(json, "address", ""));
                tip.setTelephone(getJsonStringValue(json, "telephone", ""));
            }

        }
    }


}
