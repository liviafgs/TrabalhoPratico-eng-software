package main.java.conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.StatusSolicitacaoOferta;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;

import java.time.LocalDateTime;

public class SolicitacaoOfertaService {
    private final OfertaRepository ofertaRepository;
    private final SolicitacaoOfertaRepository solicitacaoRepository;
    private final AccessControl accessControl;

    public SolicitacaoOfertaService(OfertaRepository ofertaRepository,
            SolicitacaoOfertaRepository solicitacaoRepository, AccessControl accessControl) {
        this.ofertaRepository = ofertaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.accessControl = accessControl;
    }

    public SolicitacaoOferta solicitar(AuthenticatedUser user, long idOferta, double quantidade) {
        accessControl.require(user, Permissao.SOLICITAR_ALIMENTO);
        Oferta oferta = buscarOferta(idOferta);
        validarOfertaDisponivel(oferta);

        if (quantidade <= 0) {
            throw new ApiException(400, "Quantidade solicitada deve ser maior que zero.");
        }

        double disponivel = quantidadeDisponivel(oferta);
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

    public SolicitacaoOferta confirmarRetirada(AuthenticatedUser user, long idSolicitacao) {
        accessControl.require(user, Permissao.CONFIRMAR_RETIRADA_OFERTA);
        SolicitacaoOferta solicitacao = buscarSolicitacao(idSolicitacao);
        Oferta oferta = buscarOferta(solicitacao.getIdOferta());

        if (oferta.getIdUsuarioDoador() != user.id()
                && user.perfil() != conexaosolidaria.model.PerfilUsuario.ADMINISTRADOR) {
            throw new ApiException(403, "Somente o doador da oferta pode confirmar a retirada.");
        }
        if (solicitacao.getStatus() != StatusSolicitacaoOferta.SOLICITADA) {
            throw new ApiException(409, "A solicitacao nao esta pendente.");
        }

        return solicitacaoRepository.save(solicitacao.comStatus(StatusSolicitacaoOferta.RETIRADA));
    }

    public SolicitacaoOferta cancelar(AuthenticatedUser user, long idSolicitacao) {
        accessControl.require(user, Permissao.CANCELAR_SOLICITACAO_OFERTA);
        SolicitacaoOferta solicitacao = buscarSolicitacao(idSolicitacao);
        if (solicitacao.getIdUsuarioInstituicao() != user.id()
                && user.perfil() != conexaosolidaria.model.PerfilUsuario.ADMINISTRADOR) {
            throw new ApiException(403, "Somente a instituicao solicitante pode cancelar a solicitacao.");
        }
        if (solicitacao.getStatus() != StatusSolicitacaoOferta.SOLICITADA) {
            throw new ApiException(409, "A solicitacao nao esta pendente.");
        }

        return solicitacaoRepository.save(solicitacao.comStatus(StatusSolicitacaoOferta.CANCELADA));
    }

    public double quantidadeDisponivel(Oferta oferta) {
        double solicitada = solicitacaoRepository.findAll().stream()
                .filter(item -> item.getIdOferta() == oferta.getId())
                .filter(item -> item.getStatus() == StatusSolicitacaoOferta.SOLICITADA
                        || item.getStatus() == StatusSolicitacaoOferta.RETIRADA)
                .mapToDouble(SolicitacaoOferta::getQuantidade)
                .sum();
        return Math.max(0, oferta.getQuantidade() - solicitada);
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
        if (quantidadeDisponivel(oferta) <= 0) {
            throw new ApiException(409, "A oferta nao possui quantidade disponivel.");
        }
    }
}
