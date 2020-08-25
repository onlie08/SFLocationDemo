package com.sfmap.api.maps.offlinemap;

import android.os.Parcel;
import android.os.Parcelable;

final class CityCreator
  implements Parcelable.Creator<City>
{
  public City createFromParcel(Parcel paramParcel)
  {
    return new City(paramParcel);
  }
  
  public City[] newArray(int paramInt)
  {
    return new City[paramInt];
  }
}
