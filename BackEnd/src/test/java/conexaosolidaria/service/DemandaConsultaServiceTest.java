package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.repository.ConsumoPerCapitaRepository;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.security.AccessControl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DemandaConsultaServiceTest {
    private final LocalDate hoje = LocalDate.now();
    private final AccessControl accessControl = new AccessControl();
    private final InstituicaoRepository instituicaoRepository = new InstituicaoRepository();
    private final InstituicaoService instituicaoService = new InstituicaoService(instituicaoRepository);
    private final DemandaService demandaService = new DemandaService(
            new DemandaRepository(),
            accessControl,
            new CoerenciaDemandaService(new ConsumoPerCapitaService(new ConsumoPerCapitaRepository(), accessControl)),
            instituicaoRepository);

    private final AuthenticatedUser instituicaoA = usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser instituicaoB = usuario(5L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser doador = usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR);

    DemandaConsultaServiceTest() {
        instituicaoService.cadastrar(instituicaoA, "Casa da Esperanca", "11222333000144",
                "Rua das Flores, 10, Nepomuceno", "(35) 3333-0000");
        instituicaoService.cadastrar(instituicaoB, "Lar Sao Jose", "22333444000155",
                "Avenida Brasil, 500, Lavras", "(35) 3333-1111");

        cadastrar(instituicaoA, "Arroz", "ALTA", 10);
        cadastrar(instituicaoA, "Feijão", "MEDIA", 3);
        cadastrar(instituicaoB, "Arroz", "BAIXA", 30);
        cadastrar(instituicaoB, "Leite", "ALTA", -2);
    }

    @Test
    void instituicaoVeApenasAsProprias() {
        assertEquals(List.of("Arroz", "Feijão"), alimentos(demandaService.listar(instituicaoA)));
        assertEquals(List.of("Arroz"), alimentos(demandaService.listar(instituicaoB)));
    }

    @Test
    void estabelecimentoVeTodasAsDemandasVigentes() {
        assertEquals(List.of("Arroz", "Feijão", "Arroz"), alimentos(demandaService.listar(doador)));
    }

    @Test
    void perfilSemPermissaoNaoConsultaDemandas() {
        ApiException erro = assertThrows(ApiException.class,
                () -> demandaService.listar(usuario(3L, PerfilUsuario.NUTRICIONISTA)));
        assertEquals(403, erro.getStatusCode());
    }

    @Test
    void deveFiltrarPorAlimentoSemDistinguirAcentoENemMaiuscula() {
        DemandaFiltros filtros = new DemandaFiltros("FEIJAO", null, null, null, null, true);
        assertEquals(List.of("Feijão"), alimentos(demandaService.listar(doador, filtros)));
    }

    @Test
    void deveFiltrarPorPrioridade() {
        DemandaFiltros filtros = new DemandaFiltros(null, "alta", null, null, null, true);
        assertEquals(List.of("Arroz"), alimentos(demandaService.listar(doador, filtros)));
    }

    @Test
    void deveFiltrarPorPeriodoDoPrazo() {
        DemandaFiltros ate = new DemandaFiltros(null, null, null, hoje.plusDays(10), null, true);
        assertEquals(List.of("Arroz", "Feijão"), alimentos(demandaService.listar(doador, ate)));

        DemandaFiltros de = new DemandaFiltros(null, null, hoje.plusDays(5), null, null, true);
        assertEquals(List.of("Arroz", "Arroz"), alimentos(demandaService.listar(doador, de)));
    }

    @Test
    void deveFiltrarPorLocalizacaoDaInstituicaoETrazerSeusDados() {
        DemandaFiltros filtros = new DemandaFiltros(null, null, null, null, "lavras", true);
        List<DemandaConsulta> resultado = demandaService.listar(doador, filtros);

        assertEquals(1, resultado.size());
        assertEquals("Lar Sao Jose", resultado.get(0).instituicaoNome());
        assertEquals("Avenida Brasil, 500, Lavras", resultado.get(0).instituicaoEndereco());
    }

    @Test
    void deveFiltrarPorDisponibilidade() {
        DemandaFiltros vencidas = new DemandaFiltros(null, null, null, null, null, false);
        assertEquals(List.of("Leite"), alimentos(demandaService.listar(doador, vencidas)));
    }

    @Test
    void deveCombinarFiltros() {
        DemandaFiltros filtros = new DemandaFiltros("arroz", null, null, null, "nepomuceno", true);
        List<DemandaConsulta> resultado = demandaService.listar(doador, filtros);

        assertEquals(1, resultado.size());
        assertEquals("ALTA", resultado.get(0).demanda().getPrioridade());
    }

    @Test
    void deveRecusarFiltrosInvalidos() {
        ApiException prioridade = assertThrows(ApiException.class, () -> demandaService.listar(
                doador, new DemandaFiltros(null, "urgente", null, null, null, true)));
        assertEquals(400, prioridade.getStatusCode());

        ApiException periodo = assertThrows(ApiException.class, () -> demandaService.listar(
                doador, new DemandaFiltros(null, null, hoje.plusDays(5), hoje, null, true)));
        assertEquals(400, periodo.getStatusCode());
    }

    @Test
    void deveListarDemandaDeInstituicaoAindaNaoCadastradaSemDadosDeInstituicao() {
        AuthenticatedUser semCadastro = usuario(8L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
        cadastrar(semCadastro, "Macarrao", "BAIXA", 7);

        List<DemandaConsulta> resultado = demandaService.listar(semCadastro);

        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).instituicaoNome());
        assertNull(resultado.get(0).instituicaoEndereco());
    }

    private void cadastrar(AuthenticatedUser user, String alimento, String prioridade, int diasParaPrazo) {
        demandaService.cadastrar(user, alimento, 10, "kg", hoje.plusDays(diasParaPrazo), prioridade,
                "Preparacao de refeicoes", 30, null, null);
    }

    private List<String> alimentos(List<DemandaConsulta> consultas) {
        return consultas.stream().map(consulta -> consulta.demanda().getAlimento()).toList();
    }

    private AuthenticatedUser usuario(long id, PerfilUsuario perfil) {
        return new AuthenticatedUser(id, "Usuario", id + "@teste.com", perfil);
    }
}
