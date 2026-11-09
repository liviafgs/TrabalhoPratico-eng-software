package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Estabelecimento;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.Notificacao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.TipoNotificacao;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.NotificacaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.security.AccessControl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Notificacoes aos usuarios (RF-23).
 * Os metodos "notificar..." sao ouvintes dos eventos do dominio: nao verificam permissao porque
 * quem os chama e o proprio sistema. Os demais metodos atendem a consulta do usuario.
 */
public class NotificacaoService {
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificacaoRepository notificacaoRepository;
    private final AccessControl accessControl;
    private final OfertaRepository ofertaRepository;
    private final DemandaRepository demandaRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final CorrespondenciaService correspondenciaService;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, AccessControl accessControl,
            OfertaRepository ofertaRepository, DemandaRepository demandaRepository,
            InstituicaoRepository instituicaoRepository, EstabelecimentoRepository estabelecimentoRepository,
            CorrespondenciaService correspondenciaService) {
        this.notificacaoRepository = notificacaoRepository;
        this.accessControl = accessControl;
        this.ofertaRepository = ofertaRepository;
        this.demandaRepository = demandaRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.correspondenciaService = correspondenciaService;
    }

    // ---- consulta do usuario (RF-23) ----

    /** Lista as notificacoes do usuario; lida = null traz todas, true so as lidas, false so as nao lidas. */
    public List<Notificacao> listar(AuthenticatedUser user, Boolean lida) {
        accessControl.require(user, Permissao.CONSULTAR_NOTIFICACOES);
        return notificacaoRepository.findByUsuario(user.id()).stream()
                .filter(notificacao -> lida == null || notificacao.isLida() == lida)
                .toList();
    }

    public long contarNaoLidas(AuthenticatedUser user) {
        accessControl.require(user, Permissao.CONSULTAR_NOTIFICACOES);
        return notificacaoRepository.findByUsuario(user.id()).stream()
                .filter(notificacao -> !notificacao.isLida())
                .count();
    }

    public Notificacao marcarComoLida(AuthenticatedUser user, long idNotificacao) {
        accessControl.require(user, Permissao.CONSULTAR_NOTIFICACOES);
        Notificacao notificacao = notificacaoRepository.findById(idNotificacao)
                .filter(item -> item.getIdUsuarioDestino() == user.id())
                .orElseThrow(() -> new ApiException(404, "Notificacao nao encontrada."));
        if (notificacao.isLida()) {
            return notificacao;
        }
        return notificacaoRepository.save(notificacao.comoLida(LocalDateTime.now()));
    }

    /** Marca todas as notificacoes pendentes do usuario como lidas e devolve quantas foram marcadas. */
    public int marcarTodasComoLidas(AuthenticatedUser user) {
        accessControl.require(user, Permissao.CONSULTAR_NOTIFICACOES);
        LocalDateTime agora = LocalDateTime.now();
        List<Notificacao> pendentes = notificacaoRepository.findByUsuario(user.id()).stream()
                .filter(notificacao -> !notificacao.isLida())
                .toList();
        pendentes.forEach(notificacao -> notificacaoRepository.save(notificacao.comoLida(agora)));
        return pendentes.size();
    }

    // ---- eventos do dominio (RF-23) ----

    public void notificarMudancaStatus(MudancaStatus mudanca) {
        SolicitacaoOferta solicitacao = mudanca.solicitacao();
        Optional<Oferta> oferta = ofertaRepository.findById(solicitacao.getIdOferta());
        if (oferta.isEmpty()) {
            return;
        }
        Oferta o = oferta.get();
        String descricao = formatar(solicitacao.getQuantidade()) + " " + o.getUnidadeMedida() + " de "
                + o.getAlimento();
        long doador = o.getIdUsuarioDoador();
        long instituicao = solicitacao.getIdUsuarioInstituicao();

        switch (solicitacao.getStatus()) {
            case SOLICITADA -> criar(doador, TipoNotificacao.SOLICITACAO_RECEBIDA, mudanca,
                    "A instituicao " + nomeInstituicao(instituicao) + " solicitou " + descricao + ".", o);
            case ACEITA -> criar(instituicao, TipoNotificacao.SOLICITACAO_ACEITA, mudanca,
                    nomeEstabelecimento(doador) + " aceitou sua solicitacao de " + descricao + ".", o);
            case RECUSADA -> criar(instituicao, TipoNotificacao.SOLICITACAO_RECUSADA, mudanca,
                    nomeEstabelecimento(doador) + " recusou sua solicitacao de " + descricao + ".", o);
            case AGENDADA -> criar(instituicao, TipoNotificacao.RETIRADA_AGENDADA, mudanca,
                    "Retirada de " + descricao + " agendada para "
                            + solicitacao.getRetiradaAgendadaPara().format(DATA_HORA)
                            + localDaRetirada(o) + ".", o);
            case RETIRADA -> criar(instituicao, TipoNotificacao.RETIRADA_CONFIRMADA, mudanca,
                    nomeEstabelecimento(doador) + " confirmou a retirada de " + descricao
                            + ". Confirme o recebimento para concluir a doacao.", o);
            case CONCLUIDA -> criar(doador, TipoNotificacao.DOACAO_CONCLUIDA, mudanca,
                    "A instituicao " + nomeInstituicao(instituicao) + " confirmou o recebimento de "
                            + descricao + ". Doacao concluida.", o);
            case CANCELADA -> criar(doador, TipoNotificacao.SOLICITACAO_CANCELADA, mudanca,
                    "A instituicao " + nomeInstituicao(instituicao) + " cancelou a solicitacao de "
                            + descricao + ".", o);
        }
    }

    /** Avisa, uma vez por instituicao, quando uma oferta nova atende alguma demanda dela. */
    public void notificarNovaOferta(Oferta oferta) {
        Set<Long> instituicoes = new LinkedHashSet<>();
        for (Demanda demanda : demandaRepository.findAll()) {
            if (correspondenciaService.avaliar(oferta, demanda).isPresent()) {
                instituicoes.add(demanda.getIdUsuarioInstituicao());
            }
        }
        String mensagem = "Nova oferta compativel com suas demandas: " + formatar(oferta.getQuantidade()) + " "
                + oferta.getUnidadeMedida() + " de " + oferta.getAlimento() + ", de "
                + nomeEstabelecimento(oferta.getIdUsuarioDoador()) + ".";
        instituicoes.forEach(idUsuario -> notificacaoRepository.save(new Notificacao(
                0, idUsuario, TipoNotificacao.NOVA_OPORTUNIDADE, mensagem, null, oferta.getId(), null,
                LocalDateTime.now(), null)));
    }

    /** Avisa, uma vez por estabelecimento, quando uma demanda nova pode ser atendida por oferta dele. */
    public void notificarNovaDemanda(Demanda demanda) {
        Set<Long> doadores = new LinkedHashSet<>();
        for (Oferta oferta : ofertaRepository.findAll()) {
            if (correspondenciaService.avaliar(oferta, demanda).isPresent()) {
                doadores.add(oferta.getIdUsuarioDoador());
            }
        }
        String mensagem = "Nova demanda compativel com suas ofertas: " + nomeInstituicao(demanda.getIdUsuarioInstituicao())
                + " precisa de " + formatar(demanda.getQuantidade()) + " " + demanda.getUnidadeMedida() + " de "
                + demanda.getAlimento() + " ate " + demanda.getPrazo() + ".";
        doadores.forEach(idUsuario -> notificacaoRepository.save(new Notificacao(
                0, idUsuario, TipoNotificacao.NOVA_OPORTUNIDADE, mensagem, null, null, demanda.getId(),
                LocalDateTime.now(), null)));
    }

    private void criar(long idUsuarioDestino, TipoNotificacao tipo, MudancaStatus mudanca, String mensagem,
            Oferta oferta) {
        if (idUsuarioDestino == mudanca.idUsuarioAutor()) {
            return;
        }
        notificacaoRepository.save(new Notificacao(
                0, idUsuarioDestino, tipo, mensagem, mudanca.solicitacao().getId(), oferta.getId(), null,
                LocalDateTime.now(), null));
    }

    private String localDaRetirada(Oferta oferta) {
        return oferta.getLocalRetirada() == null || oferta.getLocalRetirada().isBlank()
                ? ""
                : ". Local: " + oferta.getLocalRetirada();
    }

    private String nomeInstituicao(long idUsuario) {
        return instituicaoRepository.findByUsuarioId(idUsuario).map(Instituicao::getNome)
                .orElse("solicitante");
    }

    private String nomeEstabelecimento(long idUsuario) {
        return estabelecimentoRepository.findByUsuarioId(idUsuario).map(Estabelecimento::getNome)
                .orElse("O doador");
    }

    private static String formatar(double valor) {
        return BigDecimal.valueOf(valor).stripTrailingZeros().toPlainString();
    }
}
