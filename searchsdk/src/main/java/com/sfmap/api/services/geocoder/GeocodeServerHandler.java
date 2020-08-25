package com.sfmap.api.services.geocoder;

import android.content.Context;
import android.text.TextUtils;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.DesUtil;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.core.SearchException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

class GeocodeServerHandler extends
		JsonResultHandler<GeocodeQuery, GeocodeResult> {

	public GeocodeServerHandler(Context context, GeocodeQuery district,
			Proxy prx, String device) {
		super(context, district, prx, device);
	}

	@Override
	protected String[] getRequestLines() {
		if (task != null) {
			String[] str = new String[1];
			StringBuilder sb = new StringBuilder();
			try {
				GeoBean geoBean =  new GeoBean();
				geoBean.setAddress(this.task.getLocationName());
				geoBean.setCity(this.task.getCity());
				geoBean.setAppPackageName(AppInfo.getPackageName(context));
				geoBean.setAppCerSha1(AppInfo.getSHA1(context));
				String ak = AppInfo.getMetaValue(context, AppInfo.CONFIG_MAP_API_KEY);

				String param = new DesUtil().encrypt(geoBean.toString());
				sb.append("param=").append(param);
				sb.append("&ak=").append(ak);
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
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
		String url = AppInfo.getApiGeoUrl(this.context) + "?";
		return url;
	}

	@Override
	protected boolean getRequestType() {
		return true;
	}

	@Override
	protected GeocodeResult parseJson(JSONObject obj) throws SearchException {
		GeocodeResult geocodeResult = new GeocodeResult();
        geocodeResult.setGeocodeQuery(this.task);
		try {
			processServerErrorcode(getJsonStringValue(obj, "status", ""),
					getJsonStringValue(obj, "message", ""));
			if (obj.has("result")) {
				JSONObject jsonObject = obj.getJSONObject("result");
				ArrayList<GeocodeAddress> addressList = new ArrayList<GeocodeAddress>();
				GeocodeAddress geocodeAddress = new GeocodeAddress();
				LatLonPoint latLonPoint = new LatLonPoint();
				latLonPoint.setLatitude(jsonObject.getDouble("ycoord"));
				latLonPoint.setLongitude(jsonObject.getDouble("xcoord"));
				geocodeAddress.setLatLonPoint(latLonPoint);
				geocodeAddress.setPrecise(getJsonStringValue( jsonObject, "precision", ""));
				geocodeAddress.setConfidence(getJsonStringValue( jsonObject, "match_level", ""));
				geocodeAddress.setLevel(getJsonStringValue(jsonObject, "type", ""));
				String adName = jsonObject.getString("adname").trim();
				if(!TextUtils.isEmpty(adName)){
					String[] adnames = adName.substring(1,adName.length()-1).split(",");
					if(adnames!=null && adnames.length>2){
						geocodeAddress.setProvince(adnames[0].replace("\"",""));
						geocodeAddress.setCity(adnames[1].replace("\"",""));
						geocodeAddress.setDistrict(adnames[2].replace("\"",""));
					}
				}
				geocodeAddress.setCityCode(getJsonStringValue( jsonObject, "regcode", ""));
				geocodeAddress.setAdCode(getJsonStringValue( jsonObject, "adcode", ""));
				geocodeAddress.setFormatAddress(getJsonStringValue( jsonObject, "src_address", ""));
				addressList.add(geocodeAddress);
				geocodeResult.setGeocodeAddressList(addressList);
			}
		} catch (JSONException e) {
            throw new IllegalArgumentException("json解析失败");
		}
		return geocodeResult;
	}

    private void parseGeocodeAddress(JSONObject jsonObject, GeocodeAddress geocodeAddress)throws  JSONException {

        geocodeAddress.setLevel(getJsonStringValue(jsonObject, "level", ""));
        geocodeAddress.setConfidence(getJsonStringValue( jsonObject, "confidence", ""));
        geocodeAddress.setPrecise(getJsonStringValue( jsonObject, "precise", ""));
        LatLonPoint latLonPoint  = getLocation(jsonObject);
        geocodeAddress.setLatLonPoint(latLonPoint);

        parsrAddresses(jsonObject,geocodeAddress);


    }

    private void parsrAddresses(JSONObject jsonObject, GeocodeAddress geocodeAddress) throws  JSONException{
        if(jsonObject.has("addresses")){
            JSONObject jsonObject2 = jsonObject.getJSONObject("addresses");
            geocodeAddress.setCity(getJsonStringValue( jsonObject2, "city", ""));
            geocodeAddress.setProvince(getJsonStringValue( jsonObject2, "province", ""));
            geocodeAddress.setDistrict(getJsonStringValue( jsonObject2, "district", ""));
            geocodeAddress.setFormatAddress(getJsonStringValue( jsonObject2, "name", ""));
            geocodeAddress.setAdCode(getJsonStringValue( jsonObject2, "adcode", ""));
        }

    }

    private LatLonPoint getLocation(JSONObject jsonObject) throws  JSONException{
        if (jsonObject.has("location")) {
            JSONObject jsonLocation = jsonObject .getJSONObject("location");
            Double lat = Double.parseDouble(jsonLocation .getString("lat"));
            Double lng  = Double .parseDouble(jsonLocation.getString("lng"));
            return  new LatLonPoint(lat,lng);
        }

        return null;
    }

}
