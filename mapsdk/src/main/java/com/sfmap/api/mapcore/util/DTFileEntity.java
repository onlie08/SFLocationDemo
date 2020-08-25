package com.sfmap.api.mapcore.util;

import android.content.ContentValues;
import android.database.Cursor;

class DTFileEntity//v
  implements SQlEntity<DTFileInfo>
{
  private DTFileInfo a = null;
  
  //public w a(Cursor paramCursor)
  public DTFileInfo select(Cursor paramCursor) //原来的方法名为a，怀疑是反编译时特别对于模版的基类有错误。
  {
    DTFileInfo localw = null;
    try
    {
      String adcode = paramCursor.getString(paramCursor
        .getColumnIndex(OfflineDBCreator.M_ADCODE));
      String file = paramCursor.getString(paramCursor
        .getColumnIndex(OfflineDBCreator.FILE));
      
      return new DTFileInfo(adcode, file);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return localw;
  }
  
  public ContentValues a()
  {
    ContentValues localContentValues = null;
    try
    {
      if (this.a == null) {
        return null;
      }
      localContentValues = new ContentValues();
      localContentValues.put(OfflineDBCreator.M_ADCODE, this.a.getAdcode());
      localContentValues.put(OfflineDBCreator.FILE, this.a.getFile());
      
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
    return OfflineDBCreator.UPDATE_ITEM_FILE;
  }
  
  public void setLogInfo(DTFileInfo paramw)
  {
    this.a = paramw;
  }
  
  public static String adcodeStr(String adcode)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(OfflineDBCreator.M_ADCODE);
    localStringBuilder.append("='");
    localStringBuilder.append(adcode);
    localStringBuilder.append("'");
    return localStringBuilder.toString();
  }
  
  public static String b(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(OfflineDBCreator.M_ADCODE);
    localStringBuilder.append("='");
    localStringBuilder.append(paramString);
    localStringBuilder.append("'");
    return localStringBuilder.toString();
  }
}
