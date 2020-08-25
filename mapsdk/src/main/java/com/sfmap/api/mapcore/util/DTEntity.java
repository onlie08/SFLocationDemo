package com.sfmap.api.mapcore.util;

import android.content.ContentValues;
import android.database.Cursor;

class DTEntity//u
  implements SQlEntity<DTInfo>
{
  private DTInfo dtInfo = null;
  
  //public x a(Cursor paramCursor)
  public DTInfo select(Cursor paramCursor)	////原来的方法名为a，怀疑是反编译时特别对于模版的基类有错误。
  {
    DTInfo localx = null;
    try
    {
      String str1 = paramCursor.getString(paramCursor.getColumnIndex(OfflineDBCreator.TITLE));
      String str2 = paramCursor.getString(paramCursor.getColumnIndex(OfflineDBCreator.URL));
      String str3 = paramCursor.getString(paramCursor
        .getColumnIndex(OfflineDBCreator.ADCODE));
      String str4 = paramCursor.getString(paramCursor
        .getColumnIndex(OfflineDBCreator.FILE_NAME));
      
      String str5 = paramCursor.getString(paramCursor
        .getColumnIndex(OfflineDBCreator.VERSION));
      long l1 = paramCursor.getLong(paramCursor
        .getColumnIndex(OfflineDBCreator.LOCAL_LENGTH));
      long l2 = paramCursor.getLong(paramCursor
        .getColumnIndex(OfflineDBCreator.REMOTE_LENGTH));
      String str6 = paramCursor.getString(paramCursor
        .getColumnIndex(OfflineDBCreator.LOCAL_PATH));
      
      int index = paramCursor.getInt(paramCursor.getColumnIndex(OfflineDBCreator.M_INDEX));
      boolean bool = paramCursor.getInt(paramCursor
        .getColumnIndex(OfflineDBCreator.IS_PROVINCE)) == 1;
      int j = paramCursor.getInt(paramCursor
        .getColumnIndex(OfflineDBCreator.M_COMPLETE_CODE));
      String str7 = paramCursor.getString(paramCursor
        .getColumnIndex(OfflineDBCreator.M_CITY_CODE));
      
      int state = paramCursor.getInt(paramCursor.getColumnIndex(OfflineDBCreator.M_STATE));
      
      UpdateItem localr = new UpdateItem();
      localr.setCity(str1);
      localr.setUrl(str2);
      localr.setAdcode(str3);
      localr.setDataFileTempPath(str4);
      
      localr.setVersion(str5);
      localr.setLocalSize(l1);
      localr.setSize(l2);
      localr.setLocalPath(str6);
      
      localr.setIndex(index);
      localr.setIsSheng(bool);
      localr.setCompleteCode(j);
      localr.setCode(str7);
      
      localr.state = state;
      
      return new DTInfo(localr);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return localx;
  }
  
  public ContentValues a()
  {
    ContentValues localContentValues = null;
    try
    {
      if (this.dtInfo == null) {
        return null;
      }
      localContentValues = new ContentValues();
      localContentValues.put(OfflineDBCreator.TITLE, this.dtInfo.b());
      localContentValues.put(OfflineDBCreator.URL, this.dtInfo.c());
      localContentValues.put(OfflineDBCreator.ADCODE, this.dtInfo.d());
      localContentValues.put(OfflineDBCreator.FILE_NAME, this.dtInfo.e());
      
      localContentValues.put(OfflineDBCreator.VERSION, this.dtInfo.getVersion());
      localContentValues.put(OfflineDBCreator.LOCAL_LENGTH, Long.valueOf(this.dtInfo.g()));
      localContentValues.put(OfflineDBCreator.REMOTE_LENGTH, Long.valueOf(this.dtInfo.h()));
      localContentValues.put(OfflineDBCreator.LOCAL_PATH, this.dtInfo.i());
      
      localContentValues.put(OfflineDBCreator.M_INDEX, Integer.valueOf(this.dtInfo.j()));
      localContentValues.put(OfflineDBCreator.IS_PROVINCE, Integer.valueOf(this.dtInfo.k() ? 1 : 0));
      localContentValues.put(OfflineDBCreator.M_COMPLETE_CODE, Integer.valueOf(this.dtInfo.l()));
      localContentValues.put(OfflineDBCreator.M_CITY_CODE, this.dtInfo.m());
      localContentValues.put(OfflineDBCreator.M_STATE, Integer.valueOf(this.dtInfo.n()));
      
      return localContentValues;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return localContentValues;
  }
  
  public String getTableName()
  {
    return OfflineDBCreator.UPDATE_ITEM;
  }
  
  public void setLogInfo(DTInfo paramx)
  {
    this.dtInfo = paramx;
  }
  
  public static String getAdcodeStr(String adcode)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(OfflineDBCreator.ADCODE);
    localStringBuilder.append("='");
    localStringBuilder.append(adcode);
    localStringBuilder.append("'");
    return localStringBuilder.toString();
  }
}
