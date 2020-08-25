package com.sfmap.api.services.core;

import java.io.UnsupportedEncodingException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * Des加解密类
 */
public class DesUtil {
    private String a1 = "乏乐久乎乍乁乐丐丐丗乓乐";
    private String strAlgorithm = "DESede";
    private Cipher cipherEncode = null;
    private Cipher cipherDecode = null;
    private static volatile DesUtil desUtil;

    public static DesUtil getInstance() {
        if (desUtil == null) {
            synchronized (DesUtil.class) {
                if (desUtil == null) {
                    desUtil = new DesUtil();
                }
            }
        }
        return desUtil;
    }

    public DesUtil() {
        byte[] a2 = getString(a1).getBytes();
        KeySpec keySpec = null;
        SecretKeyFactory secretKeyFactory = null;
        SecretKey secretKey = null;
        try {
            //设置密钥
            a2 = getString(a1).getBytes();
            keySpec = new DESedeKeySpec(a2);
            secretKeyFactory = SecretKeyFactory.getInstance(strAlgorithm);
            secretKey = secretKeyFactory.generateSecret(keySpec);
            cipherEncode = Cipher.getInstance(strAlgorithm);
            cipherEncode.init(Cipher.ENCRYPT_MODE, secretKey);
            cipherDecode = Cipher.getInstance(strAlgorithm);
            cipherDecode.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a2 = null;
            keySpec = null;
            secretKeyFactory = null;
            secretKey = null;
        }
    }

    /**
     * 进行DES加密
     *
     * @param str 输入字符串
     * @return String
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encrypt(String str) throws IllegalBlockSizeException, BadPaddingException {
        if (str == null || str.length() == 0) {
            return str;
        }
        String strResult = null;
        strResult = bytes2hex(cipherEncode.doFinal(str.getBytes()));
        return strResult;
    }

    /**
     * 进行DES加密
     *
     * @param sb
     * @return 无
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public void encrypt(StringBuilder sb) throws IllegalBlockSizeException, BadPaddingException {
        if (sb == null || sb.length() == 0) {
            return;
        }
        byte[] baInput = null;
        baInput = sb.toString().getBytes();
        sb.delete(0, sb.length());
        sb.append(bytes2hex(cipherEncode.doFinal(baInput)));
    }

    /**
     * 进行DES解密
     *
     * @param strData 输入数据
     * @return strEncode 字符编码
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String strData, String strEncode) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        if (strData == null || strData.length() == 0) {
            return null;
        }
        String strResult = null;
        byte[] ba = hex2bytes(strData);
        ba = cipherDecode.doFinal(ba);
        strResult = new String(ba, strEncode);
        return strResult;
    }

    /**
     *
     * @param ba
     * @return
     */
    private String bytes2hex(byte[] ba) {
        StringBuilder sb = new StringBuilder();
        for (byte b : ba) {
            String str = Integer.toHexString(b & 0XFF);
            if (str.length() == 1) {
                sb.append(String.format("0%s", str));
            } else {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    private byte[] hex2bytes(String str) {
        if (str == null || str.length() == 0 || str.length() % 2 == 1) {
            return null;
        }
        byte[] ba = null;
        try {
            ba = new byte[str.length() / 2];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i += 2) {
                sb.delete(0, sb.length());
                sb.append("0X");
                sb.append(str.substring(i, i + 2));
                String strDecode = sb.toString();
                ba[i / 2] = (byte) Integer.decode(strDecode).intValue();
            }
            sb = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            str = null;
        }
        return ba;
    }

    public String getString(String mesg){
        String result = mesg+"乁乓丄七习丁丘丘丘丘丘丘";
        char[] c = result.toCharArray();
        for(int i=0;i<c.length;i++){
            c[i] = (char)(c[i]^20000);
        }
        return new String(c);
    }

}