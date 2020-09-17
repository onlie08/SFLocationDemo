package com.sfmap.api.mapcore.util;

import android.content.Context;
import com.sfmap.api.maps.MapException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class OfflineInitHandler//j
        extends ProtocalHandler<String, OfflineInitBean>
{
  public OfflineInitHandler(Context context, String version)
  {
    super(context, version);
    setConnTimeout(5000);
    setReadTimeout(50000);
  }

  public String getUrl()
  {
    String url = null;
    return AppInfo.getSfMapURL(cnt)+"/config/version";
  }

  protected OfflineInitBean parseJson(String json)
          throws MapException
  {
    OfflineInitBean locali = new OfflineInitBean();
    try
    {
      JSONObject localJSONObject1 = new JSONObject(json);
      if (localJSONObject1.has("result"))
      {
        JSONObject resObj = localJSONObject1.getJSONObject("result");
        if(resObj.has("offlinemap")){
          JSONObject offlinemapObj = resObj.getJSONObject("offlinemap");
          int update = offlinemapObj.optInt("update",0);
          if (update == 0) {
            locali.setHasNewVersion(false);
          } else if (update == 1) {
            locali.setHasNewVersion(true);
          }
          locali.setVersion(offlinemapObj.optString("version", ""));
        }
      }
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "OfflineInitHandler", "loadData parseJson");
    }
    return locali;
  }

  public Map<String, String> getRequestParam()
  {
    Map<String,String> hashMap = new HashMap<String,String>();
    hashMap.put("mapver", this.task);
    hashMap.put("output", "json");
    hashMap.put("ak", AppInfo.getKey(this.cnt));
    hashMap.put("plattype", "android");
    hashMap.put("opertype", "offlinemap_with_province_vone");

    String str3 = ClientInfo.getTS();
    hashMap.put("ts", str3);
    hashMap.put("scode", ClientInfo.Scode(this.cnt, str3, ""));
    return hashMap;
  }
}
