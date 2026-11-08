package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.ConsumoPerCapita;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.repository.ConsumoPerCapitaRepository;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.service.CoerenciaDemandaService.ResultadoCoerencia;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemandaCoerenciaServiceTest {
    private final AccessControl accessControl = new AccessControl();
    private final ConsumoPerCapitaService consumoService = new ConsumoPerCapitaService(
            new ConsumoPerCapitaRepository(), accessControl);
    private final DemandaService demandaService = new DemandaService(
            new DemandaRepository(), accessControl, new CoerenciaDemandaService(consumoService),
            new InstituicaoRepository());

    private final AuthenticatedUser instituicao = usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser nutricionista = usuario(3L, PerfilUsuario.NUTRICIONISTA);

    @Test
    void deveCadastrarDemandaComFinalidadeEBeneficiarios() {
        Demanda demanda = cadastrar("Arroz", 10, "kg", 30, null, null);

        assertEquals("Preparacao de refeicoes", demanda.getFinalidade());
        assertEquals(30, demanda.getBeneficiarios());
        assertEquals("MEDIA", demanda.getPrioridade());
        assertNull(demanda.getQuantidadeReferencia());
        assertFalse(demanda.isAcimaDaReferencia());
    }

    @Test
    void deveRecusarDemandaSemFinalidadeOuSemBeneficiarios() {
        ApiException semFinalidade = assertThrows(ApiException.class, () -> demandaService.cadastrar(
                instituicao, "Arroz", 10, "kg", LocalDate.now().plusDays(5), "MEDIA", " ", 30, null, null));
        assertEquals(400, semFinalidade.getStatusCode());

        ApiException semBeneficiarios = assertThrows(ApiException.class, () -> demandaService.cadastrar(
                instituicao, "Arroz", 10, "kg", LocalDate.now().plusDays(5), "MEDIA", "Refeicoes", 0, null, null));
        assertEquals(400, semBeneficiarios.getStatusCode());
    }

    @Test
    void deveCalcularQuantidadeDeReferenciaComConsumoInformadoNaDemanda() {
        Demanda demanda = cadastrar("Arroz", 15, "kg", 30, 0.5, null);

        assertEquals(15.0, demanda.getQuantidadeReferencia(), 1e-9);
        assertEquals(0.5, demanda.getConsumoPerCapita(), 1e-9);
        assertFalse(demanda.isAcimaDaReferencia());
    }

    @Test
    void deveUsarConsumoPerCapitaCadastradoParaOAlimento() {
        consumoService.cadastrar(nutricionista, "Feijão", "kg", 0.2);

        Demanda demanda = cadastrar("feijao", 6, "KG", 30, null, null);

        assertEquals(6.0, demanda.getQuantidadeReferencia(), 1e-9);
        assertFalse(demanda.isAcimaDaReferencia());
    }

    @Test
    void deveExigirJustificativaQuandoQuantidadeEstaAcimaDaReferencia() {
        ApiException erro = assertThrows(ApiException.class,
                () -> cadastrar("Arroz", 40, "kg", 30, 0.5, null));

        assertEquals(409, erro.getStatusCode());
        assertTrue(erro.getMessage().contains("referencia de 15"));
        assertTrue(erro.getMessage().contains("justificativa"));
    }

    @Test
    void devePermitirDemandaExcepcionalQuandoHaJustificativa() {
        Demanda demanda = cadastrar("Arroz", 40, "kg", 30, 0.5, "Evento beneficente no fim de semana");

        assertTrue(demanda.isAcimaDaReferencia());
        assertEquals("Evento beneficente no fim de semana", demanda.getJustificativa());
        assertEquals(15.0, demanda.getQuantidadeReferencia(), 1e-9);
    }

    @Test
    void deveConsiderarCoerenteAteVintePorCentoAcimaDaReferencia() {
        Demanda noLimite = cadastrar("Arroz", 18, "kg", 30, 0.5, null);
        assertFalse(noLimite.isAcimaDaReferencia());

        assertThrows(ApiException.class, () -> cadastrar("Arroz", 18.5, "kg", 30, 0.5, null));
    }

    @Test
    void naoDeveVerificarCoerenciaSemConsumoPerCapitaOuComUnidadeDiferente() {
        consumoService.cadastrar(nutricionista, "Arroz", "kg", 0.5);

        ResultadoCoerencia semConsumo = demandaService.verificarCoerencia(
                instituicao, "Macarrao", 500, "kg", 30, null);
        assertNull(semConsumo.quantidadeReferencia());
        assertFalse(semConsumo.acimaDaReferencia());

        ResultadoCoerencia outraUnidade = demandaService.verificarCoerencia(
                instituicao, "Arroz", 500, "g", 30, null);
        assertNull(outraUnidade.quantidadeReferencia());
        assertFalse(outraUnidade.acimaDaReferencia());
    }

    @Test
    void deveApresentarAlertaNaVerificacaoSemRegistrarDemanda() {
        ResultadoCoerencia resultado = demandaService.verificarCoerencia(
                instituicao, "Arroz", 40, "kg", 30, 0.5);

        assertTrue(resultado.acimaDaReferencia());
        assertEquals(15.0, resultado.quantidadeReferencia(), 1e-9);
        assertTrue(resultado.mensagem().contains("justificativa"));
        assertTrue(demandaService.listar(usuario(9L, PerfilUsuario.ADMINISTRADOR)).isEmpty());
    }

    @Test
    void apenasNutricionistaOuAdministradorCadastraConsumoPerCapita() {
        ApiException erro = assertThrows(ApiException.class,
                () -> consumoService.cadastrar(instituicao, "Arroz", "kg", 0.5));
        assertEquals(403, erro.getStatusCode());

        consumoService.cadastrar(usuario(9L, PerfilUsuario.ADMINISTRADOR), "Arroz", "kg", 0.5);
        assertEquals(1, consumoService.listar(instituicao).size());
    }

    @Test
    void deveAtualizarConsumoPerCapitaJaCadastradoParaOMesmoAlimentoEUnidade() {
        consumoService.cadastrar(nutricionista, "Arroz", "kg", 0.5);
        consumoService.cadastrar(nutricionista, "arroz", "KG", 0.7);

        List<ConsumoPerCapita> consumos = consumoService.listar(instituicao);
        assertEquals(1, consumos.size());
        assertEquals(0.7, consumos.get(0).getQuantidadePorPessoa(), 1e-9);
    }

    @Test
    void naoDeveCadastrarConsumoPerCapitaInvalido() {
        ApiException erro = assertThrows(ApiException.class,
                () -> consumoService.cadastrar(nutricionista, "Arroz", "kg", 0));
        assertEquals(400, erro.getStatusCode());
    }

    private Demanda cadastrar(String alimento, double quantidade, String unidade, int beneficiarios,
            Double consumo, String justificativa) {
        return demandaService.cadastrar(instituicao, alimento, quantidade, unidade,
                LocalDate.now().plusDays(5), "MEDIA", "Preparacao de refeicoes", beneficiarios, consumo,
                justificativa);
    }

    private AuthenticatedUser usuario(long id, PerfilUsuario perfil) {
        return new AuthenticatedUser(id, "Usuario", id + "@teste.com", perfil);
    }
}
