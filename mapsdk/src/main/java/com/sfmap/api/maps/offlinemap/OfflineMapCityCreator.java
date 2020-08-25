package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;

final class OfflineMapCityCreator
  implements Parcelable.Creator<OfflineMapCity>
{
  public OfflineMapCity createFromParcel(Parcel paramParcel)
  {
    return new OfflineMapCity(paramParcel);
  }
  
  public OfflineMapCity[] newArray(int paramInt)
  {
    return new OfflineMapCity[paramInt];
  }
}
