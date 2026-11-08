package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.CoerenciaDemandaService.ResultadoCoerencia;
import conexaosolidaria.service.DemandaConsulta;
import conexaosolidaria.service.DemandaFiltros;
import conexaosolidaria.service.DemandaService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
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
                RequestUtils.required(body, "prioridade"),
                RequestUtils.required(body, "finalidade"),
                requiredInt(body, "beneficiarios"),
                optionalDouble(body, "consumoPerCapita"),
                body.get("justificativa")
        );

        JsonResponse.send(exchange, 201, Map.of("demanda", demandaToMap(demanda)));
    }

    public void verificarCoerencia(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> body = RequestUtils.readJsonObject(exchange);

        double quantidade = RequestUtils.requiredDouble(body, "quantidade");
        ResultadoCoerencia resultado = demandaService.verificarCoerencia(
                user,
                RequestUtils.required(body, "alimento"),
                quantidade,
                RequestUtils.required(body, "unidadeMedida"),
                requiredInt(body, "beneficiarios"),
                optionalDouble(body, "consumoPerCapita")
        );

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("quantidadeSolicitada", quantidade);
        resposta.put("consumoPerCapita", resultado.consumoPerCapita());
        resposta.put("quantidadeReferencia", resultado.quantidadeReferencia());
        resposta.put("acimaDaReferencia", resultado.acimaDaReferencia());
        resposta.put("exigeJustificativa", resultado.acimaDaReferencia());
        resposta.put("mensagem", resultado.mensagem());
        JsonResponse.send(exchange, 200, resposta);
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> query = RequestUtils.queryParams(exchange);
        DemandaFiltros filtros = new DemandaFiltros(
                query.get("alimento"),
                query.get("prioridade"),
                parseData(query.get("prazoDe"), "prazoDe"),
                parseData(query.get("prazoAte"), "prazoAte"),
                query.get("localizacao"),
                query.containsKey("disponivel") ? Boolean.parseBoolean(query.get("disponivel")) : true
        );

        JsonResponse.send(exchange, 200, Map.of(
                "demandas",
                demandaService.listar(user, filtros).stream().map(this::consultaToMap).toList()
        ));
    }

    private LocalDate parseData(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Data invalida em " + fieldName + ". Use o formato AAAA-MM-DD.");
        }
    }

    private Map<String, Object> consultaToMap(DemandaConsulta consulta) {
        Map<String, Object> map = demandaToMap(consulta.demanda());
        map.put("instituicaoNome", consulta.instituicaoNome());
        map.put("instituicaoEndereco", consulta.instituicaoEndereco());
        return map;
    }

    private int requiredInt(Map<String, String> body, String fieldName) {
        try {
            return Integer.parseInt(RequestUtils.required(body, fieldName));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo numerico inteiro invalido: " + fieldName);
        }
    }

    private Double optionalDouble(Map<String, String> body, String fieldName) {
        String value = body.get(fieldName);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo numerico invalido: " + fieldName);
        }
    }

    private Map<String, Object> demandaToMap(Demanda demanda) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", demanda.getId());
        map.put("idUsuarioInstituicao", demanda.getIdUsuarioInstituicao());
        map.put("alimento", demanda.getAlimento());
        map.put("quantidade", demanda.getQuantidade());
        map.put("unidadeMedida", demanda.getUnidadeMedida());
        map.put("prazo", demanda.getPrazo().toString());
        map.put("prioridade", demanda.getPrioridade());
        map.put("finalidade", demanda.getFinalidade());
        map.put("beneficiarios", demanda.getBeneficiarios());
        map.put("consumoPerCapita", demanda.getConsumoPerCapita());
        map.put("quantidadeReferencia", demanda.getQuantidadeReferencia());
        map.put("acimaDaReferencia", demanda.isAcimaDaReferencia());
        map.put("justificativa", demanda.getJustificativa());
        return map;
    }
}
