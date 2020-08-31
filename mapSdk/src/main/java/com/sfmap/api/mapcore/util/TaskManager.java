package com.sfmap.api.mapcore.util;

import android.content.Context;
import com.sfmap.api.maps.MapController;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class TaskManager//q
{
  private static TaskManager instance;
  private ThreadPool b;
  private LinkedHashMap<String, ThreadTask> taskList = new LinkedHashMap<String, ThreadTask>();
  
  public static TaskManager getInstance(int nThreads)
  {
    return getInstance(true, nThreads);
  }
  
  private static synchronized TaskManager getInstance(boolean paramBoolean, int nThreads)
  {
    try
    {
      if (instance == null) {
        instance = new TaskManager(paramBoolean, nThreads);
      } else if ((paramBoolean) && (instance.b == null)) {
        instance.b = ThreadPool.getInstance(nThreads);
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return instance;
  }
  
  private TaskManager(boolean paramBoolean, int nThreads)
  {
    try
    {
      if (paramBoolean) {
        this.b = ThreadPool.getInstance(nThreads);
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  public void a()
  {
    synchronized (this.taskList)
    {
      if (this.taskList.size() < 1) {
        return;
      }
      Iterator<Entry<String,ThreadTask>> localIterator = this.taskList.entrySet().iterator();
      while (localIterator.hasNext())
      {
        //Map.Entry localEntry = (Map.Entry)localIterator.next();
    	//Object localObject1 = localEntry.getKey();
        Entry<String,ThreadTask> localEntry = (Entry<String,ThreadTask>)localIterator.next();
        String localObject1 = localEntry.getKey();
        
        OfflineMapDownloadTask localm = (OfflineMapDownloadTask)localEntry.getValue();
        localm.stopTask();
      }
      this.taskList.clear();
    }
  }
  
  public void stopTask(TaskItem taskItem)
  {
    synchronized (this.taskList)
    {
      OfflineMapDownloadTask task = (OfflineMapDownloadTask)this.taskList.get(taskItem.getMyUrl());
      if (task == null)
      {
        Utility.b("stop task,task  is null" + taskItem.getMyUrl());
        return;
      }
      task.stopTask();
    }
  }
  
  public void addTask(TaskItem taskItem, Context context, MapController map)
    throws IMMapCoreException
  {
    if (this.b == null) {
      Utility.b("threadpool is null ");
    }
    if (!this.taskList.containsKey(taskItem.getMyUrl()))
    {
      OfflineMapDownloadTask downloadTask = new OfflineMapDownloadTask(taskItem, context.getApplicationContext(), map);
      synchronized (this.taskList)
      {
        Utility.b("tasks put task " + taskItem.getMyUrl());
        this.taskList.put(taskItem.getMyUrl(), downloadTask);
      }
    }else{
      OfflineMapDownloadTask downloadTask = (OfflineMapDownloadTask)this.taskList.get(taskItem.getMyUrl());
      downloadTask.canDownload();
    }

    this.b.addTask((ThreadTask)this.taskList.get(taskItem.getMyUrl()));
  }
  
  public void b()
  {
    a();
    ThreadPool.onDestroy();
    this.b = null;
    instance = null;
  }
  
  public void removeTask(TaskItem paramp)
  {
    OfflineMapDownloadTask downloadTask = (OfflineMapDownloadTask)this.taskList.get(paramp.getMyUrl());
    if (downloadTask != null)
    {
      synchronized (this.taskList)
      {
        downloadTask.clear();
        this.taskList.remove(paramp.getMyUrl());
      }
      Utility.b("task finish remove task" + paramp.getMyUrl());
    }
    else
    {
      Utility.b(
              "task finish : by stop  had been removed" + paramp.getMyUrl());
    }
  }
  
  private boolean d = true;
}
