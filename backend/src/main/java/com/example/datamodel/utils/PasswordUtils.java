package com.example.datamodel.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用AES算法进行加密和解密
 *
 * @author DataModel Team
 */
public class PasswordUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    // 注意：生产环境应该从配置文件或环境变量中读取密钥，不要硬编码
    // 这里使用固定密钥仅用于演示，实际使用时应该使用安全的密钥管理方案
    // AES密钥：必须是16、24或32字节（128、192或256位）
    // 这里使用32字节（256位）的密钥
    private static final String SECRET_KEY_RAW = "DataModelSecretKey2024ABCDEFGHIJKL";
    
    /**
     * 获取AES密钥，确保长度正好是32字节
     */
    private static byte[] getSecretKey() {
        byte[] keyBytes = SECRET_KEY_RAW.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length == 32) {
            return keyBytes;
        } else if (keyBytes.length > 32) {
            // 如果长度超过32字节，截取前32字节
            byte[] result = new byte[32];
            System.arraycopy(keyBytes, 0, result, 0, 32);
            return result;
        } else {
            // 如果长度不足32字节，用0填充
            byte[] result = new byte[32];
            System.arraycopy(keyBytes, 0, result, 0, keyBytes.length);
            return result;
        }
    }

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码（Base64编码）
     */
    public static String encrypt(String password) {
        if (password == null || password.isEmpty()) {
            return password;
        }
        
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(getSecretKey(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 解密密码
     *
     * @param encryptedPassword 加密后的密码（Base64编码）
     * @return 明文密码
     */
    public static String decrypt(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return encryptedPassword;
        }
        
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(getSecretKey(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("密码解密失败", e);
        }
    }

    /**
     * 检查密码是否已加密（简单判断，根据前缀）
     * 如果以 "ENC:" 开头，则认为已加密
     *
     * @param password 密码
     * @return 是否已加密
     */
    public static boolean isEncrypted(String password) {
        return password != null && password.startsWith("ENC:");
    }

    /**
     * 移除加密前缀
     *
     * @param encryptedPassword 加密密码（带前缀）
     * @return 移除前缀后的密码
     */
    public static String removeEncryptionPrefix(String encryptedPassword) {
        if (encryptedPassword != null && encryptedPassword.startsWith("ENC:")) {
            return encryptedPassword.substring(4);
        }
        return encryptedPassword;
    }

    /**
     * 添加加密前缀
     *
     * @param encryptedPassword 加密密码
     * @return 带前缀的加密密码
     */
    public static String addEncryptionPrefix(String encryptedPassword) {
        if (encryptedPassword != null && !encryptedPassword.startsWith("ENC:")) {
            return "ENC:" + encryptedPassword;
        }
        return encryptedPassword;
    }
}

