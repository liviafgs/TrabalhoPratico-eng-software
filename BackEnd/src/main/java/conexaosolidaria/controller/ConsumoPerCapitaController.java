package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.ConsumoPerCapita;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.ConsumoPerCapitaService;

import java.io.IOException;
import java.util.Map;

public class ConsumoPerCapitaController {
    private final AuthService authService;
    private final ConsumoPerCapitaService consumoService;

    public ConsumoPerCapitaController(AuthService authService, ConsumoPerCapitaService consumoService) {
        this.authService = authService;
        this.consumoService = consumoService;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);

        ConsumoPerCapita consumo = consumoService.cadastrar(
                user,
                RequestUtils.required(body, "alimento"),
                RequestUtils.required(body, "unidadeMedida"),
                RequestUtils.requiredDouble(body, "quantidadePorPessoa"));

        JsonResponse.send(exchange, 201, Map.of("consumoPerCapita", toMap(consumo)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        JsonResponse.send(exchange, 200, Map.of(
                "consumosPerCapita",
                consumoService.listar(user).stream().map(this::toMap).toList()));
    }

    private Map<String, Object> toMap(ConsumoPerCapita consumo) {
        return Map.of(
                "id", consumo.getId(),
                "alimento", consumo.getAlimento(),
                "unidadeMedida", consumo.getUnidadeMedida(),
                "quantidadePorPessoa", consumo.getQuantidadePorPessoa());
    }
}
