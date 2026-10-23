package conexaosolidaria.service;

import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.Usuario;
import conexaosolidaria.repository.UsuarioRepository;
import conexaosolidaria.security.PasswordHasher;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    public Usuario cadastrar(String nome, String email, String senha, String perfilInformado) {
        validarTexto(nome, "Nome");
        validarEmail(email);

        if (usuarioRepository.existsByEmail(email)) {
            throw new ApiException(409, "Ja existe usuario cadastrado com este e-mail.");
        }

        PerfilUsuario perfil = PerfilUsuario.from(perfilInformado);
        Usuario usuario = new Usuario(
                0,
                nome.trim(),
                email.trim().toLowerCase(),
                passwordHasher.hash(senha),
                perfil,
                LocalDateTime.now()
        );

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Usuario::getId))
                .toList();
    }

    private void validarEmail(String email) {
        validarTexto(email, "E-mail");
        if (!email.contains("@") || !email.contains(".")) {
            throw new ApiException(400, "E-mail invalido.");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ApiException(400, campo + " e obrigatorio.");
        }
    }
}
