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
                RequestUtils.required(body, "alimento"),
                RequestUtils.requiredDouble(body, "quantidade"),
                RequestUtils.required(body, "unidadeMedida"),
                LocalDate.parse(RequestUtils.required(body, "retiradaAte"))
        );

        JsonResponse.send(exchange, 201, Map.of("oferta", ofertaToMap(oferta)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        JsonResponse.send(exchange, 200, Map.of(
                "ofertas",
                ofertaService.listar(user).stream().map(this::ofertaToMap).toList()
        ));
    }

    private Map<String, Object> ofertaToMap(Oferta oferta) {
        return Map.of(
                "id", oferta.getId(),
                "idUsuarioDoador", oferta.getIdUsuarioDoador(),
                "alimento", oferta.getAlimento(),
                "quantidade", oferta.getQuantidade(),
                "unidadeMedida", oferta.getUnidadeMedida(),
                "retiradaAte", oferta.getRetiradaAte().toString()
        );
    }
}
