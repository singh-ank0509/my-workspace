//package com.uppcl.ewallet.pnb.services;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.Cipher;
//import java.security.KeyFactory;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Base64;
//
//@Service
//public class RSAService {
//
//    @Value("${bank.api.public-key}")
//    private String publicKeyStr;
//
//    @Value("${bank.api.private-key}")
//    private String privateKeyStr;
//
//    public String encryptAESKey(String aesKey) {
//        try {
//            byte[] pubKeyBytes = Base64.getDecoder().decode(publicKeyStr);
//            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKeyBytes));
//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//            byte[] encryptedKey = cipher.doFinal(Base64.getDecoder().decode(aesKey));
//            return Base64.getEncoder().encodeToString(encryptedKey);
//        } catch (Exception e) {
//            throw new RuntimeException("RSA encryption failed", e);
//        }
//    }
//
//    public String decryptAESKey(String encryptedAesKey) {
//        try {
//            byte[] privKeyBytes = Base64.getDecoder().decode(privateKeyStr);
//            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));
//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);
//            byte[] decryptedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedAesKey));
//            return Base64.getEncoder().encodeToString(decryptedKey);
//        } catch (Exception e) {
//            throw new RuntimeException("RSA decryption failed", e);
//        }
//    }
//}
