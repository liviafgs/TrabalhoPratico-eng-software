package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.StatusInstituicao;
import conexaosolidaria.repository.InstituicaoRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class InstituicaoService {
    private final InstituicaoRepository instituicaoRepository;

    public InstituicaoService(InstituicaoRepository instituicaoRepository) {
        this(instituicaoRepository, null);
    }

    public InstituicaoService(InstituicaoRepository instituicaoRepository, Object ignored) {
        this.instituicaoRepository = instituicaoRepository;
    }

    public Instituicao cadastrar(AuthenticatedUser user, String nome, String cnpj, String endereco, String contato) {
        validarUsuario(user, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
        validarTexto(nome, "Nome da instituicao");
        validarCnpj(cnpj);
        validarTexto(endereco, "Endereco");
        validarTexto(contato, "Contato");

        if (instituicaoRepository.existsByUsuario(user.id())) {
            throw new ApiException(409, "Este usuario ja possui uma instituicao cadastrada.");
        }

        if (instituicaoRepository.existsByCnpj(cnpj)) {
            throw new ApiException(409, "Ja existe uma instituicao cadastrada com este CNPJ.");
        }

        return instituicaoRepository.save(new Instituicao(
                0,
                user.id(),
                nome.trim(),
                normalizeCnpj(cnpj),
                endereco.trim(),
                contato.trim(),
                StatusInstituicao.PENDENTE,
                LocalDateTime.now()));
    }

    public Instituicao consultarPorUsuario(long idUsuario) {
        return instituicaoRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new ApiException(404, "Instituicao nao encontrada para este usuario."));
    }

    public Instituicao atualizar(AuthenticatedUser user, String nome, String cnpj, String endereco, String contato) {
        Instituicao atual = consultarPorUsuario(user.id());
        if (user.perfil() != PerfilUsuario.ADMINISTRADOR && user.id() != atual.getIdUsuario()) {
            throw new ApiException(403, "Voce nao pode alterar esta instituicao.");
        }

        validarTexto(nome, "Nome da instituicao");
        validarCnpj(cnpj);
        validarTexto(endereco, "Endereco");
        validarTexto(contato, "Contato");

        return instituicaoRepository.save(new Instituicao(
                atual.getId(),
                atual.getIdUsuario(),
                nome.trim(),
                normalizeCnpj(cnpj),
                endereco.trim(),
                contato.trim(),
                atual.getStatus(),
                atual.getCriadoEm()));
    }

    public Instituicao habilitar(AuthenticatedUser user, boolean habilitar) {
        Instituicao instituicao = consultarPorUsuario(user.id());
        if (user.perfil() != PerfilUsuario.ADMINISTRADOR && user.perfil() != PerfilUsuario.INSTITUICAO_BENEFICIARIA) {
            throw new ApiException(403, "Usuario sem permissao para habilitar esta instituicao.");
        }

        StatusInstituicao novoStatus = habilitar ? StatusInstituicao.HABILITADA : StatusInstituicao.PENDENTE;
        Instituicao atualizada = instituicao.comStatus(novoStatus);
        return instituicaoRepository.save(atualizada);
    }

    public List<Instituicao> listarTodos() {
        return instituicaoRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Instituicao::getId))
                .toList();
    }

    private void validarUsuario(AuthenticatedUser user, PerfilUsuario perfilEsperado) {
        if (user == null) {
            throw new ApiException(401, "Usuario nao autenticado.");
        }

        if (user.perfil() != perfilEsperado && user.perfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new ApiException(403, "Usuario com perfil invalido para cadastrar este cadastro.");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ApiException(400, campo + " e obrigatorio.");
        }
    }

    private void validarCnpj(String cnpj) {
        String cnpjNormalizado = normalizeCnpj(cnpj);
        if (cnpjNormalizado.length() != 14 || !cnpjNormalizado.chars().allMatch(Character::isDigit)) {
            throw new ApiException(400, "CNPJ invalido.");
        }
    }

    private String normalizeCnpj(String cnpj) {
        return cnpj == null ? "" : cnpj.replaceAll("\\D", "");
    }
}
