package com.datamodel.util;

import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.UUID;

public class PasswordUtil {

    public static final int HASH_ITERATIONS = 2;

    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static String encryptPassword(String password, String salt) {
        return new Md5Hash(password, salt, HASH_ITERATIONS).toHex();
    }

    public static boolean verifyPassword(String rawPassword, String salt, String encryptedPassword) {
        String encrypted = encryptPassword(rawPassword, salt);
        return encrypted.equals(encryptedPassword);
    }

    public static void main(String[] args) {
        String salt = "abc123";
        String password = "admin123";
        String encrypted = encryptPassword(password, salt);
        System.out.println("Salt: " + salt);
        System.out.println("Password: " + password);
        System.out.println("Encrypted: " + encrypted);
    }
}
