package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.StatusSolicitacaoOferta;
import conexaosolidaria.repository.HistoricoSolicitacaoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.security.AccessControl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolicitacaoStatusServiceTest {
    private final AccessControl accessControl = new AccessControl();
    private final OfertaRepository ofertaRepository = new OfertaRepository();
    private final SolicitacaoOfertaRepository solicitacaoRepository = new SolicitacaoOfertaRepository();
    private final InstituicaoRepository instituicaoRepository = new InstituicaoRepository();
    private final InstituicaoService instituicaoService = new InstituicaoService(instituicaoRepository);
    private final DisponibilidadeOfertaService disponibilidade = new DisponibilidadeOfertaService(solicitacaoRepository);
    private final OfertaService ofertaService = new OfertaService(ofertaRepository, accessControl, disponibilidade);
    private final SolicitacaoOfertaService service = new SolicitacaoOfertaService(
            ofertaRepository, solicitacaoRepository, accessControl, disponibilidade, instituicaoRepository,
            new HistoricoSolicitacaoRepository());

    private final AuthenticatedUser doador = usuario(1L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser outroDoador = usuario(4L, PerfilUsuario.ESTABELECIMENTO_DOADOR);
    private final AuthenticatedUser instituicao = usuario(2L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser outraInstituicao = usuario(3L, PerfilUsuario.INSTITUICAO_BENEFICIARIA);
    private final AuthenticatedUser admin = usuario(9L, PerfilUsuario.ADMINISTRADOR);

    private final Oferta oferta;

    SolicitacaoStatusServiceTest() {
        cadastrarInstituicao(instituicao, "11222333000144", true);
        oferta = ofertaService.cadastrar(doador, "Arroz", 10, "kg", LocalDate.now().plusDays(5));
    }

    @Test
    void naoDevePermitirSolicitacaoDeInstituicaoNaoCadastradaOuNaoHabilitada() {
        ApiException semCadastro = assertThrows(ApiException.class,
                () -> service.solicitar(outraInstituicao, oferta.getId(), 2));
        assertEquals(403, semCadastro.getStatusCode());

        cadastrarInstituicao(outraInstituicao, "22333444000155", false);
        ApiException pendente = assertThrows(ApiException.class,
                () -> service.solicitar(outraInstituicao, oferta.getId(), 2));
        assertEquals(403, pendente.getStatusCode());

        instituicaoService.habilitar(outraInstituicao, true);
        assertEquals(StatusSolicitacaoOferta.SOLICITADA,
                service.solicitar(outraInstituicao, oferta.getId(), 2).getStatus());
    }

    @Test
    void deveSeguirOFluxoCompletoAteConcluirADoacao() {
        SolicitacaoOferta solicitacao = service.solicitar(instituicao, oferta.getId(), 4);
        assertEquals(StatusSolicitacaoOferta.SOLICITADA, solicitacao.getStatus());

        assertEquals(StatusSolicitacaoOferta.ACEITA, service.aceitar(doador, solicitacao.getId()).getStatus());

        LocalDateTime retirada = LocalDateTime.now().plusDays(2);
        SolicitacaoOferta agendada = service.agendar(doador, solicitacao.getId(), retirada);
        assertEquals(StatusSolicitacaoOferta.AGENDADA, agendada.getStatus());
        assertEquals(retirada, agendada.getRetiradaAgendadaPara());

        assertEquals(StatusSolicitacaoOferta.RETIRADA,
                service.confirmarRetirada(doador, solicitacao.getId()).getStatus());
        assertEquals(StatusSolicitacaoOferta.CONCLUIDA,
                service.concluir(instituicao, solicitacao.getId()).getStatus());

        assertEquals(6, ofertaService.quantidadeDisponivel(oferta));
    }

    @Test
    void recusarOuCancelarDevolveAQuantidadeParaAOferta() {
        SolicitacaoOferta recusada = service.solicitar(instituicao, oferta.getId(), 4);
        assertEquals(6, ofertaService.quantidadeDisponivel(oferta));
        service.recusar(doador, recusada.getId());
        assertEquals(10, ofertaService.quantidadeDisponivel(oferta));

        SolicitacaoOferta cancelada = service.solicitar(instituicao, oferta.getId(), 5);
        service.aceitar(doador, cancelada.getId());
        assertEquals(5, ofertaService.quantidadeDisponivel(oferta));
        assertEquals(StatusSolicitacaoOferta.CANCELADA,
                service.cancelar(instituicao, cancelada.getId()).getStatus());
        assertEquals(10, ofertaService.quantidadeDisponivel(oferta));
    }

    @Test
    void aceitarNaoAlteraAQuantidadeJaReservadaNaSolicitacao() {
        SolicitacaoOferta solicitacao = service.solicitar(instituicao, oferta.getId(), 4);
        service.aceitar(doador, solicitacao.getId());

        assertEquals(6, ofertaService.quantidadeDisponivel(oferta));
    }

    @Test
    void somenteODoadorDaOfertaOuOAdministradorRespondeASolicitacao() {
        SolicitacaoOferta solicitacao = service.solicitar(instituicao, oferta.getId(), 4);

        assertEquals(403, assertThrows(ApiException.class,
                () -> service.aceitar(instituicao, solicitacao.getId())).getStatusCode());
        assertEquals(403, assertThrows(ApiException.class,
                () -> service.aceitar(outroDoador, solicitacao.getId())).getStatusCode());
        assertEquals(403, assertThrows(ApiException.class,
                () -> service.recusar(outroDoador, solicitacao.getId())).getStatusCode());

        assertEquals(StatusSolicitacaoOferta.ACEITA, service.aceitar(admin, solicitacao.getId()).getStatus());
    }

    @Test
    void somenteAInstituicaoSolicitanteOuOAdministradorConcluiADoacao() {
        SolicitacaoOferta solicitacao = service.solicitar(instituicao, oferta.getId(), 4);
        service.confirmarRetirada(doador, solicitacao.getId());

        assertEquals(403, assertThrows(ApiException.class,
                () -> service.concluir(doador, solicitacao.getId())).getStatusCode());
        assertEquals(403, assertThrows(ApiException.class,
                () -> service.concluir(outraInstituicao, solicitacao.getId())).getStatusCode());

        assertEquals(StatusSolicitacaoOferta.CONCLUIDA, service.concluir(admin, solicitacao.getId()).getStatus());
    }

    @Test
    void naoDevePermitirTransicoesForaDoFluxo() {
        SolicitacaoOferta solicitacao = service.solicitar(instituicao, oferta.getId(), 4);

        ApiException concluirCedo = assertThrows(ApiException.class,
                () -> service.concluir(instituicao, solicitacao.getId()));
        assertEquals(409, concluirCedo.getStatusCode());

        service.recusar(doador, solicitacao.getId());
        assertEquals(409, assertThrows(ApiException.class,
                () -> service.aceitar(doador, solicitacao.getId())).getStatusCode());
        assertEquals(409, assertThrows(ApiException.class,
                () -> service.agendar(doador, solicitacao.getId(), LocalDateTime.now().plusDays(1))).getStatusCode());
        assertEquals(409, assertThrows(ApiException.class,
                () -> service.cancelar(instituicao, solicitacao.getId())).getStatusCode());
        assertEquals(409, assertThrows(ApiException.class,
                () -> service.confirmarRetirada(doador, solicitacao.getId())).getStatusCode());
    }

    @Test
    void naoDeveCancelarDepoisDaRetiradaEPermiteReagendar() {
        SolicitacaoOferta solicitacao = service.solicitar(instituicao, oferta.getId(), 4);
        service.aceitar(doador, solicitacao.getId());
        service.agendar(doador, solicitacao.getId(), LocalDateTime.now().plusDays(1));

        LocalDateTime novaData = LocalDateTime.now().plusDays(3);
        assertEquals(novaData, service.agendar(doador, solicitacao.getId(), novaData).getRetiradaAgendadaPara());

        service.confirmarRetirada(doador, solicitacao.getId());
        assertEquals(409, assertThrows(ApiException.class,
                () -> service.cancelar(instituicao, solicitacao.getId())).getStatusCode());
    }

    @Test
    void deveValidarADataDoAgendamento() {
        Oferta comPrazo = ofertaService.cadastrar(doador, 10L, "Feijao", "Leguminosas", 10, "kg",
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(10), null, "Centro");
        SolicitacaoOferta solicitacao = service.solicitar(instituicao, comPrazo.getId(), 2);
        service.aceitar(doador, solicitacao.getId());

        assertEquals(400, assertThrows(ApiException.class, () -> service.agendar(
                doador, solicitacao.getId(), LocalDateTime.now().minusDays(1))).getStatusCode());
        ApiException depoisDoPrazo = assertThrows(ApiException.class, () -> service.agendar(
                doador, solicitacao.getId(), LocalDateTime.now().plusDays(5)));
        assertEquals(400, depoisDoPrazo.getStatusCode());
        assertTrue(depoisDoPrazo.getMessage().contains(LocalDate.now().plusDays(3).toString()));
        assertEquals(400, assertThrows(ApiException.class,
                () -> service.agendar(doador, solicitacao.getId(), null)).getStatusCode());

        assertEquals(StatusSolicitacaoOferta.AGENDADA,
                service.agendar(doador, solicitacao.getId(), LocalDateTime.now().plusDays(2)).getStatus());
    }

    private void cadastrarInstituicao(AuthenticatedUser usuario, String cnpj, boolean habilitar) {
        instituicaoService.cadastrar(usuario, "Instituicao " + usuario.id(), cnpj,
                "Rua B, 41", "(31) 3232-4444");
        if (habilitar) {
            instituicaoService.habilitar(usuario, true);
        }
    }

    private AuthenticatedUser usuario(long id, PerfilUsuario perfil) {
        return new AuthenticatedUser(id, "Usuario", id + "@teste.com", perfil);
    }
}
