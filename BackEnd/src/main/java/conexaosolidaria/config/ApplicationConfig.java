package conexaosolidaria.config;

import conexaosolidaria.controller.AlimentoController;
import conexaosolidaria.controller.AuthController;
import conexaosolidaria.controller.DemandaController;
import conexaosolidaria.controller.EstabelecimentoController;
import conexaosolidaria.controller.FuncionalidadeController;
import conexaosolidaria.controller.InstituicaoController;
import conexaosolidaria.controller.NutricionistaController;
import conexaosolidaria.controller.OfertaController;
import conexaosolidaria.controller.PerfilAtendimentoInstituicaoController;
import conexaosolidaria.controller.SolicitacaoOfertaController;
import conexaosolidaria.controller.UsuarioController;
import conexaosolidaria.repository.AlimentoRepository;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.EstabelecimentoRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.repository.NutricionistaRepository;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.repository.PerfilAtendimentoInstituicaoRepository;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;
import conexaosolidaria.repository.UsuarioRepository;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.security.PasswordHasher;
import conexaosolidaria.security.TokenService;
import conexaosolidaria.server.ApplicationServer;
import conexaosolidaria.service.AlimentoService;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.DemandaService;
import conexaosolidaria.service.EstabelecimentoService;
import conexaosolidaria.service.InstituicaoService;
import conexaosolidaria.service.NutricionistaService;
import conexaosolidaria.service.OfertaService;
import conexaosolidaria.service.PerfilAtendimentoInstituicaoService;
import conexaosolidaria.service.SolicitacaoOfertaService;
import conexaosolidaria.service.UsuarioService;

public class ApplicationConfig {
        public ApplicationServer createServer(int port) {
                UsuarioRepository usuarioRepository = new UsuarioRepository();
                OfertaRepository ofertaRepository = new OfertaRepository();
                SolicitacaoOfertaRepository solicitacaoOfertaRepository = new SolicitacaoOfertaRepository();
                DemandaRepository demandaRepository = new DemandaRepository();
                EstabelecimentoRepository estabelecimentoRepository = new EstabelecimentoRepository();
                InstituicaoRepository instituicaoRepository = new InstituicaoRepository();
                NutricionistaRepository nutricionistaRepository = new NutricionistaRepository();
                AlimentoRepository alimentoRepository = new AlimentoRepository();
                PerfilAtendimentoInstituicaoRepository perfilAtendimentoRepository = new PerfilAtendimentoInstituicaoRepository();

                PasswordHasher passwordHasher = new PasswordHasher();
                TokenService tokenService = new TokenService();
                AccessControl accessControl = new AccessControl();

                UsuarioService usuarioService = new UsuarioService(usuarioRepository, passwordHasher);
                AuthService authService = new AuthService(usuarioRepository, passwordHasher, tokenService);
                SolicitacaoOfertaService solicitacaoOfertaService = new SolicitacaoOfertaService(
                                ofertaRepository, solicitacaoOfertaRepository, accessControl);
                OfertaService ofertaService = new OfertaService(ofertaRepository, accessControl,
                                solicitacaoOfertaService);
                DemandaService demandaService = new DemandaService(demandaRepository, accessControl);
                EstabelecimentoService estabelecimentoService = new EstabelecimentoService(estabelecimentoRepository);
                InstituicaoService instituicaoService = new InstituicaoService(instituicaoRepository);
                NutricionistaService nutricionistaService = new NutricionistaService(nutricionistaRepository,
                                instituicaoRepository);
                AlimentoService alimentoService = new AlimentoService(alimentoRepository);
                PerfilAtendimentoInstituicaoService perfilAtendimentoService = new PerfilAtendimentoInstituicaoService(
                                perfilAtendimentoRepository, instituicaoRepository);

                UsuarioController usuarioController = new UsuarioController(usuarioService, authService, accessControl);
                AuthController authController = new AuthController(authService);
                FuncionalidadeController funcionalidadeController = new FuncionalidadeController(authService,
                                accessControl);
                OfertaController ofertaController = new OfertaController(authService, ofertaService);
                DemandaController demandaController = new DemandaController(authService, demandaService);
                EstabelecimentoController estabelecimentoController = new EstabelecimentoController(authService,
                                estabelecimentoService, accessControl);
                InstituicaoController instituicaoController = new InstituicaoController(authService, instituicaoService,
                                accessControl);
                NutricionistaController nutricionistaController = new NutricionistaController(authService,
                                nutricionistaService,
                                accessControl);
                AlimentoController alimentoController = new AlimentoController(authService, alimentoService,
                                accessControl);
                PerfilAtendimentoInstituicaoController perfilAtendimentoController = new PerfilAtendimentoInstituicaoController(
                                authService, perfilAtendimentoService, accessControl);
                SolicitacaoOfertaController solicitacaoOfertaController = new SolicitacaoOfertaController(
                                authService, solicitacaoOfertaService);

                ApplicationServer server = new ApplicationServer(port);
                server.register("POST", "/usuarios", usuarioController::cadastrar);
                server.register("GET", "/usuarios", usuarioController::listar);
                server.register("POST", "/auth/login", authController::login);
                server.register("GET", "/funcionalidades", funcionalidadeController::listarPermitidas);
                server.register("POST", "/estabelecimentos", estabelecimentoController::cadastrar);
                server.register("GET", "/estabelecimentos", estabelecimentoController::listar);
                server.register("GET", "/estabelecimentos/me", estabelecimentoController::consultar);
                server.register("PUT", "/estabelecimentos", estabelecimentoController::atualizar);
                server.register("POST", "/instituicoes", instituicaoController::cadastrar);
                server.register("GET", "/instituicoes", instituicaoController::listar);
                server.register("GET", "/instituicoes/me", instituicaoController::consultar);
                server.register("PUT", "/instituicoes", instituicaoController::atualizar);
                server.register("POST", "/instituicoes/habilitar", instituicaoController::habilitar);
                server.register("POST", "/instituicoes/perfil-atendimento", perfilAtendimentoController::cadastrar);
                server.register("GET", "/instituicoes/perfil-atendimento", perfilAtendimentoController::consultar);
                server.register("POST", "/nutricionistas", nutricionistaController::cadastrar);
                server.register("GET", "/nutricionistas", nutricionistaController::listar);
                server.register("GET", "/nutricionistas/consultar", nutricionistaController::consultar);
                server.register("POST", "/alimentos", alimentoController::cadastrar);
                server.register("GET", "/alimentos", alimentoController::listar);
                server.register("POST", "/ofertas", ofertaController::cadastrar);
                server.register("GET", "/ofertas", ofertaController::listar);
                server.register("POST", "/ofertas/solicitacoes", solicitacaoOfertaController::solicitar);
                server.register("POST", "/ofertas/retiradas/confirmar", solicitacaoOfertaController::confirmarRetirada);
                server.register("POST", "/ofertas/solicitacoes/cancelar", solicitacaoOfertaController::cancelar);
                server.register("POST", "/demandas", demandaController::cadastrar);
                server.register("GET", "/demandas", demandaController::listar);

                return server;
        }
}
