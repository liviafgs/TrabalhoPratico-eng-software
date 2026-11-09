package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.Correspondencia;
import conexaosolidaria.service.Oportunidade;
import conexaosolidaria.service.OportunidadeService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OportunidadeController {
    private final AuthService authService;
    private final OportunidadeService oportunidadeService;

    public OportunidadeController(AuthService authService, OportunidadeService oportunidadeService) {
        this.authService = authService;
        this.oportunidadeService = oportunidadeService;
    }

    public void consultar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        Map<String, String> query = RequestUtils.queryParams(exchange);

        JsonResponse.send(exchange, 200, Map.of(
                "oportunidades",
                oportunidadeService.consultar(user, parseId(query.get("idOferta"), "idOferta"),
                        parseId(query.get("idDemanda"), "idDemanda")).stream().map(this::toMap).toList()));
    }

    private Long parseId(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identificador invalido em " + fieldName + ".");
        }
    }

    private Map<String, Object> toMap(Oportunidade oportunidade) {
        Correspondencia correspondencia = oportunidade.correspondencia();
        Oferta oferta = correspondencia.oferta();
        Demanda demanda = correspondencia.demanda();

        Map<String, Object> ofertaMap = new LinkedHashMap<>();
        ofertaMap.put("id", oferta.getId());
        ofertaMap.put("alimento", oferta.getAlimento());
        ofertaMap.put("categoria", oferta.getCategoria());
        ofertaMap.put("unidadeMedida", oferta.getUnidadeMedida());
        ofertaMap.put("quantidadeDisponivel", correspondencia.quantidadeDisponivel());
        ofertaMap.put("retiradaAte", oferta.getRetiradaAte() == null ? null : oferta.getRetiradaAte().toString());
        ofertaMap.put("localRetirada", oferta.getLocalRetirada());
        ofertaMap.put("idUsuarioEstabelecimento", oferta.getIdUsuarioDoador());
        ofertaMap.put("estabelecimentoNome", oportunidade.estabelecimentoNome());

        Map<String, Object> demandaMap = new LinkedHashMap<>();
        demandaMap.put("id", demanda.getId());
        demandaMap.put("alimento", demanda.getAlimento());
        demandaMap.put("quantidade", demanda.getQuantidade());
        demandaMap.put("unidadeMedida", demanda.getUnidadeMedida());
        demandaMap.put("prazo", demanda.getPrazo().toString());
        demandaMap.put("prioridade", demanda.getPrioridade());
        demandaMap.put("finalidade", demanda.getFinalidade());
        demandaMap.put("idUsuarioInstituicao", demanda.getIdUsuarioInstituicao());
        demandaMap.put("instituicaoNome", oportunidade.instituicaoNome());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("oferta", ofertaMap);
        map.put("demanda", demandaMap);
        map.put("nivelAtendimento", correspondencia.nivel().name());
        map.put("quantidadeAtendivel", correspondencia.quantidadeAtendivel());
        return map;
    }
}
