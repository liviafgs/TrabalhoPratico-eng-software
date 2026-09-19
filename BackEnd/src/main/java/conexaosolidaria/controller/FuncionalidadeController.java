package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;

import java.io.IOException;
import java.util.Map;

public class FuncionalidadeController {
    private final AuthService authService;
    private final AccessControl accessControl;

    public FuncionalidadeController(AuthService authService, AccessControl accessControl) {
        this.authService = authService;
        this.accessControl = accessControl;
    }

    public void listarPermitidas(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));

        JsonResponse.send(exchange, 200, Map.of(
                "usuario", Map.of(
                        "id", user.id(),
                        "nome", user.nome(),
                        "email", user.email(),
                        "perfil", user.perfil().name(),
                        "perfilDescricao", user.perfil().getDescricao()
                ),
                "funcionalidades",
                accessControl.permissionsOf(user.perfil()).stream()
                        .map(this::permissaoToMap)
                        .toList()
        ));
    }

    private Map<String, Object> permissaoToMap(Permissao permissao) {
        return Map.of(
                "codigo", permissao.name(),
                "descricao", permissao.getDescricao()
        );
    }
}
