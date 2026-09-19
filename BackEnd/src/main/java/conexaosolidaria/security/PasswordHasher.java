package conexaosolidaria.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final int SALT_SIZE = 16;
    private final SecureRandom secureRandom = new SecureRandom();

    public String hash(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("A senha deve possuir pelo menos 6 caracteres.");
        }

        byte[] salt = new byte[SALT_SIZE];
        secureRandom.nextBytes(salt);
        byte[] digest = digest(salt, password);

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(digest);
    }

    public boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null || !storedHash.contains(":")) {
            return false;
        }

        String[] parts = storedHash.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedDigest = Base64.getDecoder().decode(parts[1]);
        byte[] actualDigest = digest(salt, rawPassword);

        return MessageDigest.isEqual(expectedDigest, actualDigest);
    }

    private byte[] digest(byte[] salt, String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt);
            return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponivel.", e);
        }
    }
}
