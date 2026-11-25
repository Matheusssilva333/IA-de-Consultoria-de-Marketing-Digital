package com.mycompany.digitalconsultant.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utilitário de Criptografia de Alta Segurança (AES-256-GCM) para conformidade com a LGPD.
 * Proteção de dados confidenciais em repouso e trânsito interno.
 */
public final class CryptoUtils {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH_BIT = 256;

    // Chave mestra interna padrão caso não esteja configurada no ambiente
    private static final String DEFAULT_MASTER_KEY = "Sentinel-SAO-Secured-2026-MasterKey#Consultant";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CryptoUtils() {}

    private static String getMasterPassword() {
        String envKey = System.getenv("SECURITY_MASTER_KEY");
        return (envKey != null && !envKey.isBlank()) ? envKey : DEFAULT_MASTER_KEY;
    }

    private static SecretKey deriveKey(byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(getMasterPassword().toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BIT);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Cifra texto puro com AES-256-GCM gerando IV e Salt criptográficos aleatórios.
     * Retorna payload Base64 contendo [Salt (16 bytes) | IV (12 bytes) | Ciphertext + GCM Tag].
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            byte[] salt = new byte[SALT_LENGTH_BYTE];
            SECURE_RANDOM.nextBytes(salt);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            SECURE_RANDOM.nextBytes(iv);

            SecretKey secretKey = deriveKey(salt);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
            byteBuffer.put(salt);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new SecurityException("Erro ao criptografar dados confidenciais: " + e.getMessage(), e);
        }
    }

    /**
     * Decifra payload Base64 gerado por encrypt.
     */
    public static String decrypt(String cipherTextBase64) {
        if (cipherTextBase64 == null || cipherTextBase64.isEmpty()) {
            return cipherTextBase64;
        }
        try {
            byte[] decode = Base64.getDecoder().decode(cipherTextBase64);
            if (decode.length < SALT_LENGTH_BYTE + IV_LENGTH_BYTE) {
                throw new IllegalArgumentException("Payload criptografado inválido ou corrompido");
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(decode);

            byte[] salt = new byte[SALT_LENGTH_BYTE];
            byteBuffer.get(salt);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            SecretKey secretKey = deriveKey(salt);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException("Erro ao decifrar dados confidenciais: " + e.getMessage(), e);
        }
    }
}
