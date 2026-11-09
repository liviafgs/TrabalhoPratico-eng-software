package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Estabelecimento;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.service.Correspondencia.NivelAtendimento;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Apresenta as ofertas e demandas compativeis (RF-16).
 * Estabelecimentos veem as demandas que podem receber suas ofertas; instituicoes veem as ofertas
 * que podem atender suas demandas; o administrador ve todas as combinacoes.
 */
public class OportunidadeService {
    private static final Map<String, Integer> ORDEM_PRIORIDADE = Map.of("ALTA", 0, "MEDIA", 1, "BAIXA", 2);

    private final OfertaRepository ofertaRepository;
    private final DemandaRepository demandaRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final CorrespondenciaService correspondenciaService;
    private final AccessControl accessControl;

    public OportunidadeService(OfertaRepository ofertaRepository, DemandaRepository demandaRepository,
            InstituicaoRepository instituicaoRepository, EstabelecimentoRepository estabelecimentoRepository,
            CorrespondenciaService correspondenciaService, AccessControl accessControl) {
        this.ofertaRepository = ofertaRepository;
        this.demandaRepository = demandaRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.correspondenciaService = correspondenciaService;
        this.accessControl = accessControl;
    }

    /**
     * Lista as oportunidades do usuario, das mais urgentes para as menos: prioridade da demanda,
     * atendimento total antes de parcial e prazo mais proximo.
     */
    public List<Oportunidade> consultar(AuthenticatedUser user, Long idOferta, Long idDemanda) {
        accessControl.require(user, Permissao.CONSULTAR_OPORTUNIDADES);

        List<Oferta> ofertas = ofertaRepository.findAll().stream()
                .filter(oferta -> user.perfil() != PerfilUsuario.ESTABELECIMENTO_DOADOR
                        || oferta.getIdUsuarioDoador() == user.id())
                .filter(oferta -> idOferta == null || oferta.getId() == idOferta)
                .toList();
        List<Demanda> demandas = demandaRepository.findAll().stream()
                .filter(demanda -> user.perfil() != PerfilUsuario.INSTITUICAO_BENEFICIARIA
                        || demanda.getIdUsuarioInstituicao() == user.id())
                .filter(demanda -> idDemanda == null || demanda.getId() == idDemanda)
                .toList();

        return ofertas.stream()
                .flatMap(oferta -> demandas.stream()
                        .map(demanda -> correspondenciaService.avaliar(oferta, demanda))
                        .flatMap(java.util.Optional::stream))
                .map(this::comNomes)
                .sorted(Comparator
                        .comparingInt((Oportunidade item) -> ORDEM_PRIORIDADE.getOrDefault(
                                item.correspondencia().demanda().getPrioridade(), 3))
                        .thenComparing(item -> item.correspondencia().nivel() == NivelAtendimento.TOTAL ? 0 : 1)
                        .thenComparing(item -> item.correspondencia().demanda().getPrazo())
                        .thenComparingLong(item -> item.correspondencia().oferta().getId()))
                .toList();
    }

    private Oportunidade comNomes(Correspondencia correspondencia) {
        String estabelecimento = estabelecimentoRepository
                .findByUsuarioId(correspondencia.oferta().getIdUsuarioDoador())
                .map(Estabelecimento::getNome).orElse(null);
        String instituicao = instituicaoRepository
                .findByUsuarioId(correspondencia.demanda().getIdUsuarioInstituicao())
                .map(Instituicao::getNome).orElse(null);
        return new Oportunidade(correspondencia, estabelecimento, instituicao);
    }
}
