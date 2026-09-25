package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.NutricionistaResponsavel;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.NutricionistaService;

import java.io.IOException;
import java.util.Map;

public class NutricionistaController {
    private final AuthService authService;
    private final NutricionistaService nutricionistaService;
    private final AccessControl accessControl;

    public NutricionistaController(AuthService authService, NutricionistaService nutricionistaService,
            AccessControl accessControl) {
        this.authService = authService;
        this.nutricionistaService = nutricionistaService;
        this.accessControl = accessControl;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CADASTRAR_NUTRICIONISTA);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        NutricionistaResponsavel nutricionista = nutricionistaService.cadastrar(
                user,
                parseLongRequired(body, "idInstituicao"),
                RequestUtils.required(body, "nome"),
                RequestUtils.required(body, "crn"),
                RequestUtils.required(body, "contato"));

        JsonResponse.send(exchange, 201, Map.of("nutricionista", toMap(nutricionista)));
    }

    public void consultar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_NUTRICIONISTAS);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        long idInstituicao = parseLongRequired(body, "idInstituicao");
        NutricionistaResponsavel nutricionista = nutricionistaService.consultarPorInstituicao(idInstituicao);
        JsonResponse.send(exchange, 200, Map.of("nutricionista", toMap(nutricionista)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_NUTRICIONISTAS);

        JsonResponse.send(exchange, 200, Map.of(
                "nutricionistas",
                nutricionistaService.listarTodos().stream().map(this::toMap).toList()));
    }

    private Map<String, Object> toMap(NutricionistaResponsavel nutricionista) {
        return Map.of(
                "id", nutricionista.getId(),
                "idUsuario", nutricionista.getIdUsuario(),
                "idInstituicao", nutricionista.getIdInstituicao(),
                "nome", nutricionista.getNome(),
                "crn", nutricionista.getCrn(),
                "contato", nutricionista.getContato(),
                "criadoEm", nutricionista.getCriadoEm().toString());
    }

    private long parseLongRequired(Map<String, String> body, String fieldName) {
        try {
            return Long.parseLong(RequestUtils.required(body, fieldName));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo numerico invalido: " + fieldName);
        }
    }
}
