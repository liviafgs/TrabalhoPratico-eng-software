package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Notificacao;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.NotificacaoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class NotificacaoController {
    private final AuthService authService;
    private final NotificacaoService notificacaoService;

    public NotificacaoController(AuthService authService, NotificacaoService notificacaoService) {
        this.authService = authService;
        this.notificacaoService = notificacaoService;
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> query = RequestUtils.queryParams(exchange);

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("naoLidas", notificacaoService.contarNaoLidas(user));
        resposta.put("notificacoes", notificacaoService.listar(user, parseLida(query.get("lida"))).stream()
                .map(this::toMap).toList());
        JsonResponse.send(exchange, 200, resposta);
    }

    public void marcarComoLida(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        Notificacao notificacao = notificacaoService.marcarComoLida(
                user, Long.parseLong(RequestUtils.required(body, "idNotificacao")));
        JsonResponse.send(exchange, 200, Map.of("notificacao", toMap(notificacao)));
    }

    public void marcarTodasComoLidas(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        JsonResponse.send(exchange, 200, Map.of("marcadas", notificacaoService.marcarTodasComoLidas(user)));
    }

    private Boolean parseLida(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        throw new IllegalArgumentException("Valor invalido em lida. Use true ou false.");
    }

    private Map<String, Object> toMap(Notificacao notificacao) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", notificacao.getId());
        map.put("tipo", notificacao.getTipo().name());
        map.put("mensagem", notificacao.getMensagem());
        map.put("idSolicitacao", notificacao.getIdSolicitacao());
        map.put("idOferta", notificacao.getIdOferta());
        map.put("idDemanda", notificacao.getIdDemanda());
        map.put("lida", notificacao.isLida());
        map.put("criadaEm", notificacao.getCriadaEm().toString());
        map.put("lidaEm", notificacao.getLidaEm() == null ? null : notificacao.getLidaEm().toString());
        return map;
    }
}
