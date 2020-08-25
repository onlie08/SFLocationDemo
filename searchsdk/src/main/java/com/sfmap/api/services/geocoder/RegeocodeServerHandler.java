package com.sfmap.api.services.geocoder;

import android.content.Context;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.DesUtil;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.core.SearchException;
import com.sfmap.api.services.poisearch.PoiItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

class RegeocodeServerHandler extends
		JsonResultHandler<RegeocodeQuery, RegeocodeResult> {

	public RegeocodeServerHandler(Context context, RegeocodeQuery district,
			Proxy prx, String device) {
		super(context, district, prx, device);
	}

	@Override
	protected String[] getRequestLines() {
		if (task != null) {
            String[] str = new String[1];
            StringBuilder sb = new StringBuilder();
            try {
                RgeoBean rGeoBean =  new RgeoBean();
                rGeoBean.setX(String.valueOf(this.task.getPoint().getLongitude()));
                rGeoBean.setY(String.valueOf(this.task.getPoint().getLatitude()));

                rGeoBean.setAppPackageName(AppInfo.getPackageName(context));
                rGeoBean.setAppCerSha1(AppInfo.getSHA1(context));
                String ak = AppInfo.getMetaValue(context, AppInfo.CONFIG_MAP_API_KEY);

                String param = DesUtil.getInstance().encrypt(rGeoBean.toString());
                sb.append("param=").append(param);
                sb.append("&ak=").append(ak);
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e){ //在网络较差或其他情况导致定位失败的情况下，对rGeoBean中的参数进行加密时会出现数组越界异常，故做捕获处理
                e.printStackTrace();
            }

//            if(this.task.getPoint()!=null && this.task.getPoint().getLongitude()!=0 && this.task.getPoint().getLatitude()!=0){
//                sb.append("x=").append(this.task.getPoint().getLongitude());
//                sb.append("&y=").append(this.task.getPoint().getLatitude());
//            }else if(!isNullString(getLocationString(this.task.getPoints()))){
//               sb.append("&location=").append( getLocationString(this.task.getPoints()));//地理坐标集合
//            }
//            sb.append("&ak=").append("d8830a423db1430b9e3bbbe1b9cdcaed");//地理坐标集合
			str[0] = sb.toString();
			return str;
		}
		return null;
	}

	@Override
	protected int getServiceCode() {
		return 20;
	}

	@Override
	protected String getUrl() {
        String url = AppInfo.getApiRgeoUrl(this.context) + "?";
		return url;
	}


    private boolean isNullString(String str) {
        if (null == str || str.equals("")) {
            return true;
        } else {
            return false;
        }
    }


    private String getLocationString(List<LatLonPoint> list) {
        String str = "";
        if (list == null)
            return str;

        int size =  list.size();
        for (int i = 0; i < size; i++) {
            LatLonPoint location = list.get(i);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            if (i == size-1||size==0) {
                str += longitude + "," + latitude;
            } else {
                str += longitude + "," + latitude +";";
            }
        }
        return str;
    }

	@Override
	protected boolean getRequestType() {
		return true;
	}

	@Override
	protected RegeocodeResult parseJson(JSONObject obj) throws SearchException {
		RegeocodeResult geocodeResult = new RegeocodeResult();
        geocodeResult.setRegeocodeQuery(this.task);
		try {
			processServerErrorcode(getJsonStringValue(obj, "status", ""),
					getJsonStringValue(obj, "message", ""));

//			JSONArray resultArray = obj.getJSONArray("result");
            JSONObject jsonObj = obj.getJSONObject("result");
			if (jsonObj != null) {
//			if (resultArray != null) {
                List<RegeocodeAddress> addressList = new ArrayList<RegeocodeAddress>();
//				for (int i = 0; i < resultArray.length(); i++) {
//					JSONObject jsonObj = resultArray.getJSONObject(i);
					RegeocodeAddress regeocodeAddress = new RegeocodeAddress();

                    parseRegeocodeAddress(jsonObj,regeocodeAddress);
					addressList.add(regeocodeAddress);
//				}
				geocodeResult.setRegeocodeAddressList(addressList);
			}

		} catch (JSONException e) {
            throw new IllegalArgumentException("json解析失败");
		}
		return geocodeResult;
	}


    private void parseRegeocodeAddress(JSONObject jsonObj, RegeocodeAddress regeocodeAddress) throws  JSONException{
        regeocodeAddress.setFormatAddress(getJsonStringValue( jsonObj, "name", ""));
        regeocodeAddress.setDistance(Float .parseFloat(getJsonStringValue(jsonObj, "distance", "").equals("")?"0":getJsonStringValue(jsonObj, "distance", "")));
        regeocodeAddress.setStreetName(getJsonStringValue( jsonObj, "town", ""));
        regeocodeAddress.setProvince(getJsonStringValue( jsonObj, "province", ""));
        regeocodeAddress.setStreetNumber(getJsonStringValue( jsonObj, "street_no", ""));
        regeocodeAddress.setDistrict(getJsonStringValue( jsonObj, "district", ""));
        regeocodeAddress.setCity(getJsonStringValue(jsonObj, "city", ""));
        regeocodeAddress.setCityCode(getJsonStringValue( jsonObj, "regcode", ""));
        regeocodeAddress.setAdCode(getJsonStringValue( jsonObj, "adCode", ""));
//        regeocodeAddress.setFormatAddress(getJsonStringValue( jsonObj, "formatted_address", ""));
//        parseAddressComponent(jsonObj,regeocodeAddress);
        parsePOI(jsonObj,regeocodeAddress);
        LatLonPoint latLonPoint =  getLocation(jsonObj);
        regeocodeAddress.setLocation(latLonPoint);
    }

    private LatLonPoint getLocation(JSONObject jsonObject) throws  JSONException{
//        if (jsonObject.has("location")) {
//            JSONObject jsonLocation = jsonObject .getJSONObject("location");
//            Double lat = Double.parseDouble(jsonLocation .getString("lat").equals("")?"0":jsonLocation .getString("lat"));
//            Double lng  = Double .parseDouble(jsonLocation.getString("lng").equals("")?"0":jsonLocation .getString("lng"));
//            return  new LatLonPoint(lat,lng);
//        }
        if (jsonObject.has("xcoord")&&jsonObject.has("ycoord")) {
//            JSONObject jsonLocation = jsonObject .getJSONObject("location");
            Double lat = Double.parseDouble(jsonObject .getString("ycoord").equals("")?"0":jsonObject .getString("ycoord"));
            Double lng  = Double .parseDouble(jsonObject.getString("xcoord").equals("")?"0":jsonObject .getString("xcoord"));
            return  new LatLonPoint(lat,lng);
        }
        return null;
    }

    private void parsePOI(JSONObject jsonObj, RegeocodeAddress regeocodeAddress) throws  JSONException{
        if (jsonObj.has("pois")) {
            JSONArray jsonArray = jsonObj.getJSONArray("pois");
            if(jsonArray!=null){
                int length= jsonArray.length();
                List<PoiItem> poiList = new ArrayList<PoiItem>();
                for (int j = 0; j < length; j++){
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    PoiItem poiItem = new PoiItem();

                    poiItem.setPoiId(getJsonStringValue(jsonObject, "uid", ""));
                    poiItem.setDistance(Double .parseDouble(getJsonStringValue(jsonObject,  "distance", "")));
                    poiItem.setTitle(getJsonStringValue(jsonObject, "name", ""));
                    poiItem.setTel(getJsonStringValue(jsonObject, "tel", ""));
                    poiItem.setSnippet(getJsonStringValue(jsonObject, "addr", ""));
                    poiItem.setCity(getJsonStringValue(jsonObject, "city", ""));
                    poiItem.setTypeDes(getJsonStringValue(jsonObject, "type", ""));
                    poiItem.setAdcode(getJsonStringValue(jsonObject, "adcode", ""));
//                    String point = getJsonStringValue(jsonObject, "point", "");
//                    String[] latlon = point.split(",");
                    String x = getJsonStringValue(jsonObject, "x", "");
                    String y = getJsonStringValue(jsonObject, "y", "");
                    poiItem.setLatLonPoint(new LatLonPoint(Double.parseDouble(y),Double.parseDouble(x)));
                    poiList.add(poiItem);

                }
                regeocodeAddress.setPois(poiList);

            }
        }

    }

    private void parseAddressComponent(JSONObject jsonObj, RegeocodeAddress regeocodeAddress) throws  JSONException{
        if (jsonObj.has("addressComponent")) {
            JSONObject jsonObject = jsonObj .getJSONObject("addressComponent");
            regeocodeAddress.setDistance(Float .parseFloat(getJsonStringValue(jsonObject, "distance", "").equals("")?"0":getJsonStringValue(jsonObject, "distance", "")));
            regeocodeAddress.setStreetName(getJsonStringValue( jsonObject, "street", ""));
            regeocodeAddress.setProvince(getJsonStringValue( jsonObject, "province", ""));
            regeocodeAddress.setStreetNumber(getJsonStringValue( jsonObject, "street_number", ""));
            regeocodeAddress.setDistrict(getJsonStringValue( jsonObject, "district", ""));
            regeocodeAddress.setCity(getJsonStringValue(jsonObject, "city", ""));
            regeocodeAddress.setCityCode(getJsonStringValue( jsonObject, "cityCode", ""));
            regeocodeAddress.setAdCode(getJsonStringValue( jsonObject, "adCode", ""));

        }
    }

}
