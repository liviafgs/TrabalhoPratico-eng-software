package conexaosolidaria.service;

import conexaosolidaria.dto.CadastroUsuarioRequest;
import conexaosolidaria.exception.CadastroUsuarioException;
import conexaosolidaria.model.Usuario;
import conexaosolidaria.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public class UsuarioService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrar(CadastroUsuarioRequest request) {
        validar(request);

        String emailNormalizado = request.getEmail().trim().toLowerCase();

        if (usuarioRepository.existePorEmail(emailNormalizado)) {
            throw new CadastroUsuarioException("Ja existe um usuario cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome().trim());
        usuario.setEmail(emailNormalizado);
        usuario.setSenha(BCrypt.hashpw(request.getSenha(), BCrypt.gensalt()));
        usuario.setTelefone(normalizarTelefone(request.getTelefone()));
        usuario.setTipoUsuario(request.getTipoUsuario());
        usuario.setAtivo(true);

        return usuarioRepository.salvar(usuario);
    }

    public boolean senhaConfere(String senhaInformada, String senhaCriptografada) {
        if (senhaInformada == null || senhaCriptografada == null || senhaCriptografada.isBlank()) {
            return false;
        }

        return BCrypt.checkpw(senhaInformada, senhaCriptografada);
    }

    private void validar(CadastroUsuarioRequest request) {
        if (request == null) {
            throw new CadastroUsuarioException("Dados do usuario nao foram informados.");
        }

        if (isBlank(request.getNome())) {
            throw new CadastroUsuarioException("Nome e obrigatorio.");
        }

        if (request.getNome().trim().length() > 120) {
            throw new CadastroUsuarioException("Nome deve ter no maximo 120 caracteres.");
        }

        if (isBlank(request.getEmail())) {
            throw new CadastroUsuarioException("E-mail e obrigatorio.");
        }

        String email = request.getEmail().trim();

        if (email.length() > 150 || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new CadastroUsuarioException("E-mail invalido.");
        }

        if (isBlank(request.getSenha())) {
            throw new CadastroUsuarioException("Senha e obrigatoria.");
        }

        if (request.getSenha().length() < TAMANHO_MINIMO_SENHA) {
            throw new CadastroUsuarioException("Senha deve ter pelo menos 6 caracteres.");
        }

        if (!isBlank(request.getTelefone()) && request.getTelefone().trim().length() > 20) {
            throw new CadastroUsuarioException("Telefone deve ter no maximo 20 caracteres.");
        }

        if (request.getTipoUsuario() == null) {
            throw new CadastroUsuarioException("Tipo de usuario e obrigatorio.");
        }
    }

    private String normalizarTelefone(String telefone) {
        if (isBlank(telefone)) {
            return null;
        }

        return telefone.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
