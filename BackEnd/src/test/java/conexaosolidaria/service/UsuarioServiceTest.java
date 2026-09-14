package conexaosolidaria.service;

import conexaosolidaria.dto.CadastroUsuarioRequest;
import conexaosolidaria.exception.CadastroUsuarioException;
import conexaosolidaria.model.TipoUsuario;
import conexaosolidaria.model.Usuario;
import conexaosolidaria.repository.UsuarioRepository;
import conexaosolidaria.util.ConexaoBanco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioServiceTest {
    @Test
    void deveCriptografarSenhaAoCadastrarUsuario() {
        UsuarioRepository repository = new UsuarioRepositoryFake(false);
        UsuarioService service = new UsuarioService(repository);
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Maria Silva",
                "maria@email.com",
                "senha123",
                "(31) 99999-9999",
                TipoUsuario.INSTITUICAO_BENEFICIARIA
        );

        Usuario usuario = service.cadastrar(request);

        assertTrue(usuario.getSenha().startsWith("$2a$"));
        assertTrue(service.senhaConfere("senha123", usuario.getSenha()));
    }

    @Test
    void deveRecusarEmailRepetido() {
        UsuarioRepository repository = new UsuarioRepositoryFake(true);
        UsuarioService service = new UsuarioService(repository);
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Maria Silva",
                "maria@email.com",
                "senha123",
                null,
                TipoUsuario.INSTITUICAO_BENEFICIARIA
        );

        assertThrows(CadastroUsuarioException.class, () -> service.cadastrar(request));
    }

    @Test
    void deveRecusarDadosInvalidos() {
        UsuarioRepository repository = new UsuarioRepositoryFake(false);
        UsuarioService service = new UsuarioService(repository);
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "",
                "email-invalido",
                "123",
                null,
                null
        );

        assertThrows(CadastroUsuarioException.class, () -> service.cadastrar(request));
    }

    @Test
    void deveAceitarTelefoneVazio() {
        UsuarioRepository repository = new UsuarioRepositoryFake(false);
        UsuarioService service = new UsuarioService(repository);
        CadastroUsuarioRequest request = new CadastroUsuarioRequest(
                "Joao Silva",
                "joao@email.com",
                "senha123",
                "",
                TipoUsuario.ESTABELECIMENTO_DOADOR
        );

        assertDoesNotThrow(() -> service.cadastrar(request));
    }

    private static class UsuarioRepositoryFake extends UsuarioRepository {
        private final boolean emailExistente;

        UsuarioRepositoryFake(boolean emailExistente) {
            super(new ConexaoBanco());
            this.emailExistente = emailExistente;
        }

        @Override
        public Usuario salvar(Usuario usuario) {
            usuario.setId(1L);
            return usuario;
        }

        @Override
        public boolean existePorEmail(String email) {
            return emailExistente;
        }
    }
}
