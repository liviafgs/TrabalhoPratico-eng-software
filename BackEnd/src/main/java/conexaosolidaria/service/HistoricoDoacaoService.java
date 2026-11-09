package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Estabelecimento;
import conexaosolidaria.model.HistoricoSolicitacao;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.HistoricoSolicitacaoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Consulta do historico de doacoes (RF-14, RN-11).
 * Estabelecimentos veem as solicitacoes das proprias ofertas, instituicoes veem as proprias
 * solicitacoes e o administrador ve todas.
 */
public class HistoricoDoacaoService {
    private final SolicitacaoOfertaRepository solicitacaoRepository;
    private final OfertaRepository ofertaRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final HistoricoSolicitacaoRepository historicoRepository;
    private final AccessControl accessControl;

    public HistoricoDoacaoService(SolicitacaoOfertaRepository solicitacaoRepository,
            OfertaRepository ofertaRepository, InstituicaoRepository instituicaoRepository,
            EstabelecimentoRepository estabelecimentoRepository,
            HistoricoSolicitacaoRepository historicoRepository, AccessControl accessControl) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.ofertaRepository = ofertaRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.historicoRepository = historicoRepository;
        this.accessControl = accessControl;
    }

    public List<HistoricoDoacaoItem> consultar(AuthenticatedUser user, HistoricoFiltros filtros) {
        accessControl.require(user, Permissao.CONSULTAR_HISTORICO_DOACOES);
        HistoricoFiltros filtrosSeguros = filtros == null ? HistoricoFiltros.vazios() : filtros;
        if (filtrosSeguros.de() != null && filtrosSeguros.ate() != null
                && filtrosSeguros.de().isAfter(filtrosSeguros.ate())) {
            throw new ApiException(400, "A data inicial nao pode ser posterior a data final.");
        }

        return solicitacaoRepository.findAll().stream()
                .map(this::montarItem)
                .flatMap(Optional::stream)
                .filter(item -> podeVer(user, item.solicitacao(), item.oferta()))
                .filter(item -> corresponde(item, filtrosSeguros))
                .sorted(Comparator.comparing(HistoricoDoacaoItem::atualizadoEm).reversed()
                        .thenComparing(item -> item.solicitacao().getId(), Comparator.reverseOrder()))
                .toList();
    }

    public List<HistoricoSolicitacao> linhaDoTempo(AuthenticatedUser user, long idSolicitacao) {
        accessControl.require(user, Permissao.CONSULTAR_HISTORICO_DOACOES);
        SolicitacaoOferta solicitacao = solicitacaoRepository.findById(idSolicitacao)
                .orElseThrow(() -> new ApiException(404, "Solicitacao nao encontrada."));
        Oferta oferta = ofertaRepository.findById(solicitacao.getIdOferta())
                .orElseThrow(() -> new ApiException(404, "Oferta nao encontrada."));
        if (!podeVer(user, solicitacao, oferta)) {
            throw new ApiException(403, "Voce nao tem acesso a esta solicitacao.");
        }
        return historicoRepository.findBySolicitacao(idSolicitacao);
    }

    private Optional<HistoricoDoacaoItem> montarItem(SolicitacaoOferta solicitacao) {
        return ofertaRepository.findById(solicitacao.getIdOferta()).map(oferta -> {
            String instituicao = instituicaoRepository.findByUsuarioId(solicitacao.getIdUsuarioInstituicao())
                    .map(Instituicao::getNome).orElse(null);
            String estabelecimento = estabelecimentoRepository.findByUsuarioId(oferta.getIdUsuarioDoador())
                    .map(Estabelecimento::getNome).orElse(null);
            return new HistoricoDoacaoItem(solicitacao, oferta, instituicao, estabelecimento,
                    ultimaMovimentacao(solicitacao));
        });
    }

    private LocalDateTime ultimaMovimentacao(SolicitacaoOferta solicitacao) {
        List<HistoricoSolicitacao> registros = historicoRepository.findBySolicitacao(solicitacao.getId());
        return registros.isEmpty()
                ? solicitacao.getCriadoEm()
                : registros.get(registros.size() - 1).registradoEm();
    }

    private boolean podeVer(AuthenticatedUser user, SolicitacaoOferta solicitacao, Oferta oferta) {
        PerfilUsuario perfil = user.perfil();
        if (perfil == PerfilUsuario.ADMINISTRADOR) {
            return true;
        }
        if (perfil == PerfilUsuario.ESTABELECIMENTO_DOADOR) {
            return oferta.getIdUsuarioDoador() == user.id();
        }
        return perfil == PerfilUsuario.INSTITUICAO_BENEFICIARIA
                && solicitacao.getIdUsuarioInstituicao() == user.id();
    }

    private boolean corresponde(HistoricoDoacaoItem item, HistoricoFiltros filtros) {
        if (filtros.status() != null && !filtros.status().isEmpty()
                && !filtros.status().contains(item.solicitacao().getStatus())) {
            return false;
        }
        LocalDate dataMovimentacao = item.atualizadoEm().toLocalDate();
        if (filtros.de() != null && dataMovimentacao.isBefore(filtros.de())) {
            return false;
        }
        if (filtros.ate() != null && dataMovimentacao.isAfter(filtros.ate())) {
            return false;
        }
        return BuscaTexto.contem(item.oferta().getAlimento(), filtros.alimento())
                && BuscaTexto.contem(item.instituicaoNome(), filtros.instituicao())
                && BuscaTexto.contem(item.estabelecimentoNome(), filtros.estabelecimento());
    }
}
