package com.mycompany.digitalconsultant.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Gestão de senhas e autenticação segura com PBKDF2WithHmacSHA256 e Salt por usuário.
 * Mitigação contra Rainbow Tables, Dicionários e Timing Attacks.
 */
public final class PasswordSecurity {

    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");

    private PasswordSecurity() {}

    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String saltBase64) {
        if (password == null || saltBase64 == null) {
            throw new IllegalArgumentException("Senha e salt não podem ser nulos");
        }
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new SecurityException("Falha na geração de hash criptográfico: " + e.getMessage(), e);
        }
    }

    public static boolean verifyPassword(String rawPassword, String expectedHashBase64, String saltBase64) {
        if (rawPassword == null || expectedHashBase64 == null || saltBase64 == null) {
            return false;
        }
        String calculatedHash = hashPassword(rawPassword, saltBase64);
        byte[] calculatedBytes = calculatedHash.getBytes();
        byte[] expectedBytes = expectedHashBase64.getBytes();
        // Comparação em tempo constante para evitar Timing Attacks
        return MessageDigest.isEqual(calculatedBytes, expectedBytes);
    }

    /**
     * Valida política mínima de segurança de senha:
     * - Mínimo 8 caracteres
     * - Letra maiúscula, minúscula e número
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return UPPERCASE_PATTERN.matcher(password).find() &&
               LOWERCASE_PATTERN.matcher(password).find() &&
               DIGIT_PATTERN.matcher(password).find();
    }
}
