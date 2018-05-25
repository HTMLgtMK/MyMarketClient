package com.gthncz.mymarketclient.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密算法帮助类
 *
 * Created by GT on 2018/5/25.
 */

public class DigestHelper {

    /**
     * md5 算法
     * @param s
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        StringBuffer buffer = new StringBuffer();
        byte[] bits = digest.digest();
        for(int i=0;i<bits.length;++i){
            int a = bits[i];
            if(a<0) a += 0x100;
            if(a<16) buffer.append("0");
            buffer.append(Integer.toHexString(a));
        }
        return buffer.toString();
    }

    /**
     * sha1 加密算法
     * @param s
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String sha1(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(s.getBytes());
        StringBuffer buffer = new StringBuffer();
        byte[] bits = digest.digest();
        for(int i=0;i<bits.length;++i){
            int a = bits[i];
            if(a<0) a += 0x100;
            if(a<16) buffer.append("0");
            buffer.append(Integer.toHexString(a));
        }
        return buffer.toString();
    }

}
