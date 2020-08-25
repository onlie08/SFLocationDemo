/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfmap.api.location.client.bean;

/**
 *
 * @author joseph
 */
public class ByteUtils {

    public static byte[] long2Bytes(long value) {
        byte[] rs = new byte[8];
        //
        rs[0] = (byte) (value & 0xFF);
        rs[1] = (byte) (value >> 8 & 0xFF);
        rs[2] = (byte) (value >> 16 & 0xFF);
        rs[3] = (byte) (value >> 24 & 0xFF);
        rs[4] = (byte) (value >> 32 & 0xFF);
        rs[5] = (byte) (value >> 40 & 0xFF);
        rs[6] = (byte) (value >> 48 & 0xFF);
        rs[7] = (byte) (value >> 56 & 0xFF);
        //
        return rs;
    }

    public static long bytes2Long(byte[] bytes) {
        if (bytes != null) {
            //
            long rs = (((long) (bytes[7] & 0xff) << 56)
                    | ((long) (bytes[6] & 0xff) << 48)
                    | ((long) (bytes[5] & 0xff) << 40)
                    | ((long) (bytes[4] & 0xff) << 32)
                    | ((long) (bytes[3] & 0xff) << 24)
                    | ((long) (bytes[2] & 0xff) << 16)
                    | ((long) (bytes[1] & 0xff) << 8)
                    | ((long) (bytes[0] & 0xff)));
            return rs;
        }
        return 0;
    }

    public static byte[] twoInt2Bytes(int i1, int i2) {
        byte[] rs = new byte[8];
        //
        rs[0] = (byte) (i1 & 0xFF);
        rs[1] = (byte) (i1 >> 8 & 0xFF);
        rs[2] = (byte) (i1 >> 16 & 0xFF);
        rs[3] = (byte) (i1 >> 24 & 0xFF);
        rs[4] = (byte) (i2 & 0xFF);
        rs[5] = (byte) (i2 >> 8 & 0xFF);
        rs[6] = (byte) (i2 >> 16 & 0xFF);
        rs[7] = (byte) (i2 >> 24 & 0xFF);
        //
        return rs;
    }

    public static int[] bytes2TowInt(byte[] bytes) {
        //
        //java.io.DataInput di;
        //di.readShort();
        int i1 = (((bytes[3] & 0xff) << 24)
                | ((bytes[2] & 0xff) << 16)
                | ((bytes[1] & 0xff) << 8)
                | (bytes[0] & 0xff));
        int i2 = (((bytes[7] & 0xff) << 24)
                | ((bytes[6] & 0xff) << 16)
                | ((bytes[5] & 0xff) << 8)
                | (bytes[4] & 0xff));
        return new int[]{i1, i2};
    }

    public static int bytes2Int(byte[] bytes, int offset) {
        if (bytes.length - offset >= 4) {
            int i1 = (((bytes[offset + 3] & 0xff) << 24)
                    | ((bytes[offset + 2] & 0xff) << 16)
                    | ((bytes[offset + 1] & 0xff) << 8)
                    | (bytes[offset] & 0xff));
            return i1;
        }
        return -1;
    }

    public static short bytes2Short(byte[] bytes, int offset) {
        if (bytes.length - offset >= 2) {
            short i1 = (short) (((bytes[offset + 1] & 0xff) << 8)
                    | (bytes[offset] & 0xff));
            return i1;
        }
        return -1;
    }

    public static byte[] getBytes(int i1, int i2, short s3) {
        byte[] rs = new byte[10];
        //
        rs[0] = (byte) (i1 & 0xFF);
        rs[1] = (byte) (i1 >> 8 & 0xFF);
        rs[2] = (byte) (i1 >> 16 & 0xFF);
        rs[3] = (byte) (i1 >> 24 & 0xFF);
        rs[4] = (byte) (i2 & 0xFF);
        rs[5] = (byte) (i2 >> 8 & 0xFF);
        rs[6] = (byte) (i2 >> 16 & 0xFF);
        rs[7] = (byte) (i2 >> 24 & 0xFF);
        //
        rs[8] = (byte) (s3 & 0xFF);
        rs[9] = (byte) (s3 >> 8 & 0xFF);
        return rs;
    }

    public static void main(String[] args) {
        long lmax = Long.MAX_VALUE - 100;
        System.out.println(lmax);
        byte[] bytes = ByteUtils.long2Bytes(lmax);
        long lmax2 = ByteUtils.bytes2Long(bytes);
        System.out.println(lmax2);
        //
        int imax1 = Integer.MAX_VALUE - 101;
        int imax2 = Integer.MAX_VALUE - 102;
        byte[] bytes2 = ByteUtils.twoInt2Bytes(imax1, imax2);
        int[] i2 = ByteUtils.bytes2TowInt(bytes2);
        System.out.println(imax1);
        System.out.println(i2[0]);
        System.out.println(imax2);
        System.out.println(i2[1]);
    }
}
