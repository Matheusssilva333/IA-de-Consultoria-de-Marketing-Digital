package Security;

import com.mycompany.digitalconsultant.security.CryptoUtils;
import com.mycompany.digitalconsultant.security.PasswordSecurity;
import com.mycompany.digitalconsultant.security.SessionManager;

/**
 * Fachada de compatibilidade para a arquitetura de segurança industrial.
 */
public class Security {

    public static String hash(String rawPassword, String salt) {
        return PasswordSecurity.hashPassword(rawPassword, salt);
    }

    public static boolean verify(String rawPassword, String expectedHash, String salt) {
        return PasswordSecurity.verifyPassword(rawPassword, expectedHash, salt);
    }

    public static String encrypt(String plainText) {
        return CryptoUtils.encrypt(plainText);
    }

    public static String decrypt(String cipherText) {
        return CryptoUtils.decrypt(cipherText);
    }

    public static SessionManager sessions() {
        return SessionManager.getInstance();
    }
}
