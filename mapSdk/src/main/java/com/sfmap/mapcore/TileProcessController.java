package com.sfmap.mapcore;

import java.util.Hashtable;

class TileProcessController
{
  private Hashtable<String, TileCacheEntry> hashTable = new Hashtable<>();
  long initTimestamp;

  public void remove(String key)
  {
    this.hashTable.remove(key);
  }
  
  public boolean hasKey(String key)
  {
    return this.hashTable.get(key) != null;
  }
  
  public void put(String key)
  {
    this.hashTable.put(key, new TileCacheEntry(key, 0));
  }
  
  public void clear()
  {
    this.hashTable.clear();
  }
  
  public TileProcessController()
  {
    this.initTimestamp = System.currentTimeMillis();
  }

}
