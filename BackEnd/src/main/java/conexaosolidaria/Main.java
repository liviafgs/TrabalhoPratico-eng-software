package conexaosolidaria;

import conexaosolidaria.config.ApplicationConfig;
import conexaosolidaria.server.ApplicationServer;

public class Main {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        ApplicationConfig config = new ApplicationConfig();
        ApplicationServer server = config.createServer(port);

        server.start();
        System.out.println("Backend Conexao Solidaria iniciado na porta " + port);
    }
}
