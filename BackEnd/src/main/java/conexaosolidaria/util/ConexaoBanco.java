package conexaosolidaria.util;

import conexaosolidaria.exception.CadastroUsuarioException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    private static final String URL_PADRAO = "jdbc:postgresql://localhost:5432/conexao_solidaria";
    private static final String USUARIO_PADRAO = "conexao_solidaria";
    private static final String SENHA_PADRAO = "conexao";

    public Connection obterConexao() {
        try {
            return DriverManager.getConnection(
                    lerConfiguracao("DB_URL", URL_PADRAO),
                    lerConfiguracao("DB_USER", USUARIO_PADRAO),
                    lerConfiguracao("DB_PASSWORD", SENHA_PADRAO)
            );
        } catch (SQLException exception) {
            throw new CadastroUsuarioException("Nao foi possivel conectar ao banco de dados.", exception);
        }
    }

    private String lerConfiguracao(String nome, String valorPadrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? valorPadrao : valor;
    }
}
