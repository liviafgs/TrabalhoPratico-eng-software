package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.HistoricoSolicitacao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.StatusSolicitacaoOferta;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.HistoricoDoacaoItem;
import conexaosolidaria.service.HistoricoDoacaoService;
import conexaosolidaria.service.HistoricoFiltros;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HistoricoDoacaoController {
    private final AuthService authService;
    private final HistoricoDoacaoService historicoService;

    public HistoricoDoacaoController(AuthService authService, HistoricoDoacaoService historicoService) {
        this.authService = authService;
        this.historicoService = historicoService;
    }

    public void consultar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> query = RequestUtils.queryParams(exchange);
        HistoricoFiltros filtros = new HistoricoFiltros(
                query.get("alimento"),
                query.get("instituicao"),
                query.get("estabelecimento"),
                parseStatus(query.get("status")),
                parseData(query.get("de"), "de"),
                parseData(query.get("ate"), "ate"));

        JsonResponse.send(exchange, 200, Map.of(
                "solicitacoes",
                historicoService.consultar(user, filtros).stream().map(this::itemToMap).toList()));
    }

    public void linhaDoTempo(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> query = RequestUtils.queryParams(exchange);
        long idSolicitacao = Long.parseLong(RequestUtils.required(query, "idSolicitacao"));

        JsonResponse.send(exchange, 200, Map.of(
                "historico",
                historicoService.linhaDoTempo(user, idSolicitacao).stream().map(this::registroToMap).toList()));
    }

    private Set<StatusSolicitacaoOferta> parseStatus(String value) {
        Set<StatusSolicitacaoOferta> status = EnumSet.noneOf(StatusSolicitacaoOferta.class);
        if (value == null || value.isBlank()) {
            return status;
        }
        for (String parte : value.split(",")) {
            try {
                status.add(StatusSolicitacaoOferta.valueOf(parte.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status invalido: " + parte.trim() + ". Valores aceitos: "
                        + java.util.Arrays.toString(StatusSolicitacaoOferta.values()));
            }
        }
        return status;
    }

    private LocalDate parseData(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data invalida em " + fieldName + ". Use o formato AAAA-MM-DD.");
        }
    }

    private Map<String, Object> itemToMap(HistoricoDoacaoItem item) {
        SolicitacaoOferta solicitacao = item.solicitacao();
        Oferta oferta = item.oferta();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", solicitacao.getId());
        map.put("idOferta", oferta.getId());
        map.put("alimento", oferta.getAlimento());
        map.put("quantidade", solicitacao.getQuantidade());
        map.put("unidadeMedida", oferta.getUnidadeMedida());
        map.put("status", solicitacao.getStatus().name());
        map.put("idUsuarioInstituicao", solicitacao.getIdUsuarioInstituicao());
        map.put("instituicaoNome", item.instituicaoNome());
        map.put("idUsuarioEstabelecimento", oferta.getIdUsuarioDoador());
        map.put("estabelecimentoNome", item.estabelecimentoNome());
        map.put("localRetirada", oferta.getLocalRetirada());
        map.put("retiradaAgendadaPara",
                solicitacao.getRetiradaAgendadaPara() == null ? null : solicitacao.getRetiradaAgendadaPara().toString());
        map.put("criadoEm", solicitacao.getCriadoEm().toString());
        map.put("atualizadoEm", item.atualizadoEm().toString());
        return map;
    }

    private Map<String, Object> registroToMap(HistoricoSolicitacao registro) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusAnterior", registro.statusAnterior() == null ? null : registro.statusAnterior().name());
        map.put("statusNovo", registro.statusNovo().name());
        map.put("idUsuario", registro.idUsuario());
        map.put("registradoEm", registro.registradoEm().toString());
        return map;
    }
}
