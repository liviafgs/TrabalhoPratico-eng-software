package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.NutricionistaResponsavel;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.NutricionistaRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class NutricionistaService {
    private final NutricionistaRepository nutricionistaRepository;
    private final InstituicaoRepository instituicaoRepository;

    public NutricionistaService(NutricionistaRepository nutricionistaRepository,
            InstituicaoRepository instituicaoRepository) {
        this(nutricionistaRepository, instituicaoRepository, null);
    }

    public NutricionistaService(NutricionistaRepository nutricionistaRepository,
            InstituicaoRepository instituicaoRepository, Object ignored) {
        this.nutricionistaRepository = nutricionistaRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    public NutricionistaResponsavel cadastrar(AuthenticatedUser user, long idInstituicao, String nome, String crn,
            String contato) {
        validarUsuario(user);
        validarTexto(nome, "Nome do nutricionista");
        validarTexto(crn, "CRN");
        validarTexto(contato, "Contato");

        if (!instituicaoRepository.existsById(idInstituicao)) {
            throw new ApiException(404, "Instituicao nao encontrada para vincular o nutricionista.");
        }

        if (user.perfil() == PerfilUsuario.INSTITUICAO_BENEFICIARIA
                && user.id() != instituicaoRepository.findById(idInstituicao).orElseThrow().getIdUsuario()) {
            throw new ApiException(403, "Voce nao pode associar nutricionista a outra instituicao.");
        }

        if (nutricionistaRepository.existsByInstituicao(idInstituicao)) {
            throw new ApiException(409, "Ja existe um nutricionista responsavel cadastrado para esta instituicao.");
        }

        return nutricionistaRepository.save(new NutricionistaResponsavel(
                0,
                user.id(),
                idInstituicao,
                nome.trim(),
                crn.trim(),
                contato.trim(),
                LocalDateTime.now()));
    }

    public NutricionistaResponsavel associar(AuthenticatedUser user, long idInstituicao, String nome, String crn,
            String contato) {
        return cadastrar(user, idInstituicao, nome, crn, contato);
    }

    public NutricionistaResponsavel consultarPorInstituicao(long idInstituicao) {
        return nutricionistaRepository.findByInstituicaoId(idInstituicao)
                .orElseThrow(
                        () -> new ApiException(404, "Nutricionista responsavel nao encontrado para esta instituicao."));
    }

    public List<NutricionistaResponsavel> listarTodos() {
        return nutricionistaRepository.findAll().stream()
                .sorted(Comparator.comparingLong(NutricionistaResponsavel::getId))
                .toList();
    }

    private void validarUsuario(AuthenticatedUser user) {
        if (user == null) {
            throw new ApiException(401, "Usuario nao autenticado.");
        }

        if (user.perfil() != PerfilUsuario.NUTRICIONISTA && user.perfil() != PerfilUsuario.ADMINISTRADOR
                && user.perfil() != PerfilUsuario.INSTITUICAO_BENEFICIARIA) {
            throw new ApiException(403, "Usuario com perfil invalido para cadastrar este cadastro.");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ApiException(400, campo + " e obrigatorio.");
        }
    }
}
