package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Usuario;
import conexaosolidaria.repository.UsuarioRepository;
import conexaosolidaria.security.PasswordHasher;
import conexaosolidaria.security.TokenService;

public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public String login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(401, "E-mail ou senha invalidos."));

        if (!passwordHasher.matches(senha, usuario.getSenhaHash())) {
            throw new ApiException(401, "E-mail ou senha invalidos.");
        }

        return tokenService.createToken(usuario);
    }

    public AuthenticatedUser authenticate(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        return tokenService.findUserByToken(token)
                .orElseThrow(() -> new ApiException(401, "Usuario nao autenticado."));
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException(401, "Informe o token no cabecalho Authorization: Bearer <token>.");
        }

        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
