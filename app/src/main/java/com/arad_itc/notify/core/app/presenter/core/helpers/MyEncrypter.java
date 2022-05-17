package com.arad_itc.notify.core.app.presenter.core.helpers;
/*
 * @PoliteCoder97
 * */

import android.util.Base64;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MyEncrypter {

    private final static int READ_WRITE_BLOCK_BUFFER = 1024 * 1024;
    //  private final static int READ_WRITE_BLOCK_BUFFER = 1024 ;
    private final static String ALGO_FILE_ENCRYPTOR = "AES/CBC/PKCS5Padding";
    private final static String ALGO_SECRET_KEY = "AES";

    public static String encryptToFile(String keyStr, String specStr, String textToEncrypt)
            throws IOException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(specStr.getBytes("UTF-8"));
        SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), ALGO_SECRET_KEY);
        Cipher c = Cipher.getInstance(ALGO_FILE_ENCRYPTOR);
        c.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        return Base64.encodeToString(
                c.doFinal(textToEncrypt.getBytes()), Base64.DEFAULT);
    }

    public static String decryptToFile(String keyStr, String specStr, String encrypted)
            throws IOException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(specStr.getBytes("UTF-8"));

        SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), ALGO_SECRET_KEY);

        Cipher c = Cipher.getInstance(ALGO_FILE_ENCRYPTOR);
        c.init(Cipher.DECRYPT_MODE, keySpec, iv);
//      return new String(c.doFinal(Base64.decode(encryptedText)));

        return new String(c.doFinal(Base64.decode(encrypted,
                Base64.DEFAULT)));
    }

}
