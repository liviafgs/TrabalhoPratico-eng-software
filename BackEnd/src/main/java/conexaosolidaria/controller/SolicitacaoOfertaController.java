package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.SolicitacaoOfertaService;

import java.io.IOException;
import java.util.Map;

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
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        SolicitacaoOferta solicitacao = solicitacaoService.confirmarRetirada(
                user, Long.parseLong(RequestUtils.required(body, "idSolicitacao")));
        JsonResponse.send(exchange, 200, Map.of("solicitacao", toMap(solicitacao)));
    }

    public void cancelar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        SolicitacaoOferta solicitacao = solicitacaoService.cancelar(
                user, Long.parseLong(RequestUtils.required(body, "idSolicitacao")));
        JsonResponse.send(exchange, 200, Map.of("solicitacao", toMap(solicitacao)));
    }

    private Map<String, Object> toMap(SolicitacaoOferta solicitacao) {
        return Map.of(
                "id", solicitacao.getId(),
                "idOferta", solicitacao.getIdOferta(),
                "idUsuarioInstituicao", solicitacao.getIdUsuarioInstituicao(),
                "quantidade", solicitacao.getQuantidade(),
                "status", solicitacao.getStatus().name(),
                "criadoEm", solicitacao.getCriadoEm().toString());
    }
}
