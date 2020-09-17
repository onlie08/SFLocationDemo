package com.sfmap.api.mapcore.util;

import android.content.ContentValues;
import android.database.Cursor;

class DTDownloadEntity
  implements SQlEntity<DTDownloadInfo>
{
  private DTDownloadInfo downloadInfo = null;
  
  //public t a(Cursor paramCursor)
  public DTDownloadInfo select(Cursor cursor) //原来的方法名为a，怀疑是反编译时特别对于模版的基类有错误。
  {
    DTDownloadInfo localt = null;
    try
    {
      String adcode = cursor.getString(cursor
        .getColumnIndex(OfflineDBCreator.M_ADCODE1));
      int fileLength = cursor.getInt(cursor
        .getColumnIndex(OfflineDBCreator.FILE_LENGTH));
      int splitter = cursor.getInt(cursor
        .getColumnIndex(OfflineDBCreator.SPLITTER));
      
      int startPos = cursor.getInt(cursor
        .getColumnIndex(OfflineDBCreator.START_POS));
      int endPos = cursor.getInt(cursor
        .getColumnIndex(OfflineDBCreator.END_POS));
      
      return new DTDownloadInfo(adcode, fileLength, splitter, startPos, endPos);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return localt;
  }
  
  public ContentValues a()
  {
    ContentValues localContentValues = null;
    try
    {
      if (this.downloadInfo == null) {
        return null;
      }
      localContentValues = new ContentValues();
      localContentValues.put(OfflineDBCreator.M_ADCODE1, this.downloadInfo
              .getAdcode());
      localContentValues.put(OfflineDBCreator.FILE_LENGTH,
        Long.valueOf(this.downloadInfo.getFileLength()));
      localContentValues.put(OfflineDBCreator.SPLITTER,
        Integer.valueOf(this.downloadInfo.getsplitter()));
      
      localContentValues.put(OfflineDBCreator.START_POS,
        Long.valueOf(this.downloadInfo.getStartPos()));
      
      localContentValues.put(OfflineDBCreator.END_POS,
        Long.valueOf(this.downloadInfo.getEndPos()));
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
    return OfflineDBCreator.UPDATE_ITEM_DOWNLOAD_INFO;
  }
  
  public void setLogInfo(DTDownloadInfo paramt)
  {
    this.downloadInfo = paramt;
  }
  
  public static String a(String adcode)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(OfflineDBCreator.M_ADCODE1);
    localStringBuilder.append("='");
    localStringBuilder.append(adcode);
    localStringBuilder.append("'");
    return localStringBuilder.toString();
  }
}
