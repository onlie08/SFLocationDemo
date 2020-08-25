package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;

final class ProvinceCreator
  implements Parcelable.Creator<Province>
{
  public Province createFromParcel(Parcel paramParcel)
  {
    return new Province(paramParcel);
  }
  
  public Province[] newArray(int paramInt)
  {
    return new Province[paramInt];
  }
}
