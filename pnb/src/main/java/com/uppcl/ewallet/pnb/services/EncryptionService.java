package com.uppcl.ewallet.pnb.services;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.springframework.stereotype.Service;

import com.uppcl.ewallet.pnb.util.CRMUniversalMsg;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Service
public class EncryptionService {

    public String generateAESKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public String encrypt(String data, String base64Key) {
        try {
            byte[] key = Base64.getDecoder().decode(base64Key);
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] combined = ByteBuffer.allocate(iv.length + encrypted.length).put(iv).put(encrypted).array();

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    public String decrypt(String encryptedData, String base64Key) {
        try {
            byte[] fullData = Base64.getDecoder().decode(encryptedData);
            byte[] iv = Arrays.copyOfRange(fullData, 0, 16);
            byte[] data = Arrays.copyOfRange(fullData, 16, fullData.length);
            byte[] key = Base64.getDecoder().decode(base64Key);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(data);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }
    
    
    private static final int AES_KEY_SIZE = 128;    
    private static final int GCM_IV_SIZE = 16;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String RSA_ALGORITHM = "RSA";
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
	public PrivateKey privateKey;
	public PublicKey publicKey;
	private final String keyLocation = "keys/";
	
	@PostConstruct
    public void init() throws Exception {
        this.privateKey = getPrivateKey(keyLocation + "genx_private_key.pem");
        this.publicKey = getPublicKey(keyLocation + "pnb_public_key.pem");
    }
	
	public static PublicKey getPublicKey(String publicKeyPath) throws Exception {
		try {
			String pem = new String(Files.readAllBytes(new File(publicKeyPath).toPath()));
			String base64PublicKey = pem.replace("-----BEGIN PUBLIC KEY-----", "")
										.replace("-----END PUBLIC KEY-----", "")
										.replaceAll("\\s+", "");
			byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
			PublicKey publicKey = kf.generatePublic(spec);
			return publicKey;
		} catch (Exception e) {
			throw new RuntimeException("Public Key generation failed", e);
		}
    }

    public static PrivateKey getPrivateKey(String privateKeyPath) throws Exception {
		try {
			String pem = new String(Files.readAllBytes(new File(privateKeyPath).toPath()));
			String base64PrivateKey = pem.replace("-----BEGIN PRIVATE KEY-----", "")
										 .replace("-----END PRIVATE KEY-----", "")
										 .replaceAll("\\s+", "");
			byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
			PrivateKey privateKey = kf.generatePrivate(spec);
			return privateKey;
		} catch (Exception e) {
			throw new RuntimeException("Private Key generation failed", e);
		}
    }
    
    public CRMUniversalMsg encrypt(String plainText, PublicKey rsaPublicKey) throws Exception {
    	try {
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

            CRMUniversalMsg payload = new CRMUniversalMsg();
            payload.setEncKey(Base64.getEncoder().encodeToString(encryptedKeyBytes));
            payload.setEncData(Base64.getEncoder().encodeToString(combined));
            return payload;
    	} catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }
    
	public static String generateNewKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			keyGenerator.init(AES_KEY_SIZE);
			SecretKey key = keyGenerator.generateKey();
			return Base64.getEncoder().encodeToString(key.getEncoded());
		} catch (Exception ex) {
			throw new RuntimeException("AES Key generate failed", ex);		}
	}
    
    public String decrypt(CRMUniversalMsg payload, PrivateKey rsaPrivateKey) throws Exception {
    	try {
    		byte[] encryptedKeyBytes = Base64.getDecoder().decode(payload.getEncKey());

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

            byte[] combined = Base64.getDecoder().decode(payload.getEncData());
            byte[] iv = new byte[GCM_IV_SIZE];
            byte[] ciphertext = new byte[combined.length - GCM_IV_SIZE];

            System.arraycopy(combined, 0, iv, 0, GCM_IV_SIZE);
            System.arraycopy(combined, GCM_IV_SIZE, ciphertext, 0, ciphertext.length);

            Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            byte[] plainTextBytes = aesCipher.doFinal(ciphertext);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
    	} catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }
}
