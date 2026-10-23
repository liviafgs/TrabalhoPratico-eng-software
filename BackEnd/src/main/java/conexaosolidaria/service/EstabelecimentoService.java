package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Estabelecimento;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.repository.EstabelecimentoRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class EstabelecimentoService {
    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository) {
        this(estabelecimentoRepository, null);
    }

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository, Object ignored) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    public Estabelecimento cadastrar(AuthenticatedUser user, String nome, String cnpj, String endereco,
            String contato) {
        validarUsuario(user, PerfilUsuario.ESTABELECIMENTO_DOADOR);
        validarTexto(nome, "Nome do estabelecimento");
        validarCnpj(cnpj);
        validarTexto(endereco, "Endereco");
        validarTexto(contato, "Contato");

        if (estabelecimentoRepository.existsByUsuario(user.id())) {
            throw new ApiException(409, "Este usuario ja possui um estabelecimento cadastrado.");
        }

        if (estabelecimentoRepository.existsByCnpj(cnpj)) {
            throw new ApiException(409, "Ja existe um estabelecimento cadastrado com este CNPJ.");
        }

        return estabelecimentoRepository.save(new Estabelecimento(
                0,
                user.id(),
                nome.trim(),
                normalizeCnpj(cnpj),
                endereco.trim(),
                contato.trim(),
                LocalDateTime.now()));
    }

    public Estabelecimento consultarPorUsuario(long idUsuario) {
        return estabelecimentoRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new ApiException(404, "Estabelecimento nao encontrado para este usuario."));
    }

    public Estabelecimento atualizar(AuthenticatedUser user, String nome, String cnpj, String endereco,
            String contato) {
        Estabelecimento atual = consultarPorUsuario(user.id());
        if (user.perfil() != PerfilUsuario.ADMINISTRADOR && user.id() != atual.getIdUsuario()) {
            throw new ApiException(403, "Voce nao pode alterar este estabelecimento.");
        }

        validarTexto(nome, "Nome do estabelecimento");
        validarCnpj(cnpj);
        validarTexto(endereco, "Endereco");
        validarTexto(contato, "Contato");

        return estabelecimentoRepository.save(new Estabelecimento(
                atual.getId(),
                atual.getIdUsuario(),
                nome.trim(),
                normalizeCnpj(cnpj),
                endereco.trim(),
                contato.trim(),
                atual.getCriadoEm()));
    }

    public List<Estabelecimento> listarTodos() {
        return estabelecimentoRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Estabelecimento::getId))
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
