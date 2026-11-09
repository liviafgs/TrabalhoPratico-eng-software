package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.HistoricoSolicitacao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.StatusSolicitacaoOferta;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.HistoricoSolicitacaoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HistoricoDoacaoServiceTest {
    private final LocalDate hoje = LocalDate.now();
    private final AccessControl accessControl = new AccessControl();
    private final OfertaRepository ofertaRepository = new OfertaRepository();
    private final SolicitacaoOfertaRepository solicitacaoRepository = new SolicitacaoOfertaRepository();
    private final InstituicaoRepository instituicaoRepository = new InstituicaoRepository();
    private final EstabelecimentoRepository estabelecimentoRepository = new EstabelecimentoRepository();
    private final HistoricoSolicitacaoRepository historicoRepository = new HistoricoSolicitacaoRepository();
    private final InstituicaoService instituicaoService = new InstituicaoService(instituicaoRepository);
    private final EstabelecimentoService estabelecimentoService = new EstabelecimentoService(estabelecimentoRepository);
    private final DisponibilidadeOfertaService disponibilidade = new DisponibilidadeOfertaService(solicitacaoRepository);
    private final OfertaService ofertaService = new OfertaService(ofertaRepository, accessControl, disponibilidade);
    private final SolicitacaoOfertaService solicitacaoService = new SolicitacaoOfertaService(
            ofertaRepository, solicitacaoRepository, accessControl, disponibilidade, instituicaoRepository,
            historicoRepository);
    private final HistoricoDoacaoService historicoService = new HistoricoDoacaoService(
            solicitacaoRepository, ofertaRepository, instituicaoRepository, estabelecimentoRepository,
            historicoRepository, accessControl);

    private final AuthenticatedUser padaria = usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser mercado = usuario(4L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser casaEsperanca = usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser larSaoJose = usuario(3L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser admin = usuario(9L, PerfilUsuario.ADMINISTRADOR);

    private SolicitacaoOferta arrozParaCasa;
    private SolicitacaoOferta feijaoParaLar;
    private SolicitacaoOferta leiteParaCasa;

    HistoricoDoacaoServiceTest() {
        estabelecimentoService.cadastrar(padaria, "Padaria Central", "11222333000144", "Rua 1", "(35) 1111-1111");
        estabelecimentoService.cadastrar(mercado, "Mercado Bom Preco", "22333444000155", "Rua 2", "(35) 2222-2222");
        cadastrarInstituicao(casaEsperanca, "Casa da Esperanca", "33444555000166");
        cadastrarInstituicao(larSaoJose, "Lar Sao Jose", "44555666000177");

        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        Oferta feijao = ofertaService.cadastrar(mercado, "Feijão", 20, "kg", hoje.plusDays(5));
        Oferta leite = ofertaService.cadastrar(padaria, "Leite", 20, "litro", hoje.plusDays(5));

        arrozParaCasa = solicitacaoService.solicitar(casaEsperanca, arroz.getId(), 5);
        feijaoParaLar = solicitacaoService.solicitar(larSaoJose, feijao.getId(), 5);
        leiteParaCasa = solicitacaoService.solicitar(casaEsperanca, leite.getId(), 5);

        solicitacaoService.aceitar(padaria, arrozParaCasa.getId());
        solicitacaoService.confirmarRetirada(padaria, arrozParaCasa.getId());
        solicitacaoService.concluir(casaEsperanca, arrozParaCasa.getId());
        solicitacaoService.recusar(padaria, leiteParaCasa.getId());
    }

    @Test
    void cadaPerfilVeApenasOQueLheDizRespeito() {
        assertEquals(List.of("Leite", "Arroz"), alimentos(historicoService.consultar(padaria, null)));
        assertEquals(List.of("Feijão"), alimentos(historicoService.consultar(mercado, null)));
        assertEquals(List.of("Leite", "Arroz"), alimentos(historicoService.consultar(casaEsperanca, null)));
        assertEquals(List.of("Feijão"), alimentos(historicoService.consultar(larSaoJose, null)));
        assertEquals(3, historicoService.consultar(admin, null).size());
    }

    @Test
    void perfilSemPermissaoNaoConsultaOHistorico() {
        ApiException erro = assertThrows(ApiException.class, () -> historicoService.consultar(
                usuario(7L, PerfilUsuario.NUTRICIONISTA), null));
        assertEquals(403, erro.getStatusCode());
    }

    @Test
    void deveListarAsDoacoesMaisRecentesPrimeiroComOsNomesDosParticipantes() {
        List<HistoricoDoacaoItem> itens = historicoService.consultar(admin, null);

        assertEquals(leiteParaCasa.getId(), itens.get(0).solicitacao().getId());
        HistoricoDoacaoItem concluida = itens.stream()
                .filter(item -> item.solicitacao().getId() == arrozParaCasa.getId()).findFirst().orElseThrow();
        assertEquals("Casa da Esperanca", concluida.instituicaoNome());
        assertEquals("Padaria Central", concluida.estabelecimentoNome());
    }

    @Test
    void deveFiltrarPorStatus() {
        HistoricoFiltros concluidas = filtros(null, null, null, Set.of(StatusSolicitacaoOferta.CONCLUIDA), null, null);
        assertEquals(List.of("Arroz"), alimentos(historicoService.consultar(admin, concluidas)));

        HistoricoFiltros duas = filtros(null, null, null,
                Set.of(StatusSolicitacaoOferta.CONCLUIDA, StatusSolicitacaoOferta.RECUSADA), null, null);
        assertEquals(List.of("Leite", "Arroz"), alimentos(historicoService.consultar(admin, duas)));
    }

    @Test
    void deveFiltrarPorAlimentoInstituicaoEEstabelecimento() {
        assertEquals(List.of("Feijão"), alimentos(historicoService.consultar(admin,
                filtros("FEIJAO", null, null, Set.of(), null, null))));
        assertEquals(List.of("Feijão"), alimentos(historicoService.consultar(admin,
                filtros(null, "lar sao", null, Set.of(), null, null))));
        assertEquals(List.of("Leite", "Arroz"), alimentos(historicoService.consultar(admin,
                filtros(null, null, "padaria", Set.of(), null, null))));
        assertEquals(List.of("Arroz"), alimentos(historicoService.consultar(admin,
                filtros("arroz", "esperanca", "padaria", Set.of(), null, null))));
    }

    @Test
    void deveFiltrarPorPeriodoDaUltimaMovimentacao() {
        assertEquals(3, historicoService.consultar(admin, filtros(null, null, null, Set.of(), hoje, hoje)).size());
        assertEquals(0, historicoService.consultar(admin,
                filtros(null, null, null, Set.of(), hoje.plusDays(1), null)).size());
        assertEquals(0, historicoService.consultar(admin,
                filtros(null, null, null, Set.of(), null, hoje.minusDays(1))).size());

        ApiException invertido = assertThrows(ApiException.class, () -> historicoService.consultar(admin,
                filtros(null, null, null, Set.of(), hoje.plusDays(2), hoje)));
        assertEquals(400, invertido.getStatusCode());
    }

    @Test
    void deveRegistrarCadaMudancaDeStatusNaLinhaDoTempo() {
        List<HistoricoSolicitacao> linha = historicoService.linhaDoTempo(casaEsperanca, arrozParaCasa.getId());

        assertEquals(4, linha.size());
        assertNull(linha.get(0).statusAnterior());
        assertEquals(StatusSolicitacaoOferta.SOLICITADA, linha.get(0).statusNovo());
        assertEquals(casaEsperanca.id(), linha.get(0).idUsuario());
        assertEquals(StatusSolicitacaoOferta.ACEITA, linha.get(1).statusNovo());
        assertEquals(StatusSolicitacaoOferta.RETIRADA, linha.get(2).statusNovo());
        assertEquals(StatusSolicitacaoOferta.RETIRADA, linha.get(3).statusAnterior());
        assertEquals(StatusSolicitacaoOferta.CONCLUIDA, linha.get(3).statusNovo());
        assertEquals(padaria.id(), linha.get(1).idUsuario());
    }

    @Test
    void naoDeveExibirLinhaDoTempoDeSolicitacaoDeOutroParticipante() {
        assertEquals(403, assertThrows(ApiException.class,
                () -> historicoService.linhaDoTempo(larSaoJose, arrozParaCasa.getId())).getStatusCode());
        assertEquals(403, assertThrows(ApiException.class,
                () -> historicoService.linhaDoTempo(mercado, arrozParaCasa.getId())).getStatusCode());
        assertEquals(404, assertThrows(ApiException.class,
                () -> historicoService.linhaDoTempo(admin, 999)).getStatusCode());
        assertEquals(4, historicoService.linhaDoTempo(admin, arrozParaCasa.getId()).size());
        assertEquals(LocalDate.now(), historicoService.linhaDoTempo(padaria, arrozParaCasa.getId())
                .get(0).registradoEm().toLocalDate());
    }

    private HistoricoFiltros filtros(String alimento, String instituicao, String estabelecimento,
            Set<StatusSolicitacaoOferta> status, LocalDate de, LocalDate ate) {
        return new HistoricoFiltros(alimento, instituicao, estabelecimento, status, de, ate);
    }

    private List<String> alimentos(List<HistoricoDoacaoItem> itens) {
        return itens.stream().map(item -> item.oferta().getAlimento()).toList();
    }

    private void cadastrarInstituicao(AuthenticatedUser usuario, String nome, String cnpj) {
        instituicaoService.cadastrar(usuario, nome, cnpj, "Rua B, 41", "(31) 3232-4444");
        instituicaoService.habilitar(usuario, true);
    }

    private AuthenticatedUser usuario(long id, PerfilUsuario perfil) {
        return new AuthenticatedUser(id, "Usuario", id + "@teste.com", perfil);
    }
}
