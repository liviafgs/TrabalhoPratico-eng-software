package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.model.Alimento;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AlimentoService;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.dto.AuthenticatedUser;

import java.io.IOException;
import java.util.Map;

public class AlimentoController {
    private final AuthService authService;
    private final AlimentoService alimentoService;
    private final AccessControl accessControl;

    public AlimentoController(AuthService authService, AlimentoService alimentoService, AccessControl accessControl) {
        this.authService = authService;
        this.alimentoService = alimentoService;
        this.accessControl = accessControl;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CADASTRAR_ALIMENTO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        Alimento alimento = alimentoService.cadastrar(
                RequestUtils.required(body, "nome"),
                RequestUtils.required(body, "categoria"),
                RequestUtils.required(body, "unidadeMedida"));

        JsonResponse.send(exchange, 201, Map.of("alimento", toMap(alimento)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_ALIMENTOS);

        JsonResponse.send(exchange, 200, Map.of(
                "alimentos",
                alimentoService.listarTodos().stream().map(this::toMap).toList()));
    }

    private Map<String, Object> toMap(Alimento alimento) {
        return Map.of(
                "id", alimento.getId(),
                "nome", alimento.getNome(),
                "categoria", alimento.getCategoria(),
                "unidadeMedida", alimento.getUnidadeMedida(),
                "descricao", alimento.getDescricao() == null ? "" : alimento.getDescricao(),
                "criadoEm", alimento.getCriadoEm().toString());
    }
}
