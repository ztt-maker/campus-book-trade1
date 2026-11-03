package com.campus.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密工具类
 * 数据库中不保存明文密码，注册时加密，登录时把输入密码加密后再比对
 */
public class MD5Util {

    /**
     * 对字符串做 MD5 加密，返回 32 位小写十六进制字符串
     */
    public static String md5(String text) {
        if (text == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append("0"); // 不足两位补 0
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // JDK 一定支持 MD5，理论上不会走到这里
            throw new RuntimeException("MD5 加密失败", e);
        }
    }
}
