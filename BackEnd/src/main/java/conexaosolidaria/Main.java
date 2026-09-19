package conexaosolidaria;

import conexaosolidaria.controller.UsuarioConsoleController;
import conexaosolidaria.repository.UsuarioRepository;
import conexaosolidaria.service.UsuarioService;
import conexaosolidaria.util.ConexaoBanco;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConexaoBanco conexaoBanco = new ConexaoBanco();
        UsuarioRepository usuarioRepository = new UsuarioRepository(conexaoBanco);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        UsuarioConsoleController usuarioController = new UsuarioConsoleController(usuarioService);

        try (Scanner scanner = new Scanner(System.in)) {
            usuarioController.cadastrarPeloConsole(scanner);
        }
    }
}
