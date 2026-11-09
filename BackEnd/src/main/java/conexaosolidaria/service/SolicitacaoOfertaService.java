package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.StatusInstituicao;
import conexaosolidaria.model.StatusSolicitacaoOferta;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;

import java.time.LocalDateTime;

public class SolicitacaoOfertaService {
    private final OfertaRepository ofertaRepository;
    private final SolicitacaoOfertaRepository solicitacaoRepository;
    private final AccessControl accessControl;
    private final DisponibilidadeOfertaService disponibilidade;
    private final InstituicaoRepository instituicaoRepository;

    public SolicitacaoOfertaService(OfertaRepository ofertaRepository,
            SolicitacaoOfertaRepository solicitacaoRepository, AccessControl accessControl,
            DisponibilidadeOfertaService disponibilidade, InstituicaoRepository instituicaoRepository) {
        this.ofertaRepository = ofertaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.accessControl = accessControl;
        this.disponibilidade = disponibilidade;
        this.instituicaoRepository = instituicaoRepository;
    }

    public SolicitacaoOferta solicitar(AuthenticatedUser user, long idOferta, double quantidade) {
        accessControl.require(user, Permissao.SOLICITAR_ALIMENTO);
        exigirInstituicaoHabilitada(user);
        Oferta oferta = buscarOferta(idOferta);
        validarOfertaDisponivel(oferta);

        if (quantidade <= 0) {
            throw new ApiException(400, "Quantidade solicitada deve ser maior que zero.");
        }

        double disponivel = disponibilidade.quantidadeDisponivel(oferta);
        if (quantidade > disponivel) {
            throw new ApiException(409, "Quantidade solicitada maior que a quantidade disponivel.");
        }

        return solicitacaoRepository.save(new SolicitacaoOferta(
                0,
                idOferta,
                user.id(),
                quantidade,
                StatusSolicitacaoOferta.SOLICITADA,
                LocalDateTime.now()));
    }

    public SolicitacaoOferta aceitar(AuthenticatedUser user, long idSolicitacao) {
        SolicitacaoOferta solicitacao = carregarParaResposta(user, idSolicitacao);
        exigirTransicao(solicitacao, StatusSolicitacaoOferta.ACEITA);
        return solicitacaoRepository.save(solicitacao.comStatus(StatusSolicitacaoOferta.ACEITA));
    }

    public SolicitacaoOferta recusar(AuthenticatedUser user, long idSolicitacao) {
        SolicitacaoOferta solicitacao = carregarParaResposta(user, idSolicitacao);
        exigirTransicao(solicitacao, StatusSolicitacaoOferta.RECUSADA);
        return solicitacaoRepository.save(solicitacao.comStatus(StatusSolicitacaoOferta.RECUSADA));
    }

    public SolicitacaoOferta agendar(AuthenticatedUser user, long idSolicitacao, LocalDateTime dataHora) {
        SolicitacaoOferta solicitacao = carregarParaResposta(user, idSolicitacao);
        exigirTransicao(solicitacao, StatusSolicitacaoOferta.AGENDADA);
        validarAgendamento(buscarOferta(solicitacao.getIdOferta()), dataHora);
        return solicitacaoRepository.save(solicitacao.agendadaPara(dataHora));
    }

    public SolicitacaoOferta confirmarRetirada(AuthenticatedUser user, long idSolicitacao) {
        accessControl.require(user, Permissao.CONFIRMAR_RETIRADA_OFERTA);
        SolicitacaoOferta solicitacao = buscarSolicitacao(idSolicitacao);
        Oferta oferta = buscarOferta(solicitacao.getIdOferta());

        accessControl.requireDonoOuAdministrador(user, oferta.getIdUsuarioDoador(),
                "Somente o doador da oferta pode confirmar a retirada.");
        exigirTransicao(solicitacao, StatusSolicitacaoOferta.RETIRADA);

        return solicitacaoRepository.save(solicitacao.comStatus(StatusSolicitacaoOferta.RETIRADA));
    }

    public SolicitacaoOferta concluir(AuthenticatedUser user, long idSolicitacao) {
        accessControl.require(user, Permissao.CONCLUIR_DOACAO);
        SolicitacaoOferta solicitacao = buscarSolicitacao(idSolicitacao);

        accessControl.requireDonoOuAdministrador(user, solicitacao.getIdUsuarioInstituicao(),
                "Somente a instituicao solicitante pode concluir a doacao.");
        exigirTransicao(solicitacao, StatusSolicitacaoOferta.CONCLUIDA);

        return solicitacaoRepository.save(solicitacao.comStatus(StatusSolicitacaoOferta.CONCLUIDA));
    }

    public SolicitacaoOferta cancelar(AuthenticatedUser user, long idSolicitacao) {
        accessControl.require(user, Permissao.CANCELAR_SOLICITACAO_OFERTA);
        SolicitacaoOferta solicitacao = buscarSolicitacao(idSolicitacao);
        accessControl.requireDonoOuAdministrador(user, solicitacao.getIdUsuarioInstituicao(),
                "Somente a instituicao solicitante pode cancelar a solicitacao.");
        exigirTransicao(solicitacao, StatusSolicitacaoOferta.CANCELADA);

        return solicitacaoRepository.save(solicitacao.comStatus(StatusSolicitacaoOferta.CANCELADA));
    }

    /** Aceitar, recusar e agendar cabem ao doador da oferta (ou ao administrador). */
    private SolicitacaoOferta carregarParaResposta(AuthenticatedUser user, long idSolicitacao) {
        accessControl.require(user, Permissao.RESPONDER_SOLICITACAO_OFERTA);
        SolicitacaoOferta solicitacao = buscarSolicitacao(idSolicitacao);
        Oferta oferta = buscarOferta(solicitacao.getIdOferta());
        accessControl.requireDonoOuAdministrador(user, oferta.getIdUsuarioDoador(),
                "Somente o doador da oferta pode responder a solicitacao.");
        return solicitacao;
    }

    private void exigirTransicao(SolicitacaoOferta solicitacao, StatusSolicitacaoOferta novoStatus) {
        if (!solicitacao.getStatus().podeMudarPara(novoStatus)) {
            throw new ApiException(409, "Nao e possivel alterar a solicitacao de "
                    + solicitacao.getStatus() + " para " + novoStatus + ".");
        }
    }

    private void exigirInstituicaoHabilitada(AuthenticatedUser user) {
        boolean habilitada = instituicaoRepository.findByUsuarioId(user.id())
                .map(Instituicao::getStatus)
                .filter(status -> status == StatusInstituicao.HABILITADA)
                .isPresent();
        if (!habilitada) {
            throw new ApiException(403, "Instituicao nao cadastrada ou nao habilitada para solicitar alimentos.");
        }
    }

    private void validarAgendamento(Oferta oferta, LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new ApiException(400, "Data e hora da retirada sao obrigatorias.");
        }
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new ApiException(400, "A retirada nao pode ser agendada no passado.");
        }
        if (oferta.getRetiradaAte() != null && dataHora.toLocalDate().isAfter(oferta.getRetiradaAte())) {
            throw new ApiException(400, "A retirada deve ser agendada ate " + oferta.getRetiradaAte() + ".");
        }
        if (oferta.getValidade() != null && dataHora.toLocalDate().isAfter(oferta.getValidade())) {
            throw new ApiException(400, "A retirada deve ser agendada antes do vencimento da oferta ("
                    + oferta.getValidade() + ").");
        }
    }

    private Oferta buscarOferta(long idOferta) {
        return ofertaRepository.findById(idOferta)
                .orElseThrow(() -> new ApiException(404, "Oferta nao encontrada."));
    }

    private SolicitacaoOferta buscarSolicitacao(long idSolicitacao) {
        return solicitacaoRepository.findById(idSolicitacao)
                .orElseThrow(() -> new ApiException(404, "Solicitacao nao encontrada."));
    }

    private void validarOfertaDisponivel(Oferta oferta) {
        if (oferta.isExpirada()) {
            throw new ApiException(409, "A oferta esta expirada.");
        }
        if (disponibilidade.quantidadeDisponivel(oferta) <= 0) {
            throw new ApiException(409, "A oferta nao possui quantidade disponivel.");
        }
    }
}
