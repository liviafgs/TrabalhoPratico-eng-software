package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.SolicitacaoOfertaService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SolicitacaoOfertaController {
    private final AuthService authService;
    private final SolicitacaoOfertaService solicitacaoService;

    public SolicitacaoOfertaController(AuthService authService, SolicitacaoOfertaService solicitacaoService) {
        this.authService = authService;
        this.solicitacaoService = solicitacaoService;
    }

    public void solicitar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        SolicitacaoOferta solicitacao = solicitacaoService.solicitar(
                user,
                Long.parseLong(RequestUtils.required(body, "idOferta")),
                RequestUtils.requiredDouble(body, "quantidade"));
        JsonResponse.send(exchange, 201, Map.of("solicitacao", toMap(solicitacao)));
    }

    public void confirmarRetirada(HttpExchange exchange) throws IOException {
        responder(exchange, solicitacaoService::confirmarRetirada);
    }

    public void cancelar(HttpExchange exchange) throws IOException {
        responder(exchange, solicitacaoService::cancelar);
    }

    public void aceitar(HttpExchange exchange) throws IOException {
        responder(exchange, solicitacaoService::aceitar);
    }

    public void recusar(HttpExchange exchange) throws IOException {
        responder(exchange, solicitacaoService::recusar);
    }

    public void concluir(HttpExchange exchange) throws IOException {
        responder(exchange, solicitacaoService::concluir);
    }

    public void agendar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(RequestUtils.required(body, "dataHoraRetirada"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data e hora invalidas. Use o formato AAAA-MM-DDTHH:MM.");
        }
        SolicitacaoOferta solicitacao = solicitacaoService.agendar(
                user, Long.parseLong(RequestUtils.required(body, "idSolicitacao")), dataHora);
        JsonResponse.send(exchange, 200, Map.of("solicitacao", toMap(solicitacao)));
    }

    private void responder(HttpExchange exchange,
            BiFunction<AuthenticatedUser, Long, SolicitacaoOferta> acao) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        SolicitacaoOferta solicitacao = acao.apply(
                user, Long.parseLong(RequestUtils.required(body, "idSolicitacao")));
        JsonResponse.send(exchange, 200, Map.of("solicitacao", toMap(solicitacao)));
    }

    private Map<String, Object> toMap(SolicitacaoOferta solicitacao) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", solicitacao.getId());
        map.put("idOferta", solicitacao.getIdOferta());
        map.put("idUsuarioInstituicao", solicitacao.getIdUsuarioInstituicao());
        map.put("quantidade", solicitacao.getQuantidade());
        map.put("status", solicitacao.getStatus().name());
        map.put("criadoEm", solicitacao.getCriadoEm().toString());
        map.put("retiradaAgendadaPara",
                solicitacao.getRetiradaAgendadaPara() == null ? null : solicitacao.getRetiradaAgendadaPara().toString());
        return map;
    }
}
