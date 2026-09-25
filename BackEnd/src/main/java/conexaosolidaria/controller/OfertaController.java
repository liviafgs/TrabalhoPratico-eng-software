package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.OfertaFiltros;
import conexaosolidaria.service.OfertaService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
                                body.get("categoria"),
                                RequestUtils.requiredDouble(body, "quantidade"),
                                RequestUtils.required(body, "unidadeMedida"),
                                LocalDate.parse(RequestUtils.required(body, "retiradaAte")),
                                body.containsKey("validade") && !body.get("validade").isBlank()
                                                ? LocalDate.parse(body.get("validade"))
                                                : null,
                                body.containsKey("dataHoraRetirada") && !body.get("dataHoraRetirada").isBlank()
                                                ? LocalDateTime.parse(body.get("dataHoraRetirada"))
                                                : null,
                                body.get("localRetirada"));

                JsonResponse.send(exchange, 201, Map.of("oferta", ofertaToMap(oferta)));
        }

        public void listar(HttpExchange exchange) throws IOException {
                AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
                Map<String, String> query = RequestUtils.queryParams(exchange);
                OfertaFiltros filtros = new OfertaFiltros(
                                query.get("alimento"),
                                query.get("categoria"),
                                query.get("localizacao"),
                                parseDate(query.get("data")),
                                query.containsKey("disponivel") ? Boolean.parseBoolean(query.get("disponivel")) : true);
                JsonResponse.send(exchange, 200, Map.of(
                                "ofertas",
                                ofertaService.consultarDisponiveis(user, filtros).stream().map(this::ofertaToMap)
                                                .toList()));
        }

        private Map<String, Object> ofertaToMap(Oferta oferta) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("id", oferta.getId());
                response.put("idUsuarioDoador", oferta.getIdUsuarioDoador());
                response.put("idAlimento", oferta.getIdAlimento());
                response.put("alimento", oferta.getAlimento());
                response.put("categoria", oferta.getCategoria());
                response.put("quantidade", oferta.getQuantidade());
                response.put("quantidadeDisponivel", ofertaService.quantidadeDisponivel(oferta));
                response.put("unidadeMedida", oferta.getUnidadeMedida());
                response.put("retiradaAte",
                                oferta.getRetiradaAte() == null ? null : oferta.getRetiradaAte().toString());
                response.put("validade", oferta.getValidade() == null ? null : oferta.getValidade().toString());
                response.put("dataHoraRetirada",
                                oferta.getDataHoraRetirada() == null ? null : oferta.getDataHoraRetirada().toString());
                response.put("localRetirada", oferta.getLocalRetirada());
                response.put("expirada", oferta.isExpirada());
                response.put("disponivel", !oferta.isExpirada() && ofertaService.quantidadeDisponivel(oferta) > 0);
                return response;
        }

        private LocalDate parseDate(String value) {
                return value == null || value.isBlank() ? null : LocalDate.parse(value);
        }
}
