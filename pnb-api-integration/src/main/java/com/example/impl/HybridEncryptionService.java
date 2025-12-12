package com.example.impl;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.example.data.EncryptedPayload;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class HybridEncryptionService {

    private static final int AES_KEY_SIZE = 128;    
    private static final int GCM_IV_SIZE = 16;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String RSA_ALGORITHM = "RSA";
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
//    private final Environment env;
	public PrivateKey privateKey;
	public PublicKey publicKey;
	private final String keyLocation = "keys/";

//    public HybridEncryptionService(Environment env) {
//        this.env = env;
//    }

//    @PostConstruct
//    public void init() throws Exception {
//        this.privateKey = getPrivateKeyFromBase64(env.getProperty("private.key"));
//        this.publicKey = getPublicKeyFromBase64(env.getProperty("public.key"));
//    }
    
    @PostConstruct
    public void init() throws Exception {
        this.privateKey = getPrivateKeyFromBase64(keyLocation + "genx_private_key.pem");
        this.publicKey = getPublicKeyFromBase64(keyLocation + "pnb_public_key.pem");
    }

    public static PublicKey getPublicKeyFromBase64(String publicKeyPath) throws Exception {
    	String pem = new String(Files.readAllBytes(new File(publicKeyPath).toPath()));
        String base64PublicKey = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = kf.generatePublic(spec);
        return publicKey;
    }

    public static PrivateKey getPrivateKeyFromBase64(String privateKeyPath) throws Exception {
    	String pem = new String(Files.readAllBytes(new File(privateKeyPath).toPath()));
        String base64PrivateKey = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = kf.generatePrivate(spec);
        return privateKey;
    }

    public static EncryptedPayload encrypt(String plainText, PublicKey rsaPublicKey) throws Exception {
    	String keyStr = generateNewKey();
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

        byte[] ivBytes = new byte[GCM_IV_SIZE];
        SECURE_RANDOM.nextBytes(ivBytes);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes);

        Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
        byte[] encryptedData = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[ivBytes.length + encryptedData.length];
        System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
        System.arraycopy(encryptedData, 0, combined, ivBytes.length, encryptedData.length);
        
        RSAKeyParameters rsaKeyParameters = (RSAKeyParameters) PublicKeyFactory.createKey(rsaPublicKey.getEncoded());
        AsymmetricBlockCipher rsaCipher = new OAEPEncoding(
                new RSABlindedEngine(),
                new org.bouncycastle.crypto.digests.SHA256Digest(),
                new org.bouncycastle.crypto.digests.SHA256Digest(),
                null
        );

        rsaCipher.init(true, rsaKeyParameters);

        byte[] inputBytes = keyStr.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedKeyBytes = rsaCipher.processBlock(inputBytes, 0, inputBytes.length);

        EncryptedPayload payload = new EncryptedPayload();
        payload.setEncryptedKey(Base64.getEncoder().encodeToString(encryptedKeyBytes));
        payload.setEncryptedData(Base64.getEncoder().encodeToString(combined));
        return payload;
    }
    
    public static String generateNewKey()
    {
        try
        {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(AES_KEY_SIZE);
            SecretKey key = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        }
        catch (Exception ex)
        {
            System.err.println("Error in Generate Key " + ex.getMessage());
            return null;
        }
    }
    
    public static String decrypt(EncryptedPayload payload, PrivateKey rsaPrivateKey) throws Exception {
        byte[] encryptedKeyBytes = Base64.getDecoder().decode(payload.getEncryptedKey());

        RSAKeyParameters rsaPrivateKeyParameters =
                (RSAKeyParameters) PrivateKeyFactory.createKey(rsaPrivateKey.getEncoded());

        AsymmetricBlockCipher rsaCipher = new OAEPEncoding(
                new RSABlindedEngine(),
                new org.bouncycastle.crypto.digests.SHA256Digest(),
                new org.bouncycastle.crypto.digests.SHA256Digest(),
                null
        );
        rsaCipher.init(false, rsaPrivateKeyParameters);
        byte[] decryptedKeyBytes = rsaCipher.processBlock(encryptedKeyBytes, 0, encryptedKeyBytes.length);

        SecretKey aesKey = new SecretKeySpec(Base64.getDecoder().decode(new String(decryptedKeyBytes, StandardCharsets.UTF_8)), ALGORITHM);

        byte[] combined = Base64.getDecoder().decode(payload.getEncryptedData());
        byte[] iv = new byte[GCM_IV_SIZE];
        byte[] ciphertext = new byte[combined.length - GCM_IV_SIZE];

        System.arraycopy(combined, 0, iv, 0, GCM_IV_SIZE);
        System.arraycopy(combined, GCM_IV_SIZE, ciphertext, 0, ciphertext.length);

        Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

        byte[] plainTextBytes = aesCipher.doFinal(ciphertext);
        return new String(plainTextBytes, StandardCharsets.UTF_8);
    }
}

