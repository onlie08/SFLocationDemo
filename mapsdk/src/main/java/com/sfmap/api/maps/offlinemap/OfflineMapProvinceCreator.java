package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;

final class OfflineMapProvinceCreator
  implements Parcelable.Creator<OfflineMapProvince>
{
  public OfflineMapProvince createFromParcel(Parcel paramParcel)
  {
    return new OfflineMapProvince(paramParcel);
  }
  
  public OfflineMapProvince[] newArray(int paramInt)
  {
    return new OfflineMapProvince[paramInt];
  }
}
