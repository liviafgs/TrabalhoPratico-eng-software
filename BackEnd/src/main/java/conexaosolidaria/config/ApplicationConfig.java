package conexaosolidaria.config;

import conexaosolidaria.controller.AuthController;
import conexaosolidaria.controller.DemandaController;
import conexaosolidaria.controller.FuncionalidadeController;
import conexaosolidaria.controller.OfertaController;
import conexaosolidaria.controller.UsuarioController;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.UsuarioRepository;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.security.PasswordHasher;
import conexaosolidaria.security.TokenService;
import conexaosolidaria.server.ApplicationServer;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.DemandaService;
import conexaosolidaria.service.OfertaService;
import conexaosolidaria.service.UsuarioService;

public class ApplicationConfig {
    public ApplicationServer createServer(int port) {
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        OfertaRepository ofertaRepository = new OfertaRepository();
        DemandaRepository demandaRepository = new DemandaRepository();

        PasswordHasher passwordHasher = new PasswordHasher();
        TokenService tokenService = new TokenService();
        AccessControl accessControl = new AccessControl();

        UsuarioService usuarioService = new UsuarioService(usuarioRepository, passwordHasher);
        AuthService authService = new AuthService(usuarioRepository, passwordHasher, tokenService);
        OfertaService ofertaService = new OfertaService(ofertaRepository, accessControl);
        DemandaService demandaService = new DemandaService(demandaRepository, accessControl);

        UsuarioController usuarioController = new UsuarioController(usuarioService, authService, accessControl);
        AuthController authController = new AuthController(authService);
        FuncionalidadeController funcionalidadeController = new FuncionalidadeController(authService, accessControl);
        OfertaController ofertaController = new OfertaController(authService, ofertaService);
        DemandaController demandaController = new DemandaController(authService, demandaService);

        ApplicationServer server = new ApplicationServer(port);
        server.register("POST", "/usuarios", usuarioController::cadastrar);
        server.register("GET", "/usuarios", usuarioController::listar);
        server.register("POST", "/auth/login", authController::login);
        server.register("GET", "/funcionalidades", funcionalidadeController::listarPermitidas);
        server.register("POST", "/ofertas", ofertaController::cadastrar);
        server.register("GET", "/ofertas", ofertaController::listar);
        server.register("POST", "/demandas", demandaController::cadastrar);
        server.register("GET", "/demandas", demandaController::listar);

        return server;
    }
}
