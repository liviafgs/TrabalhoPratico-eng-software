package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Notificacao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.TipoNotificacao;
import conexaosolidaria.repository.ConsumoPerCapitaRepository;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.HistoricoSolicitacaoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.NotificacaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoServiceTest {
    private final LocalDate hoje = LocalDate.now();
    private final AccessControl accessControl = new AccessControl();
    private final OfertaRepository ofertaRepository = new OfertaRepository();
    private final DemandaRepository demandaRepository = new DemandaRepository();
    private final SolicitacaoOfertaRepository solicitacaoRepository = new SolicitacaoOfertaRepository();
    private final InstituicaoRepository instituicaoRepository = new InstituicaoRepository();
    private final EstabelecimentoRepository estabelecimentoRepository = new EstabelecimentoRepository();
    private final InstituicaoService instituicaoService = new InstituicaoService(instituicaoRepository);
    private final EstabelecimentoService estabelecimentoService = new EstabelecimentoService(estabelecimentoRepository);
    private final DisponibilidadeOfertaService disponibilidade = new DisponibilidadeOfertaService(solicitacaoRepository);
    private final OfertaService ofertaService = new OfertaService(ofertaRepository, accessControl, disponibilidade);
    private final SolicitacaoOfertaService solicitacaoService = new SolicitacaoOfertaService(
            ofertaRepository, solicitacaoRepository, accessControl, disponibilidade, instituicaoRepository,
            new HistoricoSolicitacaoRepository());
    private final DemandaService demandaService = new DemandaService(
            demandaRepository, accessControl,
            new CoerenciaDemandaService(new ConsumoPerCapitaService(new ConsumoPerCapitaRepository(), accessControl)),
            instituicaoRepository);
    private final NotificacaoService notificacaoService = new NotificacaoService(
            new NotificacaoRepository(), accessControl, ofertaRepository, demandaRepository, instituicaoRepository,
            estabelecimentoRepository, new CorrespondenciaService(disponibilidade, instituicaoRepository));

    private final AuthenticatedUser padaria = usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser mercado = usuario(4L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser casa = usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser lar = usuario(3L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);

    NotificacaoServiceTest() {
        estabelecimentoService.cadastrar(padaria, "Padaria Central", "11222333000144", "Rua 1", "(35) 1111-1111");
        estabelecimentoService.cadastrar(mercado, "Mercado Bom Preco", "22333444000155", "Rua 2", "(35) 2222-2222");
        cadastrarInstituicao(casa, "Casa da Esperanca", "33444555000166");
        cadastrarInstituicao(lar, "Lar Sao Jose", "44555666000177");

        ofertaService.aoCadastrar(notificacaoService::notificarNovaOferta);
        demandaService.aoCadastrar(notificacaoService::notificarNovaDemanda);
        solicitacaoService.aoMudarStatus(notificacaoService::notificarMudancaStatus);
    }

    @Test
    void deveNotificarCadaEtapaDaDoacaoParaQuemPrecisaSaber() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        SolicitacaoOferta solicitacao = solicitacaoService.solicitar(casa, arroz.getId(), 4);

        assertEquals(List.of(TipoNotificacao.SOLICITACAO_RECEBIDA), tipos(padaria));
        assertTrue(mensagens(padaria).get(0).contains("Casa da Esperanca"));
        assertTrue(mensagens(padaria).get(0).contains("4 kg de Arroz"));
        assertTrue(tipos(casa).isEmpty());

        solicitacaoService.aceitar(padaria, solicitacao.getId());
        solicitacaoService.agendar(padaria, solicitacao.getId(), LocalDateTime.now().plusDays(2));
        solicitacaoService.confirmarRetirada(padaria, solicitacao.getId());
        assertEquals(List.of(TipoNotificacao.RETIRADA_CONFIRMADA, TipoNotificacao.RETIRADA_AGENDADA,
                TipoNotificacao.SOLICITACAO_ACEITA), tipos(casa));
        assertTrue(mensagens(casa).get(1).contains("agendada para"));

        solicitacaoService.concluir(casa, solicitacao.getId());
        assertEquals(List.of(TipoNotificacao.DOACAO_CONCLUIDA, TipoNotificacao.SOLICITACAO_RECEBIDA), tipos(padaria));
    }

    @Test
    void deveNotificarRecusaECancelamento() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        SolicitacaoOferta recusada = solicitacaoService.solicitar(casa, arroz.getId(), 4);
        SolicitacaoOferta cancelada = solicitacaoService.solicitar(lar, arroz.getId(), 3);

        solicitacaoService.recusar(padaria, recusada.getId());
        solicitacaoService.cancelar(lar, cancelada.getId());

        assertEquals(List.of(TipoNotificacao.SOLICITACAO_RECUSADA), tipos(casa));
        assertTrue(mensagens(casa).get(0).contains("Padaria Central recusou"));
        assertEquals(TipoNotificacao.SOLICITACAO_CANCELADA, tipos(padaria).get(0));
    }

    @Test
    void naoDeveNotificarOAutorDaPropriaAcao() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        SolicitacaoOferta solicitacao = solicitacaoService.solicitar(casa, arroz.getId(), 4);
        AuthenticatedUser admin = usuario(9L, PerfilUsuario.ADMINISTRADOR);

        solicitacaoService.aceitar(admin, solicitacao.getId());

        assertTrue(notificacaoService.listar(admin, null).isEmpty());
        assertEquals(TipoNotificacao.SOLICITACAO_ACEITA, tipos(casa).get(0));
    }

    @Test
    void deveAvisarSobreNovaOfertaCompativelUmaVezPorInstituicao() {
        demanda(casa, "Arroz", 10, "kg");
        demanda(casa, "Arroz integral", 5, "kg");
        demanda(lar, "Feijao", 5, "kg");

        ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));

        List<Notificacao> daCasa = notificacaoService.listar(casa, null);
        assertEquals(1, daCasa.size());
        assertEquals(TipoNotificacao.NOVA_OPORTUNIDADE, daCasa.get(0).getTipo());
        assertTrue(daCasa.get(0).getMensagem().contains("Padaria Central"));
        assertNotNull(daCasa.get(0).getIdOferta());
        assertTrue(notificacaoService.listar(lar, null).isEmpty());
    }

    @Test
    void deveAvisarSobreNovaDemandaCompativelComAsOfertasDoEstabelecimento() {
        ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        ofertaService.cadastrar(mercado, "Leite", 10, "litro", hoje.plusDays(5));

        demanda(casa, "Arroz", 10, "kg");

        List<Notificacao> daPadaria = notificacaoService.listar(padaria, null);
        assertEquals(1, daPadaria.size());
        assertTrue(daPadaria.get(0).getMensagem().contains("Casa da Esperanca"));
        assertNotNull(daPadaria.get(0).getIdDemanda());
        assertTrue(notificacaoService.listar(mercado, null).isEmpty());
    }

    @Test
    void deveListarSoAsNotificacoesDoUsuarioEFiltrarPorLida() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        solicitacaoService.solicitar(casa, arroz.getId(), 2);
        solicitacaoService.solicitar(lar, arroz.getId(), 3);

        List<Notificacao> todas = notificacaoService.listar(padaria, null);
        assertEquals(2, todas.size());
        assertTrue(todas.get(0).getId() > todas.get(1).getId());
        assertTrue(notificacaoService.listar(casa, null).isEmpty());

        notificacaoService.marcarComoLida(padaria, todas.get(0).getId());
        assertEquals(1, notificacaoService.listar(padaria, false).size());
        assertEquals(1, notificacaoService.listar(padaria, true).size());
        assertEquals(1L, notificacaoService.contarNaoLidas(padaria));
    }

    @Test
    void deveMarcarComoLidaDeFormaIdempotenteESoParaODono() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        solicitacaoService.solicitar(casa, arroz.getId(), 2);
        long id = notificacaoService.listar(padaria, null).get(0).getId();

        Notificacao lida = notificacaoService.marcarComoLida(padaria, id);
        assertTrue(lida.isLida());
        LocalDateTime primeiraLeitura = lida.getLidaEm();
        assertEquals(primeiraLeitura, notificacaoService.marcarComoLida(padaria, id).getLidaEm());

        assertEquals(404, assertThrows(ApiException.class,
                () -> notificacaoService.marcarComoLida(mercado, id)).getStatusCode());
        assertEquals(404, assertThrows(ApiException.class,
                () -> notificacaoService.marcarComoLida(padaria, 999)).getStatusCode());
    }

    @Test
    void deveMarcarTodasComoLidas() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        solicitacaoService.solicitar(casa, arroz.getId(), 2);
        solicitacaoService.solicitar(lar, arroz.getId(), 3);

        assertEquals(2, notificacaoService.marcarTodasComoLidas(padaria));
        assertEquals(0L, notificacaoService.contarNaoLidas(padaria));
        assertEquals(0, notificacaoService.marcarTodasComoLidas(padaria));
        assertFalse(notificacaoService.listar(padaria, true).isEmpty());
    }

    @Test
    void perfilSemPermissaoNaoConsultaNotificacoes() {
        ApiException erro = assertThrows(ApiException.class, () -> notificacaoService.listar(
                usuario(7L, PerfilUsuario.NUTRICIONISTA), null));
        assertEquals(403, erro.getStatusCode());
    }

    @Test
    void falhaNoOuvinteNaoDesfazOCadastro() {
        ofertaService.aoCadastrar(oferta -> {
            throw new IllegalStateException("falha simulada");
        });

        Oferta oferta = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));

        assertTrue(oferta.getId() > 0);
        assertEquals(1, ofertaService.listar(padaria).size());
    }

    private void demanda(AuthenticatedUser instituicao, String alimento, double quantidade, String unidade) {
        demandaService.cadastrar(instituicao, alimento, quantidade, unidade, hoje.plusDays(7), "ALTA",
                "Preparacao de refeicoes", 30, null, null);
    }

    private List<TipoNotificacao> tipos(AuthenticatedUser usuario) {
        return notificacaoService.listar(usuario, null).stream().map(Notificacao::getTipo).toList();
    }

    private List<String> mensagens(AuthenticatedUser usuario) {
        return notificacaoService.listar(usuario, null).stream().map(Notificacao::getMensagem).toList();
    }

    private void cadastrarInstituicao(AuthenticatedUser usuario, String nome, String cnpj) {
        instituicaoService.cadastrar(usuario, nome, cnpj, "Rua B, 41", "(31) 3232-4444");
        instituicaoService.habilitar(usuario, true);
    }

    private AuthenticatedUser usuario(long id, PerfilUsuario perfil) {
        return new AuthenticatedUser(id, "Usuario", id + "@teste.com", perfil);
    }
}
