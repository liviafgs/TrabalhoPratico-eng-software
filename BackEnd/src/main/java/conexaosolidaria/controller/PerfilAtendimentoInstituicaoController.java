package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.model.PerfilAtendimentoInstituicao;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;
import conexaosolidaria.service.PerfilAtendimentoInstituicaoService;

import java.io.IOException;
import java.util.Map;

public class PerfilAtendimentoInstituicaoController {
    private final AuthService authService;
    private final PerfilAtendimentoInstituicaoService perfilService;
    private final AccessControl accessControl;

    public PerfilAtendimentoInstituicaoController(AuthService authService,
            PerfilAtendimentoInstituicaoService perfilService, AccessControl accessControl) {
        this.authService = authService;
        this.perfilService = perfilService;
        this.accessControl = accessControl;
    }

    public void cadastrar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CADASTRAR_PERFIL_ATENDIMENTO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        PerfilAtendimentoInstituicao perfil = perfilService.cadastrar(
                user,
                Long.parseLong(RequestUtils.required(body, "idInstituicao")),
                Integer.parseInt(RequestUtils.required(body, "numeroBeneficiarios")),
                Integer.parseInt(RequestUtils.required(body, "capacidadeAtendimento")),
                RequestUtils.required(body, "publicoAtendido"),
                RequestUtils.required(body, "frequenciaAtendimento"));

        JsonResponse.send(exchange, 201, Map.of("perfilAtendimento", toMap(perfil)));
    }

    public void consultar(HttpExchange exchange) throws IOException {
        AuthenticatedUser user = authService.authenticate(RequestUtils.authorizationHeader(exchange));
        accessControl.require(user, Permissao.CONSULTAR_PERFIL_ATENDIMENTO);

        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        long idInstituicao = Long.parseLong(RequestUtils.required(body, "idInstituicao"));
        PerfilAtendimentoInstituicao perfil = perfilService.consultarPorInstituicao(idInstituicao);
        JsonResponse.send(exchange, 200, Map.of("perfilAtendimento", toMap(perfil)));
    }

    private Map<String, Object> toMap(PerfilAtendimentoInstituicao perfil) {
        return Map.of(
                "id", perfil.getId(),
                "idInstituicao", perfil.getIdInstituicao(),
                "numeroBeneficiarios", perfil.getNumeroBeneficiarios(),
                "capacidadeAtendimento", perfil.getCapacidadeAtendimento(),
                "publicoAtendido", perfil.getPublicoAtendido(),
                "frequenciaAtendimento", perfil.getFrequenciaAtendimento(),
                "criadoEm", perfil.getCriadoEm().toString());
    }
}
