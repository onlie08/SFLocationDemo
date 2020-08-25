package com.sfmap.api.mapcore.util;

class DTDownloadInfo//t
{
  private String adcode = "";
  private long fileLength = 0L;
  private int splitter = 0;
  private long startPos = 0L;
  private long endPos = 0L;
  
  public DTDownloadInfo() {}
  
  public DTDownloadInfo(String adcode, int fileLength, int splitter, int startPos, int endPos)
  {
    this.adcode = adcode;
    this.fileLength = fileLength;
    this.splitter = splitter;
    this.startPos = startPos;
    this.endPos = endPos;
  }
  
  public DTDownloadInfo(String adcode, long fileLength, int splitter, long startPos, long endPos)
  {
    this.adcode = adcode;
    this.fileLength = fileLength;
    this.splitter = splitter;
    this.startPos = startPos;
    this.endPos = endPos;
  }
  
  public long a(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return getStartPos();
    }
    return 0L;
  }
  
  public long b(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return getEndPos();
    }
    return 0L;
  }
  
  public String getAdcode()
  {
    return this.adcode;
  }
  
  public long getFileLength()
  {
    return this.fileLength;
  }
  
  public int getsplitter()
  {
    return this.splitter;
  }
  
  public long getStartPos()
  {
    return this.startPos;
  }
  
  public long getEndPos()
  {
    return this.endPos;
  }
}
