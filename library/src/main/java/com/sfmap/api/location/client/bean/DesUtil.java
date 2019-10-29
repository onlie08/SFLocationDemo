package com.sfmap.api.location.client.bean;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import android.os.Build;

import com.sfmap.api.location.BuildConfig;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * Des加解密类
 */
public class DesUtil {
    private String key = "openmap007spas$#@!888888";
    private String strAlgorithm = "DESede";
    private Cipher cipherEncode = null;
    private Cipher cipherDecode = null;

    public DesUtil() {
        SecureRandom secureRandom = null;
        byte[] baKey = key.getBytes();
        KeySpec keySpec = null;
        SecretKeyFactory secretKeyFactory = null;
        SecretKey secretKey = null;
        try {
            secureRandom = new SecureRandom();
            //设置密钥
            baKey = Utils.strKey.getBytes();
            keySpec = new DESedeKeySpec(baKey);
            secretKeyFactory = SecretKeyFactory.getInstance(strAlgorithm);
            secretKey = secretKeyFactory.generateSecret(keySpec);
            cipherEncode = Cipher.getInstance(strAlgorithm);
            cipherEncode.init(Cipher.ENCRYPT_MODE, secretKey, secureRandom);
            cipherDecode = Cipher.getInstance(strAlgorithm);
            cipherDecode.init(Cipher.DECRYPT_MODE, secretKey, secureRandom);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            secureRandom = null;
            baKey = null;
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
     */
    public String encrypt(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        String strResult = null;
        try {
            strResult = bytes2hex(cipherEncode.doFinal(str.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResult;
    }

    /**
     * 进行DES加密
     *
     * @param sb
     * @return 无
     */
    public void encrypt(StringBuilder sb) {
        if (sb == null || sb.length() == 0) {
            return;
        }
        byte[] baInput = null;
        try {
            baInput = sb.toString().getBytes();
            sb.delete(0, sb.length());
            sb.append(bytes2hex(cipherEncode.doFinal(baInput)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baInput = null;
        }
    }

    /**
     * 进行DES解密
     *
     * @param strData 输入数据
     * @return strEncode 字符编码
     */
    public String decrypt(String strData, String strEncode) {
        if (strData == null || strData.length() == 0) {
            return null;
        }
        String strResult = null;
        try {
            byte[] ba = hex2bytes(strData);
            ba = cipherDecode.doFinal(ba);
            strResult = new String(ba, strEncode);
        } catch (Exception e) {
            if(BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
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
}
