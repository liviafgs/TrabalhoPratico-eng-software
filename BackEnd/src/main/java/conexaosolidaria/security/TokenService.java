package conexaosolidaria.security;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Usuario;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TokenService {
    private final SecureRandom secureRandom = new SecureRandom();
    private final ConcurrentMap<String, AuthenticatedUser> sessions = new ConcurrentHashMap<>();

    public String createToken(Usuario usuario) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        sessions.put(token, new AuthenticatedUser(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        ));

        return token;
    }

    public Optional<AuthenticatedUser> findUserByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(sessions.get(token.trim()));
    }
}
