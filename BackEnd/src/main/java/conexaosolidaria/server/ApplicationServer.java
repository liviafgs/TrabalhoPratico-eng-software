package conexaosolidaria.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import conexaosolidaria.exception.ApiException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationServer {
    private final int port;
    private final Map<String, RouteHandler> routes = new LinkedHashMap<>();

    public ApplicationServer(int port) {
        this.port = port;
    }

    public void register(String method, String path, RouteHandler handler) {
        routes.put(routeKey(method, path), handler);
    }

    public void start() {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/", this::dispatch);
            httpServer.start();
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel iniciar o servidor.", e);
        }
    }

    private void dispatch(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            JsonResponse.send(exchange, 204, null);
            return;
        }

        RouteHandler handler = routes.get(routeKey(exchange.getRequestMethod(), exchange.getRequestURI().getPath()));
        if (handler == null) {
            JsonResponse.send(exchange, 404, Map.of("erro", "Rota nao encontrada."));
            return;
        }

        try {
            handler.handle(exchange);
        } catch (ApiException e) {
            JsonResponse.send(exchange, e.getStatusCode(), Map.of("erro", e.getMessage()));
        } catch (IllegalArgumentException e) {
            JsonResponse.send(exchange, 400, Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            JsonResponse.send(exchange, 500, Map.of("erro", "Erro interno do servidor."));
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private String routeKey(String method, String path) {
        return method.toUpperCase() + " " + path;
    }
}
