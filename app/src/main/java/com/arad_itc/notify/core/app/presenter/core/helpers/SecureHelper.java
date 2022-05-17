package com.arad_itc.notify.core.app.presenter.core.helpers;

import android.util.Base64;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class SecureHelper {
    public static final Integer KEY_ITERATION_COUNT = 256;
    public static final Integer KEY_LENGTH = 128;
    public static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final String SECRET_KEY_ALGORITHM = "AES";
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";


    public static String encrypt(String password, String seed,
                                 String textToEncrypt) throws GeneralSecurityException,
            IOException {
        Cipher cipher = getCipher(password, seed, Cipher.ENCRYPT_MODE);
        return Base64.encodeToString(
                cipher.doFinal(textToEncrypt.getBytes()), Base64.DEFAULT);
    }

    public static String decrypt(String password, String seed,
                                 String encrypted) throws GeneralSecurityException, IOException {
        Cipher cipher = getCipher(password, seed, Cipher.DECRYPT_MODE);
        return new String(cipher.doFinal(Base64.decode(encrypted,
                Base64.DEFAULT)));
    }

    public static String decryptData(String password, String seed,String text)throws Exception{

//        byte[] encryted_bytes = Base64.decode(text, Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        byte[] static_key = Arrays.copyOf(password.getBytes(),128);

        SecretKeySpec keySpec = new SecretKeySpec(static_key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(hexToBytes(seed));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decrypted = cipher.doFinal(text.getBytes());
        String result = new String(decrypted);
        return result;
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }

    }

    /**
     * Reference: http://nelenkov.blogspot.in/2012/04/using-password-based-encryption-on.html
     * NOTE: Using the seed as both Salt & IV, since we have no space to store the Salt & IV in the encrypted data
     */
    public static Cipher getCipher(String password, String seed, int mode)
            throws IOException, GeneralSecurityException {
        byte salt[] = seed.getBytes();
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                KEY_ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, SECRET_KEY_ALGORITHM);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        int cipherBlocSize = cipher.getBlockSize();
        byte[] iv = paddedByteArray(seed, cipherBlocSize);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(mode, key, ivSpec);
        return cipher;
    }

    public static byte[] paddedByteArray(String str, int size) {
        byte[] dst = new byte[size];
        byte[] src = str.getBytes();
        int length = str.length() > size ? size : str.length();
        System.arraycopy(src, 0, dst, 0, length);
        return dst;
    }
}
