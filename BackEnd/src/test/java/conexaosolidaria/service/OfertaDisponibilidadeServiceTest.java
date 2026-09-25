package test.java.conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfertaDisponibilidadeServiceTest {

    @Test
    void deveFiltrarOfertasPorAlimentoCategoriaLocalEData() {
        OfertaRepository ofertaRepository = new OfertaRepository();
        OfertaService service = criarService(ofertaRepository, new SolicitacaoOfertaRepository());
        AuthenticatedUser doador = usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR);

        service.cadastrar(doador, 10L, "Arroz", "Cereais", 20, "kg",
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(3), null, "Centro");
        service.cadastrar(doador, 11L, "Feijao", "Leguminosas", 10, "kg",
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(3), null, "Zona Sul");

        List<Oferta> resultado = service.consultarDisponiveis(
                usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA),
                new OfertaFiltros("arroz", "cere", "cent", LocalDate.now().plusDays(3), true));

        assertEquals(1, resultado.size());
        assertEquals("Arroz", resultado.get(0).getAlimento());
    }

    @Test
    void deveAbaterQuantidadeSolicitadaEConfirmada() {
        OfertaRepository ofertaRepository = new OfertaRepository();
        SolicitacaoOfertaRepository solicitacaoRepository = new SolicitacaoOfertaRepository();
        OfertaService ofertaService = criarService(ofertaRepository, solicitacaoRepository);
        SolicitacaoOfertaService solicitacaoService = new SolicitacaoOfertaService(
                ofertaRepository, solicitacaoRepository, new AccessControl());
        Oferta oferta = ofertaService.cadastrar(
                usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR),
                "Arroz", 10, "kg", LocalDate.now().plusDays(2));

        SolicitacaoOferta solicitacao = solicitacaoService.solicitar(
                usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA), oferta.getId(), 4);
        assertEquals(6, ofertaService.quantidadeDisponivel(oferta));

        solicitacaoService.confirmarRetirada(
                usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR), solicitacao.getId());
        assertEquals(6, ofertaService.quantidadeDisponivel(oferta));

        assertThrows(RuntimeException.class, () -> solicitacaoService.solicitar(
                usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA), oferta.getId(), 7));
    }

    @Test
    void naoDeveConsultarOfertaExpiradaComoDisponivel() {
        OfertaRepository ofertaRepository = new OfertaRepository();
        OfertaService service = criarService(ofertaRepository, new SolicitacaoOfertaRepository());
        service.cadastrar(
                usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR),
                "Leite", 5, "litro", LocalDate.now().minusDays(1));

        List<Oferta> resultado = service.consultarDisponiveis(
                usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA), OfertaFiltros.vazios());

        assertEquals(0, resultado.size());
    }

    private OfertaService criarService(OfertaRepository ofertaRepository,
            SolicitacaoOfertaRepository solicitacaoRepository) {
        AccessControl accessControl = new AccessControl();
        SolicitacaoOfertaService solicitacaoService = new SolicitacaoOfertaService(
                ofertaRepository, solicitacaoRepository, accessControl);
        return new OfertaService(ofertaRepository, accessControl, solicitacaoService);
    }

    private AuthenticatedUser usuario(long id, PerfilUsuario perfil) {
        return new AuthenticatedUser(id, "Usuario", id + "@teste.com", perfil);
    }
}
