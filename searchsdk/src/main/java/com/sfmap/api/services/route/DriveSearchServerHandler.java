package com.sfmap.api.services.route;

import android.content.Context;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.DesUtil;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.core.SearchException;
import com.sfmap.api.services.route.RouteSearch.DriveRouteQuery;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class DriveSearchServerHandler extends
		JsonResultHandler<DriveRouteQuery, DriveRouteResult> {
	private int mItemCount = 0;
	private ArrayList<String> mSuggestions = new ArrayList<String>();
	public static final String VER = "2.5.6";//服务器约定的协议版本号

	public DriveSearchServerHandler(Context context, DriveRouteQuery qInternal,
			Proxy prx, String device) {
		super(context, qInternal, prx, device);
	}


	public int getItemCount() {
		return mItemCount;
	}

	public DriveRouteQuery getQuery() {
		return this.task;
	}

	public List<String> getSuggestion() {
		return mSuggestions;
	}

	@Override
	protected String[] getRequestLines() {
		if (task != null) {
			String[] str = new String[1];
			StringBuilder sb = new StringBuilder();
			//cc=1&opt=sf2&x1=116.496&y1=23.529&x2=114.049&y2=22.977&strategy=0&type=0&Vehicle=8&Weight=14.0&tolls=1&ak=55e00c8573f141e8878272479a21176c
//            sb_url.append(String.format("x1=%s&y1=%s&x2=%s&y2=%s&waypoints=%s&app_ver=%s&strategy=%s&Vehicle=%s&Mload=%s&VehicleDir=%s&ak=%s", param.fromX, param.fromY, param.toX, param.toY,param.viapoints, param.sdk_version,param.policy2,"1","0","-1.00","0376a9aa84724dd2a995f858dd963346"));

			sb.append("cc=")
					.append("1");// 起点坐标
			sb.append("&opt=")
					.append("sf2");// 起点坐标
			sb.append("&x1=")
					.append(getQuery().getFromAndTo().getFrom().getLongitude());// 起点坐标
			sb.append("&y1=")
					.append(getQuery().getFromAndTo().getFrom().getLatitude());// 起点坐标
			sb.append("&x2=")
					.append(getQuery().getFromAndTo().getTo().getLongitude());// 起点坐标
			sb.append("&y2=")
					.append(getQuery().getFromAndTo().getTo().getLatitude());// 起点坐标
			sb.append("&strategy=")
					.append(getQuery().getTactics() + "");// 导航策略
			sb.append("&type=")
					.append("0");// 导航策略
			sb.append("&Vehicle=")
					.append("8");// 导航策略
			sb.append("&Weight=")
					.append("14.0");// 导航策略
			sb.append("&tolls=")
					.append("1");// 导航策略
			sb.append("&ak=")
					.append("55e00c8573f141e8878272479a21176c");// 导航策略
//			if (0 != getQuery().getTactics()){
//				sb.append("&strategy=").append(getQuery().getTactics() + "");// 导航策略
//			}
//			sb.append("&app_ver=")
//					.append("9");// 起点坐标
////			sb.append("strategy=")
////					.append("9");// 起点坐标
//			sb.append("&Vehicle=")
//					.append("1");// 起点坐标
//			sb.append("&Mload=")
//					.append("0");// 起点坐标
//			sb.append("&VehicleDir=")
//					.append("-1.0");// 起点坐标
//			sb.append("&ak=")
//					.append("0376a9aa84724dd2a995f858dd963346");// 起点坐标

			// 入参设置
//			sb.append("origin=")
//					.append(getQuery().getFromAndTo().getFrom().getLongitude()
//							+ ","
//							+ getQuery().getFromAndTo().getFrom().getLatitude());// 起点坐标
//			sb.append("&destination=").append(
//					getQuery().getFromAndTo().getTo().getLongitude() + ","
//							+ getQuery().getFromAndTo().getTo().getLatitude());// 终点坐标
//			if (!isNullString(getQuery().getCoordType()))
//				sb.append("&coord_type=").append(getQuery().getCoordType());// 坐标类型
//			if (0 != getQuery().getTactics()){
//                    sb.append("&strategy=").append(getQuery().getTactics() + "");// 导航策略
//            }
//
//			if (!isNullString(getLocationString(getQuery().getPassedByPoints())))
//
//				sb.append("&waypoints=").append( getLocationString(getQuery().getPassedByPoints()));// 途径点坐标
//			if (!isNullString(getAvoidpolygonsString(getQuery() .getAvoidpolygons())))
//               sb.append("&avoidpolygons=").append( getAvoidpolygonsString(getQuery().getAvoidpolygons()));// 避让区域坐标


//
//			final RouteCarParam param = RouteRequestParamBuilder.buildCar(startPOI, endPOI, midPOIList, method, isNeedResetMyLocation);
//

//			RouteCarParam routeCarParam = new RouteCarParam();
//			routeCarParam.fromX = String.valueOf(getQuery().getFromAndTo().getFrom().getLongitude());
//			routeCarParam.fromY = String.valueOf(getQuery().getFromAndTo().getFrom().getLatitude());
//			routeCarParam.toX = String.valueOf(getQuery().getFromAndTo().getTo().getLongitude());
//			routeCarParam.toY = String.valueOf(getQuery().getFromAndTo().getTo().getLatitude());
//			routeCarParam.policy2 = String.valueOf(getQuery().getTactics());
//			routeCarParam.output = "bin";
//			routeCarParam.forwardDir = -1.0f;
//
////			String base_car_url = AppInfo.getRouteCarURL();
//			XmlBean xmlBean = new XmlBean();
//			String soapContent = getCarRouteXml(routeCarParam, getQuery().getPassedByPoints());
//			xmlBean.setSoapContent(soapContent.substring(soapContent.indexOf("?>") + 2));
//			xmlBean.setRouteType("1");
////			String packageName = AppInfo.getPackageName();
////			String Sha1 = AppInfo.getSHA1();
////			String ak = AppInfo.getAppApiKey();
////			xmlBean.setAppPackageName(packageName);
////			xmlBean.setAppCerSha1(Sha1);
//            //生产
//            xmlBean.setAppPackageName("com.sfmap.map");
//            xmlBean.setAppCerSha1("24:4F:38:2E:B7:9F:A9:10:F2:26:2D:49:9A:A5:4B:A7:A0:EB:51:28");
//            String ak = "89946b389666462a820d698ac67a4300";//这三个参数临时写固定，等拿到context后修改为代码获取
//
//
//			String xmlParam = null;
//			try {
//				xmlParam = new DesUtil().encrypt(xmlBean.toString());
//			} catch (IllegalBlockSizeException e1) {
//				e1.printStackTrace();
//			} catch (BadPaddingException e1) {
//				e1.printStackTrace();
//			}
//
//			HashMap<String, Object> map = new HashMap<>();
//			map.put("param", xmlParam);
//			map.put("ak", ak);
//
//			sb.append("param=")
//					.append(xmlParam);// 起点坐标
//			sb.append("&ak=")
//					.append(ak);// 起点坐标


			str[0] = sb.toString();
        return str;
    }
		return null;
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

	private String getAvoidpolygonsString(List<List<LatLonPoint>> list) {
		String str = "";
		if (list == null)
			return str;

        int size = list.size();
		for (int i = 0; i < size; i++) {
			List<LatLonPoint> location = list.get(i);
            if (i == size-1||size==0) {
				str += getLocationString(location);
			} else {
				str += getLocationString(location) + "|";
			}
		}
		return str;
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
		String url = null;
//		url = AppInfo.getRouteCarURL(this.context) + "?";
		url = "https://gis.sf-express.com/rp/v2/api" + "?";
		return url;
	}

	@Override
	protected boolean getRequestType() {
		return true;
	}

	@Override
	protected DriveRouteResult parseJson(JSONObject obj) throws SearchException {
		DriveRouteResult pr = null;
		// 解析结果
		processServerErrorcode(getJsonStringValue(obj, "status", ""),
				getJsonStringValue(obj, "message", ""));
		pr = new RouteJsonParser().driveParse(obj);
		if (pr != null)
			pr.setDriveQuery(this.task);
		return pr;
	}

	public static String getCarRouteXml(RouteCarParam routeCarParam, List<LatLonPoint> midPOIList) {
		String xmlString = "";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			document.setXmlStandalone(true);

			Element route = document.createElement("route");
			document.appendChild(route);
			route.setAttribute("forwardDir", String.valueOf(routeCarParam.forwardDir));

			route.setAttribute("ContentOptions", "0x14");
			route.setAttribute("Flag", "4104");
			route.setAttribute("SdkVer", "3.3.1.1.1.20150207.3713.78");
			route.setAttribute("Source", "sfmap");
			route.setAttribute("Type", routeCarParam.policy2);
			route.setAttribute("Uuid", UUID.randomUUID().toString());
			route.setAttribute("Vers", VER);
			route.setAttribute("No", "355888091184779_" + System.currentTimeMillis());
//			route.setAttribute("No", com.sfmap.tbt.util.DeviceInfo.getDeviceID(MapApplication.getContext()) + "_" + System.currentTimeMillis());

			Element Vehicle = document.createElement("Vehicle");
			Vehicle.setAttribute("Type", "1");
			route.appendChild(Vehicle);

			Element startPoint = document.createElement("startpoint");
			route.appendChild(startPoint);
			// 此处循环添加startpoint的几个子元素
			Element startX = document.createElement("x");
			startX.setTextContent(routeCarParam.fromX);
			startPoint.appendChild(startX);
			Element startY = document.createElement("y");
			startY.setTextContent(routeCarParam.fromY);
			startPoint.appendChild(startY);

			if(midPOIList != null){
				int size = midPOIList.size();
				for (int i = 0; i < size; i++) {
					Element viaPoint = document.createElement("viapoint");
					route.appendChild(viaPoint);
					Element viaX = document.createElement("x");
					viaX.setTextContent(midPOIList.get(i).getLongitude() + "");
					viaPoint.appendChild(viaX);
					Element viaY = document.createElement("y");
					viaY.setTextContent(midPOIList.get(i).getLatitude() + "");
					viaPoint.appendChild(viaY);
				}
			}

			Element endPoint = document.createElement("endpoint");
			route.appendChild(endPoint);
			// 此处循环添加startpoint的几个子元素
			Element endX = document.createElement("x");
			endX.setTextContent(routeCarParam.toX);
			endPoint.appendChild(endX);
			Element endY = document.createElement("y");
			endY.setTextContent(routeCarParam.toY);
			endPoint.appendChild(endY);

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(document);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			transformer.transform(domSource, new StreamResult(bos));
			xmlString = bos.toString();
			System.out.println(xmlString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xmlString;
	}

	class RouteCarParam {

		public int off = 0;

		public String fromX;

		public String fromY;

		public String toX;

		public String toY;
		// 算路原则
		public String policy2;
		// 起点poiid
		public String start_poiid;
		// 起点poi类型
		public String start_types;
		// 终点poiid
		public String end_poiid;
		// 终点poi类型
		public String end_types;
		// 途经点经纬度
		public String viapoints;
		// 途经点poi类型
		public String viapoint_types;
		// 途经点poiid
		public String viapoint_poiids;

		public String output = "bin";
		// tbt版本号
		public String sdk_version;

		public float forwardDir;
	}

}
