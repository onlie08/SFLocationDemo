package com.sfmap.api.maps.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 包含图片瓦块信息类。调用TileProvider 方法返回Tile 对象。
 */
public final class Tile
  implements Parcelable
{
  public static final Parcelable.Creator<Tile> CREATOR = new Parcelable.Creator<Tile>() {

    @Override
    public Tile createFromParcel(Parcel source) {
      int i = source.readInt();
      int j = source.readInt();
      int k = source.readInt();
      byte[] arrayOfByte = source.createByteArray();
      return new Tile(i, j, k, arrayOfByte);
    }

    @Override
    public Tile[] newArray(int size) {
      return new Tile[size];
    }
  };
  private final int a;
    /**
     * 编码的图片像素宽度，单位像素。
     */
  public final int width;
    /**
     * 编码的图片像素高度，单位像素
     */
  public final int height;
    /**
     * 包含图片数据的字节数组，这个图片可以通过调用类BitmapFactory的decodeByteArray(byte[], int, int)方法来创建。
     */
  public final byte[] data;
  
  private Tile(int paramInt1, int width, int height, byte[] paramArrayOfByte)
  {
    this.a = paramInt1;
    this.width = width;
    this.height = height;
    this.data = paramArrayOfByte;
  }

    /**
     * 构造函数。
     * @param width - 编码的图片像素宽度，单位像素。
     * @param height - 编码的图片像素高度，单位像素。
     * @param data - 包含图片数据的字节数组，这个图片可以通过调用类BitmapFactory的decodeByteArray(byte[], int, int)方法来创建。
     */
  public Tile(int width, int height, byte[] data)
  {
    this(1, width, height, data);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.a);
    paramParcel.writeInt(this.width);
    paramParcel.writeInt(this.height);
    paramParcel.writeByteArray(this.data);
  }
}
