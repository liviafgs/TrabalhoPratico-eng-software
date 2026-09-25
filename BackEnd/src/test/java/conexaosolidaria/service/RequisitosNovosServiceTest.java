package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Alimento;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.PerfilAtendimentoInstituicao;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.StatusInstituicao;
import conexaosolidaria.repository.AlimentoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.PerfilAtendimentoInstituicaoRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequisitosNovosServiceTest {

    @Test
    void deveHabilitarInstituicaoERegistrarPerfilDeAtendimento() {
        InstituicaoRepository instituicaoRepository = new InstituicaoRepository();
        PerfilAtendimentoInstituicaoRepository perfilRepository = new PerfilAtendimentoInstituicaoRepository();

        InstituicaoService instituicaoService = new InstituicaoService(instituicaoRepository);
        PerfilAtendimentoInstituicaoService perfilService = new PerfilAtendimentoInstituicaoService(perfilRepository,
                instituicaoRepository);

        AuthenticatedUser usuario = new AuthenticatedUser(30L, "Instituicao Teste", "teste@instituicao.com",
                PerfilUsuario.INSTITUICAO_BENEFICIARIA);
        Instituicao instituicao = instituicaoService.cadastrar(
                usuario,
                "Instituicao Teste",
                "11222333000144",
                "Rua B, 41",
                "(31) 3232-4444");

        instituicaoService.habilitar(usuario, true);
        PerfilAtendimentoInstituicao perfil = perfilService.cadastrar(
                usuario,
                instituicao.getId(),
                250,
                350,
                "Crianças e idosos",
                "Semanal");

        assertEquals(StatusInstituicao.HABILITADA, instituicaoService.consultarPorUsuario(usuario.id()).getStatus());
        assertEquals(250, perfil.getNumeroBeneficiarios());
        assertEquals("Crianças e idosos", perfil.getPublicoAtendido());
    }

    @Test
    void deveCadastrarAlimentoEConsultaLo() {
        AlimentoRepository alimentoRepository = new AlimentoRepository();
        AlimentoService alimentoService = new AlimentoService(alimentoRepository);

        Alimento alimento = alimentoService.cadastrar(
                "Arroz",
                "Cereais",
                "kg");

        assertEquals("Arroz", alimento.getNome());
        assertEquals("Cereais", alimento.getCategoria());
        assertEquals(alimento, alimentoService.consultarPorNome("Arroz"));
    }
}
