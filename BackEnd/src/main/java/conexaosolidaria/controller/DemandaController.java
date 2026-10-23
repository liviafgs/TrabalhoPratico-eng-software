package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.DemandaService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class DemandaController {
    private final AuthService authService;
    private final DemandaService demandaService;

    public DemandaController(AuthService authService, DemandaService demandaService) {
        this.authService = authService;
        this.demandaService = demandaService;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);

        Demanda demanda = demandaService.cadastrar(
                user,
                RequestUtils.required(body, "alimento"),
                RequestUtils.requiredDouble(body, "quantidade"),
                RequestUtils.required(body, "unidadeMedida"),
                LocalDate.parse(RequestUtils.required(body, "prazo")),
                RequestUtils.required(body, "prioridade")
        );

        JsonResponse.send(exchange, 201, Map.of("demanda", demandaToMap(demanda)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        JsonResponse.send(exchange, 200, Map.of(
                "demandas",
                demandaService.listar(user).stream().map(this::demandaToMap).toList()
        ));
    }

    private Map<String, Object> demandaToMap(Demanda demanda) {
        return Map.of(
                "id", demanda.getId(),
                "idUsuarioInstituicao", demanda.getIdUsuarioInstituicao(),
                "alimento", demanda.getAlimento(),
                "quantidade", demanda.getQuantidade(),
                "unidadeMedida", demanda.getUnidadeMedida(),
                "prazo", demanda.getPrazo().toString(),
                "prioridade", demanda.getPrioridade()
        );
    }
}
