package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.InstituicaoService;

import java.io.IOException;
import java.util.Map;

public class InstituicaoController {
    private final AuthService authService;
    private final InstituicaoService instituicaoService;
    private final AccessControl accessControl;

    public InstituicaoController(AuthService authService, InstituicaoService instituicaoService,
            AccessControl accessControl) {
        this.authService = authService;
        this.instituicaoService = instituicaoService;
        this.accessControl = accessControl;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CADASTRAR_INSTITUICAO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        Instituicao instituicao = instituicaoService.cadastrar(
                user,
                RequestUtils.required(body, "nome"),
                RequestUtils.required(body, "cnpj"),
                RequestUtils.required(body, "endereco"),
                RequestUtils.required(body, "contato"));

        JsonResponse.send(exchange, 201, Map.of("instituicao", toMap(instituicao)));
    }

    public void consultar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_INSTITUICOES);

        Instituicao instituicao = instituicaoService.consultarPorUsuario(user.id());
        JsonResponse.send(exchange, 200, Map.of("instituicao", toMap(instituicao)));
    }

    public void atualizar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CADASTRAR_INSTITUICAO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        Instituicao instituicao = instituicaoService.atualizar(
                user,
                RequestUtils.required(body, "nome"),
                RequestUtils.required(body, "cnpj"),
                RequestUtils.required(body, "endereco"),
                RequestUtils.required(body, "contato"));

        JsonResponse.send(exchange, 200, Map.of("instituicao", toMap(instituicao)));
    }

    public void habilitar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.HABILITAR_INSTITUICAO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        boolean habilitar = Boolean.parseBoolean(RequestUtils.required(body, "habilitada"));
        Instituicao instituicao = instituicaoService.habilitar(user, habilitar);
        JsonResponse.send(exchange, 200, Map.of("instituicao", toMap(instituicao)));
    }

    public void listar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_INSTITUICOES);

        JsonResponse.send(exchange, 200, Map.of(
                "instituicoes",
                instituicaoService.listarTodos().stream().map(this::toMap).toList()));
    }

    private Map<String, Object> toMap(Instituicao instituicao) {
        return Map.of(
                "id", instituicao.getId(),
                "idUsuario", instituicao.getIdUsuario(),
                "nome", instituicao.getNome(),
                "cnpj", instituicao.getCnpj(),
                "endereco", instituicao.getEndereco(),
                "contato", instituicao.getContato(),
                "status", instituicao.getStatus().name(),
                "statusDescricao", instituicao.getStatus().getDescricao(),
                "criadoEm", instituicao.getCriadoEm().toString());
    }
}
