package com.sfmap.api.services.cloud;

import android.content.Context;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.SearchException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Proxy;



class CloudStorageDelServerHandler extends JsonResultHandler<Object,CloudStorageResult> {
    private String path="/gds/storage/data/delete?";
    /**
     * 使用Json解析返回结果。
     *
     * @param context
     * @param tsk
     * @param prx
     * @param device
     */
    public CloudStorageDelServerHandler(Context context, Object tsk, Proxy prx, String device) {
        super(context, tsk, prx, device);
    }

    @Override
    protected CloudStorageResult parseJson(JSONObject obj) throws SearchException {
        CloudStorageResult result=null;
        try {
            processServerErrorcode(getJsonStringValue(obj, "status", ""),
                    getJsonStringValue(obj, "message", ""));
            // 解析结果
            if (obj.has("results")) {
                JSONObject results = obj.optJSONObject("results");
                if (results != null) {
                    result=new CloudStorageResult();
                    JSONArray jsonArray = results.optJSONArray("ids");
                    if(jsonArray!=null&&jsonArray.length()>0) {
                        int[] ids=new int[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ids[i] = jsonArray.getInt(i);
                        }
                        result.setId(ids);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected boolean getRequestType() {
        return true;
    }

    @Override
    protected int getServiceCode() {
        return 0;
    }

    @Override
    protected String[] getRequestLines() {
        if (task != null) {
            CloudStorage.Del addObject=(CloudStorage.Del)task;
            String[] str = new String[1];
            StringBuilder sb = new StringBuilder();
            try {
                // 入参设置
                if (addObject!=null){ // bbox
                    sb.append("datasetId=").append(addObject.getDataSetId());

                    StringBuffer dataJson=new StringBuffer();
                    int[] items= addObject.getDataIds();
                    for(int i=0;i<items.length;i++){
                       if(i!=0) dataJson.append(",");
                        dataJson.append(items[i]);
                    }
                    sb.append("&ids=").append(dataJson);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            str[0] = sb.toString();
            return str;
        }
        return null;
    }

    @Override
    protected String getUrl() {
        String url = null;
        url = AppInfo.getCloudUrl(this.context)+path;
        return  url+path;
    }
}
