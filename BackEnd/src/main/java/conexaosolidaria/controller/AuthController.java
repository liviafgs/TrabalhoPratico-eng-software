package conexaosolidaria.controller;

import com.sun.net.httpserver.HttpExchange;
import conexaosolidaria.server.JsonResponse;
import conexaosolidaria.server.RequestUtils;
import conexaosolidaria.service.AuthService;

import java.io.IOException;
import java.util.Map;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void login(HttpExchange exchange) throws IOException {
        Map<String, String> body = RequestUtils.readJsonObject(exchange);
        String token = authService.login(
                RequestUtils.required(body, "email"),
                RequestUtils.required(body, "senha")
        );

        JsonResponse.send(exchange, 200, Map.of("token", token, "tipo", "Bearer"));
    }
}
