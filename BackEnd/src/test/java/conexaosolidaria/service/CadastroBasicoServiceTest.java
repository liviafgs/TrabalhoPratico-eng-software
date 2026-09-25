package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Estabelecimento;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.NutricionistaResponsavel;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.NutricionistaRepository;
import conexaosolidaria.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CadastroBasicoServiceTest {

    @Test
    void deveCadastrarEConsultarEstabelecimentoDoUsuario() {
        EstabelecimentoRepository repository = new EstabelecimentoRepository();
        EstabelecimentoService service = new EstabelecimentoService(repository, null);

        Estabelecimento estabelecimento = service.cadastrar(
                new AuthenticatedUser(10L, "Mercado", "mercado@email.com", PerfilUsuario.ESTABELECIMENTO_DOADOR),
                "Mercado Vida Boa",
                "12345678000199",
                "Rua das Flores, 100",
                "(11) 99999-0000");

        assertEquals("Mercado Vida Boa", estabelecimento.getNome());
        assertEquals(10L, estabelecimento.getIdUsuario());
        assertEquals(estabelecimento, service.consultarPorUsuario(10L));
    }

    @Test
    void deveCadastrarInstituicaoEResponsavelTecnico() {
        InstituicaoRepository instituicaoRepository = new InstituicaoRepository();
        NutricionistaRepository nutricionistaRepository = new NutricionistaRepository();

        InstituicaoService instituicaoService = new InstituicaoService(instituicaoRepository, null);
        NutricionistaService nutricionistaService = new NutricionistaService(nutricionistaRepository,
                instituicaoRepository, null);

        AuthenticatedUser usuario = new AuthenticatedUser(20L, "Casa de Apoio", "apoio@email.com",
                PerfilUsuario.INSTITUICAO_BENEFICIARIA);
        Instituicao instituicao = instituicaoService.cadastrar(
                usuario,
                "Casa de Apoio Comunitaria",
                "98765432000188",
                "Av. Central, 77",
                "(21) 3333-4444");

        NutricionistaResponsavel nutricionista = nutricionistaService.cadastrar(
                usuario,
                instituicao.getId(),
                "Dr. Ana Souza",
                "CRN-SP 12345",
                "(21) 98888-7777");

        assertEquals("Casa de Apoio Comunitaria", instituicao.getNome());
        assertEquals("CRN-SP 12345", nutricionista.getCrn());
        assertEquals(instituicao.getId(), nutricionista.getIdInstituicao());
    }
}
