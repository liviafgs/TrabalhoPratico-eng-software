package conexaosolidaria;

import conexaosolidaria.config.ApplicationConfig;
import conexaosolidaria.server.ApplicationServer;

public class Main {
    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        ApplicationServer server = config.createServer(8080);

        server.start();
        System.out.println("Backend Conexao Solidaria iniciado em http://localhost:8080");
    }
}
