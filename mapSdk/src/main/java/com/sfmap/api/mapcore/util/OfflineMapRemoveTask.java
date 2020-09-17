package com.sfmap.api.mapcore.util;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class OfflineMapRemoveTask//n
{
  private Context a;
  
  public OfflineMapRemoveTask(Context paramContext)
  {
    this.a = paramContext;
  }
  
  public void a(CityObject paramg)
  {
    b(paramg);
  }
  
  private boolean b(CityObject cityObject)
  {
    if (cityObject != null)
    {
      boolean bool = a(cityObject.getAdcode(), this.a);
      if (bool)
      {
        cityObject.h();
      }
      else
      {
        cityObject.g();
        return false;
      }
      return bool;
    }
    return false;
  }
  
  private boolean a(String paramString, Context context)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    List<String> localList = OfflineDBOperation.getInstance(context).b(paramString);
    
    String rootPath = Util.getMapRoot(context);
    for (String str2 : localList) {
      try
      {
        File file = new File(rootPath + "data/vmap/" + str2);
        if (file.exists())
        {
          boolean res = Utility.deleteFiles(file);
          if (!res)
          {
            Utility.log("deleteDownload delete some thing wrong!");
            return false;
          }
        }
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
        return false;
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        return false;
      }
    }
    try
    {
      Utility.d(rootPath + "data/vmap/");
      Utility.a(paramString, context);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      return false;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return false;
    }
    return true;
  }
}
