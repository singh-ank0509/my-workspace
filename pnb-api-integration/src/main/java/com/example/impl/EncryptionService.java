package com.example.impl;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.data.EncryptedPayload;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionService {

    private static final int AES_KEY_SIZE = 128;    
	private static final int IV_SIZE = 16;

  public static EncryptedPayload encrypt(String plainText, PublicKey rsaPublicKey) throws Exception {
  byte[] aesKeyBytes = new byte[AES_KEY_SIZE];
  SecureRandom random = new SecureRandom();
  random.nextBytes(aesKeyBytes);
  SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

  byte[] ivBytes = new byte[IV_SIZE];
  random.nextBytes(ivBytes);
  IvParameterSpec iv = new IvParameterSpec(ivBytes);

  Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
  aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
  byte[] encryptedDataBytes = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

  byte[] combined = new byte[IV_SIZE + encryptedDataBytes.length];
  System.arraycopy(ivBytes, 0, combined, 0, IV_SIZE);
  System.arraycopy(encryptedDataBytes, 0, combined, IV_SIZE, encryptedDataBytes.length);

  Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
  rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
  byte[] encryptedKeyBytes = rsaCipher.doFinal(aesKeyBytes);

  EncryptedPayload payload = new EncryptedPayload();
  payload.setEncryptedKey(Base64.getEncoder().encodeToString(encryptedKeyBytes));
  payload.setEncryptedData(Base64.getEncoder().encodeToString(combined));
  return payload;
}

public static String decrypt(EncryptedPayload payload, PrivateKey rsaPrivateKey) throws Exception {
  byte[] encryptedKeyBytes = Base64.getDecoder().decode(payload.getEncryptedKey());
  Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
  rsaCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
  byte[] aesKeyBytes = rsaCipher.doFinal(encryptedKeyBytes);
  SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

  byte[] combined = Base64.getDecoder().decode(payload.getEncryptedData());
  byte[] ivBytes = new byte[IV_SIZE];
  byte[] cipherText = new byte[combined.length - IV_SIZE];
  System.arraycopy(combined, 0, ivBytes, 0, IV_SIZE);
  System.arraycopy(combined, IV_SIZE, cipherText, 0, cipherText.length);

  Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
  aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(ivBytes));
  byte[] plainTextBytes = aesCipher.doFinal(cipherText);

  return new String(plainTextBytes, StandardCharsets.UTF_8);
}

//    public static void main(String[] args) {
//        try {
//        	String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4ZdnX3D4LUxgTZRYCU3eBEg6fix7LY1mxIt6njjsoF5JWQdTMEfWj2lSO1FGk2GqbrSkDFbZFtm51F+1IQ2DzTct8S1+cuRuM7XaEw3lEOXkg9pIYcl43OBnXAf2QlyqHKpuwFDx9D/V+epXVpjZGyAQhVzJBDfg8ekspF1Oie3tzo7K/ZmfpHNRzUJ1OKsY7Yj0OgLK/jgF48ewAselF0MZZkApT+ZRLL+qNPANahXR6FSETDZS57D/ZlPtuk+sUVJmy8yA+q/ET8VKOnVWhpUb9EMqNUDVDf+VzT+bFWXJ/osGIacamAqFhmLcWMNr+paT/F6Z0J3bccBt3CMbfQIDAQAB";
//            String base64Key = Base64.getEncoder().encodeToString(publicKey.getBytes());
//            String plain = "Hello from CBC!";
//            String encrypted = encrypt(plain, base64Key);
//            System.out.println("Encrypted: " + encrypted);
////        	String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCw4ayj/xKs5Pp9gnlkp+HMhh8LvCvUOG6h1/ERbx7G4jU4rLduCPxPVRQRdW4IxyHHbrWJ/tYy3imafQmyBSQ0TPAD11raCzuBEkUNakk3ExO9XM69ZNE6NQNITiCEV6LUdeLMf6Nf3AMMa5HYUqTCXM/4szThzHGbp9ymTbKXTiqY6VPHc5XEBLPvynJKEYTJ6rmY3gwODwtfHFkn5VV4ZvdUgyj6NnJSO8QGqAhcInQWG+QcxqjZFBpXqb+VwHe0PaCdg6fOdgyQO1UUnUC/sNsJcf7C34M9Uo7xfORHDT/Vh93bXiaZP/MSLkjlafoDhKDeU8NdUJg4qg8nEFyHAgMBAAECggEAVQ1e70iimUJNdHZ9mrnjKG5BlWn5BUXRZL8qKsDs4qM5BjR+y0lE0XzOdoqpy0y9YGFKp71DEBillqSTOeeg/gkUzDQMepY2GZ1IWOgHppTkxARRYN6OgNem6Wiv2BMvt8urr7ucOmbXjGxZJ9StZswsMKEzR3QAGOHBhrILsxbVQTE104kpbwFG4FfwyvvOA0wpqNyWVWGodo3lZn60QxFfHDPJl7L3U11xIMSbNt3BQa7weDHe8VY/X/YMTlp0ZuWgOTobkv/JVk2fqVABGJNGNXIx0Tw3sZ3FtpdFEsWUg8YdQ4f0zVVlo0tTKz91lKATNtAQAN2341ZGRf0qEQKBgQDb7g8t4mkHDURSXwCKlmMN8QOGzlPTiLwkUIF2oWHZEeMwW+N2qcXbMMSbrrsas4qLzIIimPU8iVx/MEmOyrJZcT6NtgILlgDWhOOXDQIRqrOO1xZvf2PlAnos2eDwje7VXeHzktTbpAPvEF/HwwKTFFDsOYPG6Arqj/spyFwkuQKBgQDN5DFtnLd2CoIr2u8/kVR2angTWj61xW1vhB5ZlyAm8ydvOSJoTdHIAgdlk+NgBv+UhWvjfrax+lxUz0FZ4ysEFaJAk5pMsHcgJGuzzXW/NO21zE4pX59ztk3t8AXavAemsW88FUQqhquR0NA2+iuroIsskNWNZoexZ0lgzFprPwKBgDb/mHIsDYHC8pP6yALgStMgs3YRwOrEZfBa5ZVKFFMZYwwj3oU9PYpr7q7UClxKC9qcAOsv/xvtaeEwrSR1UhaF+FOt8WL+VyVORC/xA9RxMMgGx8iIPe32KaFvtw3RIKng+XFVBcALiyRMnvcwsEMhsH7yfx7NxnUUHoY7YbeZAoGBAMPip2QyBho4RUCUO6uvCTzgbv16js9B1qySdo2mSuDD1nDrWuEBqOUxiPgLi/iAsAkOJ2d51zfdiprl1LdAelyYeJYX2GW+PphGDnIyuCQCp2afyn1yjLH6cIpAIlHJMz5U8g+n7ALJvdItaOvleEcBp2fvJD4znkYajqWZxyzhAoGAbTI0EbHlFb8F9wYdKQBxk/a+zvya3ZUjBSnVCx25JBNwPF5s1FdTrajHsg+yTSdAcYrqi+F8dY0ZMxiAle2nAAZBgbHnVqJTi2Tnw7kG4jyxZpmftrvYo4ar72G6ILxDoVcOaCRb7LqYv7qBD/BMWilrAwoWwMRA2JVIfzOT1Us=";
//            String decrypted = decrypt(encrypted, Base64.getEncoder().encodeToString(publicKey.getBytes()));
//            System.out.println("Decrypted: " + decrypted);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
