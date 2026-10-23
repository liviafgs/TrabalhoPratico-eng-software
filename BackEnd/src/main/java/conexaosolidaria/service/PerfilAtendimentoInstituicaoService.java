package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.PerfilAtendimentoInstituicao;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.PerfilAtendimentoInstituicaoRepository;

import java.time.LocalDateTime;

public class PerfilAtendimentoInstituicaoService {
    private final PerfilAtendimentoInstituicaoRepository perfilRepository;
    private final InstituicaoRepository instituicaoRepository;

    public PerfilAtendimentoInstituicaoService(PerfilAtendimentoInstituicaoRepository perfilRepository,
            InstituicaoRepository instituicaoRepository) {
        this.perfilRepository = perfilRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    public PerfilAtendimentoInstituicao cadastrar(AuthenticatedUser user, long idInstituicao, int numeroBeneficiarios,
            int capacidadeAtendimento, String publicoAtendido, String frequenciaAtendimento) {
        validarUsuario(user);
        if (!instituicaoRepository.existsById(idInstituicao)) {
            throw new ApiException(404, "Instituicao nao encontrada.");
        }
        if (user.perfil() != PerfilUsuario.ADMINISTRADOR
                && user.id() != instituicaoRepository.findById(idInstituicao).orElseThrow().getIdUsuario()) {
            throw new ApiException(403, "Voce nao pode alterar o perfil de atendimento desta instituicao.");
        }
        validarTexto(publicoAtendido, "Publico atendido");
        validarTexto(frequenciaAtendimento, "Frequencia de atendimento");
        if (numeroBeneficiarios < 0 || capacidadeAtendimento < 0) {
            throw new ApiException(400, "Numero de beneficiarios e capacidade devem ser maiores ou iguais a zero.");
        }
        if (perfilRepository.existsByInstituicaoId(idInstituicao)) {
            return perfilRepository.findByInstituicaoId(idInstituicao).orElseThrow();
        }

        return perfilRepository.save(new PerfilAtendimentoInstituicao(
                0,
                idInstituicao,
                numeroBeneficiarios,
                capacidadeAtendimento,
                publicoAtendido.trim(),
                frequenciaAtendimento.trim(),
                LocalDateTime.now()));
    }

    public PerfilAtendimentoInstituicao consultarPorInstituicao(long idInstituicao) {
        return perfilRepository.findByInstituicaoId(idInstituicao)
                .orElseThrow(
                        () -> new ApiException(404, "Perfil de atendimento nao encontrado para esta instituicao."));
    }

    private void validarUsuario(AuthenticatedUser user) {
        if (user == null) {
            throw new ApiException(401, "Usuario nao autenticado.");
        }
        if (user.perfil() != PerfilUsuario.INSTITUICAO_BENEFICIARIA && user.perfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new ApiException(403, "Usuario sem permissao para cadastrar perfil de atendimento.");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ApiException(400, campo + " e obrigatorio.");
        }
    }
}
