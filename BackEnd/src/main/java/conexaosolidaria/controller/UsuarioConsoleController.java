package conexaosolidaria.controller;

import conexaosolidaria.dto.CadastroUsuarioRequest;
import conexaosolidaria.exception.CadastroUsuarioException;
import conexaosolidaria.model.TipoUsuario;
import conexaosolidaria.model.Usuario;
import conexaosolidaria.service.UsuarioService;

import java.util.Scanner;

public class UsuarioConsoleController {
    private final UsuarioService usuarioService;

    public UsuarioConsoleController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void cadastrarPeloConsole(Scanner scanner) {
        System.out.println("=== Cadastro de usuario ===");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("E-mail: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        System.out.print("Telefone (opcional): ");
        String telefone = scanner.nextLine();

        System.out.println("Tipo de usuario:");
        System.out.println("1 - Estabelecimento doador");
        System.out.println("2 - Instituicao beneficiaria");
        System.out.println("3 - Administrador");
        System.out.print("Opcao: ");

        try {
            TipoUsuario tipoUsuario = lerTipoUsuario(scanner.nextLine());
            CadastroUsuarioRequest request = new CadastroUsuarioRequest(nome, email, senha, telefone, tipoUsuario);
            Usuario usuario = usuarioService.cadastrar(request);
            System.out.println("Usuario cadastrado com sucesso. ID: " + usuario.getId());
        } catch (CadastroUsuarioException exception) {
            System.out.println("Nao foi possivel cadastrar o usuario: " + exception.getMessage());
        }
    }

    private TipoUsuario lerTipoUsuario(String opcao) {
        return switch (opcao.trim()) {
            case "1" -> TipoUsuario.ESTABELECIMENTO_DOADOR;
            case "2" -> TipoUsuario.INSTITUICAO_BENEFICIARIA;
            case "3" -> TipoUsuario.ADMINISTRADOR;
            default -> throw new CadastroUsuarioException("Tipo de usuario invalido.");
        };
    }
}
