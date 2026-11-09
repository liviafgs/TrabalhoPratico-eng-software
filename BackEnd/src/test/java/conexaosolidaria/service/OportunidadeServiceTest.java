package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.repository.ConsumoPerCapitaRepository;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.HistoricoSolicitacaoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.service.Correspondencia.NivelAtendimento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OportunidadeServiceTest {
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
    private final CorrespondenciaService correspondenciaService = new CorrespondenciaService(
            disponibilidade, instituicaoRepository);
    private final OportunidadeService oportunidadeService = new OportunidadeService(
            ofertaRepository, demandaRepository, instituicaoRepository, estabelecimentoRepository,
            correspondenciaService, accessControl);

    private final AuthenticatedUser padaria = usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser mercado = usuario(4L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser casa = usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser lar = usuario(3L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser pendente = usuario(5L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser admin = usuario(9L, PerfilUsuario.ADMINISTRADOR);

    OportunidadeServiceTest() {
        estabelecimentoService.cadastrar(padaria, "Padaria Central", "11222333000144", "Rua 1", "(35) 1111-1111");
        estabelecimentoService.cadastrar(mercado, "Mercado Bom Preco", "22333444000155", "Rua 2", "(35) 2222-2222");
        cadastrarInstituicao(casa, "Casa da Esperanca", "33444555000166", true);
        cadastrarInstituicao(lar, "Lar Sao Jose", "44555666000177", true);
        cadastrarInstituicao(pendente, "Abrigo Novo", "55666777000188", false);
    }

    @Test
    void deveCorresponderPorAlimentoUnidadeEQuantidade() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));

        Correspondencia total = avaliar(arroz, demanda(casa, "arroz", 10, "KG", "ALTA", 7)).orElseThrow();
        assertEquals(NivelAtendimento.TOTAL, total.nivel());
        assertEquals(10.0, total.quantidadeAtendivel(), 1e-9);

        Correspondencia parcial = avaliar(arroz, demanda(casa, "Arroz", 50, "kg", "BAIXA", 7)).orElseThrow();
        assertEquals(NivelAtendimento.PARCIAL, parcial.nivel());
        assertEquals(20.0, parcial.quantidadeAtendivel(), 1e-9);

        assertTrue(avaliar(arroz, demanda(casa, "Arroz integral", 5, "kg", "MEDIA", 7)).isPresent());
        assertTrue(avaliar(arroz, demanda(casa, "Arroz", 5, "g", "MEDIA", 7)).isEmpty());
        assertTrue(avaliar(arroz, demanda(casa, "Feijão", 5, "kg", "MEDIA", 7)).isEmpty());
    }

    @Test
    void naoDeveCorresponderComOfertaEsgotadaOuExpirada() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 10, "kg", hoje.plusDays(5));
        Demanda demanda = demanda(casa, "Arroz", 5, "kg", "ALTA", 7);

        solicitacaoService.solicitar(lar, arroz.getId(), 10);
        assertTrue(avaliar(arroz, demanda).isEmpty());

        Oferta expirada = ofertaService.cadastrar(padaria, "Arroz", 10, "kg", hoje.minusDays(1));
        assertTrue(avaliar(expirada, demanda).isEmpty());
    }

    @Test
    void naoDeveCorresponderComDemandaVencidaOuDeInstituicaoNaoHabilitada() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));

        assertTrue(avaliar(arroz, demanda(casa, "Arroz", 5, "kg", "ALTA", -1)).isEmpty());
        assertTrue(avaliar(arroz, demanda(pendente, "Arroz", 5, "kg", "ALTA", 7)).isEmpty());
        assertTrue(avaliar(arroz, demanda(casa, "Arroz", 5, "kg", "ALTA", 0)).isPresent());
    }

    @Test
    void naoDeveCorresponderQuandoARetiradaDaOfertaForDepoisDoPrazoDaDemanda() {
        Oferta retiradaTarde = ofertaService.cadastrar(padaria, 10L, "Arroz", "Cereais", 20, "kg",
                hoje.plusDays(10), null, LocalDateTime.now().plusDays(8), "Centro");
        Oferta retiradaCedo = ofertaService.cadastrar(padaria, 10L, "Arroz", "Cereais", 20, "kg",
                hoje.plusDays(10), null, LocalDateTime.now().plusDays(2), "Centro");
        Demanda demanda = demanda(casa, "Arroz", 5, "kg", "ALTA", 5);

        assertTrue(avaliar(retiradaTarde, demanda).isEmpty());
        assertTrue(avaliar(retiradaCedo, demanda).isPresent());
    }

    @Test
    void instituicaoVeSomenteAsOfertasQueAtendemSuasDemandasPorOrdemDeUrgencia() {
        ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        ofertaService.cadastrar(mercado, "Feijão", 5, "kg", hoje.plusDays(5));
        demanda(casa, "Arroz", 50, "kg", "BAIXA", 7);
        demanda(casa, "Feijao", 5, "kg", "ALTA", 7);
        demanda(casa, "Arroz", 10, "kg", "ALTA", 3);
        demanda(lar, "Arroz", 5, "kg", "MEDIA", 7);

        List<Oportunidade> resultado = oportunidadeService.consultar(casa, null, null);

        assertEquals(3, resultado.size());
        assertEquals("Casa da Esperanca", resultado.get(0).instituicaoNome());
        assertEquals(List.of("ALTA", "ALTA", "BAIXA"), prioridades(resultado));
        assertEquals(NivelAtendimento.TOTAL, resultado.get(0).correspondencia().nivel());
        assertEquals(hoje.plusDays(3), resultado.get(0).correspondencia().demanda().getPrazo());
        assertEquals(hoje.plusDays(7), resultado.get(1).correspondencia().demanda().getPrazo());
        assertEquals(NivelAtendimento.PARCIAL, resultado.get(2).correspondencia().nivel());
        assertEquals("Padaria Central", resultado.get(0).estabelecimentoNome());
    }

    @Test
    void estabelecimentoVeSomenteAsDemandasQuePodemReceberSuasOfertas() {
        ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        ofertaService.cadastrar(mercado, "Feijão", 5, "kg", hoje.plusDays(5));
        demanda(casa, "Arroz", 10, "kg", "ALTA", 7);
        demanda(lar, "Feijao", 5, "kg", "MEDIA", 7);

        List<Oportunidade> daPadaria = oportunidadeService.consultar(padaria, null, null);
        assertEquals(1, daPadaria.size());
        assertEquals("Casa da Esperanca", daPadaria.get(0).instituicaoNome());

        List<Oportunidade> doMercado = oportunidadeService.consultar(mercado, null, null);
        assertEquals(1, doMercado.size());
        assertEquals("Lar Sao Jose", doMercado.get(0).instituicaoNome());

        assertEquals(2, oportunidadeService.consultar(admin, null, null).size());
    }

    @Test
    void deveFiltrarPorOfertaEPorDemanda() {
        Oferta arroz = ofertaService.cadastrar(padaria, "Arroz", 20, "kg", hoje.plusDays(5));
        Oferta outroArroz = ofertaService.cadastrar(mercado, "Arroz", 20, "kg", hoje.plusDays(5));
        Demanda daCasa = demanda(casa, "Arroz", 10, "kg", "ALTA", 7);
        demanda(lar, "Arroz", 5, "kg", "MEDIA", 7);

        assertEquals(4, oportunidadeService.consultar(admin, null, null).size());
        assertEquals(2, oportunidadeService.consultar(admin, arroz.getId(), null).size());
        assertEquals(2, oportunidadeService.consultar(admin, null, daCasa.getId()).size());
        assertEquals(1, oportunidadeService.consultar(admin, outroArroz.getId(), daCasa.getId()).size());
        assertEquals(0, oportunidadeService.consultar(admin, 999L, null).size());
    }

    @Test
    void naoDeveListarOportunidadesParaPerfilSemPermissao() {
        ApiException erro = assertThrows(ApiException.class,
                () -> oportunidadeService.consultar(usuario(7L, PerfilUsuario.NUTRICIONISTA), null, null));
        assertEquals(403, erro.getStatusCode());
        assertFalse(erro.getMessage().isBlank());
    }

    private java.util.Optional<Correspondencia> avaliar(Oferta oferta, Demanda demanda) {
        return correspondenciaService.avaliar(oferta, demanda);
    }

    private Demanda demanda(AuthenticatedUser instituicao, String alimento, double quantidade, String unidade,
            String prioridade, int diasParaPrazo) {
        return demandaService.cadastrar(instituicao, alimento, quantidade, unidade, hoje.plusDays(diasParaPrazo),
                prioridade, "Preparacao de refeicoes", 30, null, null);
    }

    private List<String> prioridades(List<Oportunidade> oportunidades) {
        return oportunidades.stream().map(item -> item.correspondencia().demanda().getPrioridade()).toList();
    }

    private void cadastrarInstituicao(AuthenticatedUser usuario, String nome, String cnpj, boolean habilitar) {
        instituicaoService.cadastrar(usuario, nome, cnpj, "Rua B, 41", "(31) 3232-4444");
        if (habilitar) {
            instituicaoService.habilitar(usuario, true);
        }
    }

    private AuthenticatedUser usuario(long id, PerfilUsuario perfil) {
        return new AuthenticatedUser(id, "Usuario", id + "@teste.com", perfil);
    }
}
