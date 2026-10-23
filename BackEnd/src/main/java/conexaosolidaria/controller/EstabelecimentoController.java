package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Estabelecimento;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.EstabelecimentoService;

import java.io.IOException;
import java.util.Map;

public class EstabelecimentoController {
    private final AuthService authService;
    private final EstabelecimentoService estabelecimentoService;
    private final AccessControl accessControl;

    public EstabelecimentoController(AuthService authService, EstabelecimentoService estabelecimentoService,
            AccessControl accessControl) {
        this.authService = authService;
        this.estabelecimentoService = estabelecimentoService;
        this.accessControl = accessControl;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CADASTRAR_ESTABELECIMENTO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        Estabelecimento estabelecimento = estabelecimentoService.cadastrar(
                user,
                RequestUtils.required(body, "nome"),
                RequestUtils.required(body, "cnpj"),
                RequestUtils.required(body, "endereco"),
                RequestUtils.required(body, "contato"));

        JsonResponse.send(exchange, 201, Map.of("estabelecimento", toMap(estabelecimento)));
    }

    public void consultar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_ESTABELECIMENTOS);

        Estabelecimento estabelecimento = estabelecimentoService.consultarPorUsuario(user.id());
        JsonResponse.send(exchange, 200, Map.of("estabelecimento", toMap(estabelecimento)));
    }

    public void atualizar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.ATUALIZAR_ESTABELECIMENTO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        Estabelecimento estabelecimento = estabelecimentoService.atualizar(
                user,
                RequestUtils.required(body, "nome"),
                RequestUtils.required(body, "cnpj"),
                RequestUtils.required(body, "endereco"),
                RequestUtils.required(body, "contato"));

        JsonResponse.send(exchange, 200, Map.of("estabelecimento", toMap(estabelecimento)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_ESTABELECIMENTOS);

        JsonResponse.send(exchange, 200, Map.of(
                "estabelecimentos",
                estabelecimentoService.listarTodos().stream().map(this::toMap).toList()));
    }

    private Map<String, Object> toMap(Estabelecimento estabelecimento) {
        return Map.of(
                "id", estabelecimento.getId(),
                "idUsuario", estabelecimento.getIdUsuario(),
                "nome", estabelecimento.getNome(),
                "cnpj", estabelecimento.getCnpj(),
                "endereco", estabelecimento.getEndereco(),
                "contato", estabelecimento.getContato(),
                "criadoEm", estabelecimento.getCriadoEm().toString());
    }
}
