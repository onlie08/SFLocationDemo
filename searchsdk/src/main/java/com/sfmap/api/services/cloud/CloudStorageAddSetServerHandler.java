package com.sfmap.api.services.cloud;

import android.content.Context;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.SearchException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.List;

class CloudStorageAddSetServerHandler extends JsonResultHandler<Object,CloudStorageResult> {
    private String path="/gds/storage/dataSet/create?";
    /**
     * 使用Json解析返回结果。
     *
     * @param context
     * @param tsk
     * @param prx
     * @param device
     */
    public CloudStorageAddSetServerHandler(Context context, Object tsk, Proxy prx, String device) {
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
                    String datasetId = results.getString("datasetId");
                    if(!datasetId.equals("")||datasetId!=null){
                        try{
                        int id=Integer.valueOf(datasetId);
                        result.setDatasetId(id);
                        }
                        catch(NumberFormatException ex){

                        }
                    }
                }
            }
        } catch (SearchException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected boolean getRequestType() {
        return false;
    }

    @Override
    protected int getServiceCode() {
        return 0;
    }

    @Override
    protected String[] getRequestLines() {

        if (task != null) {
            CloudStorage.Add addObject=(CloudStorage.Add)task;
            String[] str = new String[1];
            StringBuilder sb = new StringBuilder();
            try {
                CloudDatasetItem item=addObject.getDatasetItem();
                // 入参设置
                if (addObject!=null&&item!=null){
                    sb.append("name=").append(item.getName());
                    sb.append("&geoType=").append(item.getGeoType());

                    //拼自定义字段 json字符串
                    StringBuffer fieldsInfoString=new StringBuffer();
                    List<DBFieldInfo> fieldInfoList=item.getFieldInfos();
                    if(fieldInfoList!=null&&fieldInfoList.size()>0) {
                        fieldsInfoString.append("[");
                        for (int j = 0; j < fieldInfoList.size(); j++) {
                            if (j != 0) fieldsInfoString.append(",");
                            DBFieldInfo fieldInfo = fieldInfoList.get(j);
                            fieldsInfoString.append("{");
                            String fieldString = "\"fieldName\":\""+fieldInfo.getFieldName()+"\",";
                                    fieldString+="\"fieldTitle\":\""+fieldInfo.getFieldTitle()+"\",";
                                    fieldString+="\"fieldType\":\""+fieldInfo.getFieldType().getTypeString()+"\",";
                                    fieldString+="\"dbindex\":\""+fieldInfo.getDbindex()+"\"";

                            fieldsInfoString.append(fieldString);
                            fieldsInfoString.append("}");
                        }
                        fieldsInfoString.append("]");
                    }
                    sb.append("&fieldsJson=").append(fieldsInfoString);
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
        url = AppInfo.getCloudUrl(this.context);
        return  url+path;
    }
}
