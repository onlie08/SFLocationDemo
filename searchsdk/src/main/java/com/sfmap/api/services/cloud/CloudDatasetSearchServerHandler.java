package com.sfmap.api.services.cloud;


import android.content.Context;
import android.text.TextUtils;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.SearchException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

class CloudDatasetSearchServerHandler extends
        JsonResultHandler<CloudDatasetSearch.Query, CloudDatasetSearchResult>{

    public CloudDatasetSearchServerHandler(Context context, CloudDatasetSearch.Query tsk, Proxy prx, String device) {
        super(context, tsk, prx, device);
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
        String url = AppInfo.getCloudUrl(this.context);
        String path="";
        if (this.task.isIdSearch()){
            path = "/gds/storage/dataSet/get?";
        } else if (this.task.isPageSearch()){
            path = "/gds/storage/dataSet/list?";
        } else if (this.task.isAllSearch()){
            path = "/gds/storage/dataSet/listAll?";
        }
        return url+path;
    }

    @Override
    protected String[] getRequestLines() {
        if (task != null) {
            String[] str = new String[1];
            StringBuilder sb = new StringBuilder();
            sb.append("ak=").append(this.mKey);
            sb.append("&scode=").append(this.scode);

            try {
                if (this.task.isIdSearch()){
                    if (this.task.getId()>0){
                        sb.append("&id=").append(this.task.getId());
                    } else {
                        throw new SearchException(
                                SearchException.ERROR_INVALID_PARAMETER);
                    }
                } else if (this.task.isPageSearch()){
                    if (this.task.getId()>0){
                        sb.append("&id=").append(this.task.getId());
                    }
                    if (this.task.getUserId()>0){
                        sb.append("&userId=").append(this.task.getUserId());
                    }
                    if (!TextUtils.isEmpty(this.task.getName())){
                        sb.append("&name=").append(URLEncoder.encode(this.task.getName(),"UTF-8"));
                    }
                    if (this.task.getGeoType()>0){
                        sb.append("&geoType=").append(this.task.getGeoType());
                    }
                    if (this.task.getPageNumber()>=0){
                        sb.append("&pageNumber=").append(this.task.getPageNumber());
                    }
                    if (this.task.getPageSize()>0){
                        sb.append("&pageSize=").append(this.task.getPageSize());
                    }
                } else if (this.task.isAllSearch()){
                    if (this.task.getId()>0){
                        sb.append("&id=").append(this.task.getId());
                    }
                    if (this.task.getUserId()>0){
                        sb.append("&userId=").append(this.task.getUserId());
                    }
                    if (!TextUtils.isEmpty(this.task.getName())){
                        sb.append("&name=").append(URLEncoder.encode(this.task.getName(),"UTF-8"));
                    }
                    if (this.task.getGeoType()>0){
                        sb.append("&geoType=").append(this.task.getGeoType());
                    }
                }
            } catch (SearchException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            str[0] = sb.toString();
            return str;
        }
        return null;
    }

    @Override
    protected CloudDatasetSearchResult parseJson(JSONObject obj) throws SearchException {
        CloudDatasetSearchResult result = null;

        try {
            processServerErrorcode(getJsonStringValue(obj, "status", ""),
                    getJsonStringValue(obj, "message", ""));
            if (obj.has("results")) {
                JSONObject results = obj.optJSONObject("results");
                if (results != null){
                    if (this.task.isIdSearch()){
                        List<CloudDatasetItem> cloudItems = new ArrayList<>();
                        CloudDatasetItem cloudItem = new CloudDatasetItem();
                        // 解析
                        parseCloudItem(results, cloudItem);
                        cloudItems.add(cloudItem);

                        result = new CloudDatasetSearchResult(this.task, cloudItems);
                        result.setTotal(1);
                        result.setMessage(getJsonStringValue(obj, "message", ""));
                    } else if (this.task.isPageSearch() || this.task.isAllSearch()){
                        JSONArray jsonArray = results.optJSONArray("result");
                        List<CloudDatasetItem> cloudItems = new ArrayList<>();
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                CloudDatasetItem cloudItem = new CloudDatasetItem();
                                // 解析
                                parseCloudItem(jsonObj, cloudItem);
                                cloudItems.add(cloudItem);
                            }
                            result = new CloudDatasetSearchResult(this.task, cloudItems);
                            result.setPageNum(results.optInt("pageNum"));
                            result.setPageSize(results.optInt("pageSize"));
                            result.setStartRow(results.optInt("startRow"));
                            result.setEndRow(results.optInt("endRow"));
                            result.setTotal(results.optLong("total"));
                            result.setPages(results.optInt("pages"));
                            result.setMessage(getJsonStringValue(obj, "message", ""));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void parseCloudItem(JSONObject obj, CloudDatasetItem mapItem){
        mapItem.setId(obj.optLong("id"));
        mapItem.setName(obj.optString("name"));
        mapItem.setUserId(obj.optLong("userId"));
        mapItem.setGeoType(obj.optInt("geoType"));
        try {
            if (obj.has("fields")){
                JSONArray jsonArray = obj.optJSONArray("fields");
                if (jsonArray != null){
                    List<DBFieldInfo> infos = new ArrayList<>();
                    int len = jsonArray.length();
                    for (int i=0;i<len;i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        DBFieldInfo info = new DBFieldInfo();
                        info.setCreateTime(object.optLong("createDate"));
                        info.setDbindex(object.optInt("dbindex"));
                        info.setEditTime(object.optLong("editDate"));
                        info.setFieldName(object.optString("fieldName"));
                        info.setFieldSize(object.optInt("fieldSize"));
                        info.setFieldTitle(object.optString("fieldTitle"));
                        String fieldType = object.optString("fieldType");
                        if (DBFieldInfo.FieldType.type_double.getTypeString().equals(fieldType)){
                            info.setFieldType(DBFieldInfo.FieldType.type_double);
                        } else if (DBFieldInfo.FieldType.type_integer.getTypeString().equals(fieldType)){
                            info.setFieldType(DBFieldInfo.FieldType.type_integer);
                        } else if (DBFieldInfo.FieldType.type_long.getTypeString().equals(fieldType)){
                            info.setFieldType(DBFieldInfo.FieldType.type_long);
                        } else if (DBFieldInfo.FieldType.type_text.getTypeString().equals(fieldType)){
                            info.setFieldType(DBFieldInfo.FieldType.type_text);
                        } else if (DBFieldInfo.FieldType.type_varchar.getTypeString().equals(fieldType)){
                            info.setFieldType(DBFieldInfo.FieldType.type_varchar);
                        }
                        info.setId(object.optInt("id"));
                        infos.add(info);
                    }
                    mapItem.setFieldInfos(infos);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
