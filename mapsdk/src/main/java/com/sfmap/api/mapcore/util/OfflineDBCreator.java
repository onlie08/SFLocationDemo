package com.sfmap.api.mapcore.util;

import android.database.sqlite.SQLiteDatabase;

class OfflineDBCreator//y
  implements DBCreator
{
  static final String UPDATE_ITEM = "update_item";
  static final String UPDATE_ITEM_FILE = "update_item_file";
  static final String UPDATE_ITEM_DOWNLOAD_INFO = "update_item_download_info";
  static final String TITLE = "title";
  static final String URL = "url";
  static final String ADCODE = "mAdcode";
  static final String FILE_NAME = "fileName";
  static final String VERSION = "version";
  static final String LOCAL_LENGTH = "lLocalLength";
  static final String REMOTE_LENGTH = "lRemoteLength";
  static final String LOCAL_PATH = "localPath";
  static final String M_INDEX = "mIndex";
  static final String IS_PROVINCE = "isProvince";
  static final String M_COMPLETE_CODE = "mCompleteCode";
  static final String M_CITY_CODE = "mCityCode";
  static final String M_STATE = "mState";
  static final String M_ADCODE = "mAdcode";
  static final String FILE = "file";
  static final String M_ADCODE1 = "mAdcode";
  static final String FILE_LENGTH = "fileLength";
  static final String SPLITTER = "splitter";
  static final String START_POS = "startPos";
  static final String END_POS = "endPos";
  private static final String TABLE_UPDATE_ITEM = "CREATE TABLE IF NOT EXISTS " + UPDATE_ITEM + " (_id integer primary key autoincrement, " + TITLE + "  TEXT, " + URL + " TEXT," + ADCODE + " TEXT," + FILE_NAME + " TEXT," + VERSION + " TEXT," + LOCAL_LENGTH + " INTEGER," + REMOTE_LENGTH + " INTEGER," + LOCAL_PATH + " TEXT," + M_INDEX + " INTEGER," + IS_PROVINCE + " INTEGER NOT NULL," + M_COMPLETE_CODE + " INTEGER," + M_CITY_CODE + " TEXT," + M_STATE + " INTEGER," + " UNIQUE(" + ADCODE + "));";
  private static final String TALBE_UPDATE_ITEM_FILE = "CREATE TABLE IF NOT EXISTS " + UPDATE_ITEM_FILE + " (_id integer primary key autoincrement," + M_ADCODE + " TTEXT, " + FILE + " TEXT);";
  private static final String TABLE_UPDATE_ITEM_DOWNLOAD_INFO = "CREATE TABLE IF NOT EXISTS " + UPDATE_ITEM_DOWNLOAD_INFO + " (_id integer primary key autoincrement," + M_ADCODE1 + " TEXT," + FILE_LENGTH + " integer," + SPLITTER + " integer," + START_POS + " integer," + END_POS + " integer," + " UNIQUE(" + M_ADCODE1 + "));";
  private static OfflineDBCreator instance;
  
  public static synchronized OfflineDBCreator getInstance()
  {
    if (instance == null) {
      instance = new OfflineDBCreator();
    }
    return instance;
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    try
    {
      paramSQLiteDatabase.execSQL(TABLE_UPDATE_ITEM);
      paramSQLiteDatabase.execSQL(TALBE_UPDATE_ITEM_FILE);
      paramSQLiteDatabase.execSQL(TABLE_UPDATE_ITEM_DOWNLOAD_INFO);
    }
    catch (Throwable localThrowable)
    {
      SDKLogHandler.exception(localThrowable, "DB", "onCreate");
      localThrowable.printStackTrace();
    }
  }
  
  public String getDBFileName()
  {
    return "offlineDbV4.db";
  }
  
  public int c()
  {
    return 1;
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
}
