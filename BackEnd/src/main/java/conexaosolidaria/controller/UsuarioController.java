package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.dto.UsuarioResponse;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.UsuarioService;

import java.io.IOException;
import java.util.Map;

public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthService authService;
    private final AccessControl accessControl;

    public UsuarioController(UsuarioService usuarioService, AuthService authService, AccessControl accessControl) {
        this.usuarioService = usuarioService;
        this.authService = authService;
        this.accessControl = accessControl;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        UsuarioResponse usuario = UsuarioResponse.from(usuarioService.cadastrar(
                RequestUtils.required(body, "nome"),
                RequestUtils.required(body, "email"),
                RequestUtils.required(body, "senha"),
                RequestUtils.required(body, "perfil")
        ));

        JsonResponse.send(exchange, 201, Map.of("usuario", usuarioToMap(usuario)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_USUARIOS);

        JsonResponse.send(exchange, 200, Map.of(
                "usuarios",
                usuarioService.listarTodos().stream()
                        .map(UsuarioResponse::from)
                        .map(this::usuarioToMap)
                        .toList()
        ));
    }

    private Map<String, Object> usuarioToMap(UsuarioResponse usuario) {
        return Map.of(
                "id", usuario.id(),
                "nome", usuario.nome(),
                "email", usuario.email(),
                "perfil", usuario.perfil(),
                "perfilDescricao", usuario.perfilDescricao()
        );
    }
}
