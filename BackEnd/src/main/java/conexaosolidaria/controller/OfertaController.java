package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.OfertaService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class OfertaController {
    private final AuthService authService;
    private final OfertaService ofertaService;

    public OfertaController(AuthService authService, OfertaService ofertaService) {
        this.authService = authService;
        this.ofertaService = ofertaService;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);

        Oferta oferta = ofertaService.cadastrar(
                user,
                body.containsKey("idAlimento") && !body.get("idAlimento").isBlank()
                        ? Long.parseLong(body.get("idAlimento"))
                        : 0L,
                RequestUtils.required(body, "alimento"),
                RequestUtils.requiredDouble(body, "quantidade"),
                RequestUtils.required(body, "unidadeMedida"),
                LocalDate.parse(RequestUtils.required(body, "retiradaAte")),
                body.containsKey("validade") && !body.get("validade").isBlank() ? LocalDate.parse(body.get("validade"))
                        : null,
                body.containsKey("dataHoraRetirada") && !body.get("dataHoraRetirada").isBlank()
                        ? LocalDateTime.parse(body.get("dataHoraRetirada"))
                        : null,
                body.get("localRetirada"));

        JsonResponse.send(exchange, 201, Map.of("oferta", ofertaToMap(oferta)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        JsonResponse.send(exchange, 200, Map.of(
                "ofertas",
                ofertaService.listar(user).stream().map(this::ofertaToMap).toList()));
    }

    private Map<String, Object> ofertaToMap(Oferta oferta) {
        return Map.of(
                "id", oferta.getId(),
                "idUsuarioDoador", oferta.getIdUsuarioDoador(),
                "idAlimento", oferta.getIdAlimento(),
                "alimento", oferta.getAlimento(),
                "quantidade", oferta.getQuantidade(),
                "unidadeMedida", oferta.getUnidadeMedida(),
                "retiradaAte", oferta.getRetiradaAte() == null ? null : oferta.getRetiradaAte().toString(),
                "validade", oferta.getValidade() == null ? null : oferta.getValidade().toString(),
                "dataHoraRetirada",
                oferta.getDataHoraRetirada() == null ? null : oferta.getDataHoraRetirada().toString(),
                "localRetirada", oferta.getLocalRetirada() == null ? "" : oferta.getLocalRetirada());
    }
}
