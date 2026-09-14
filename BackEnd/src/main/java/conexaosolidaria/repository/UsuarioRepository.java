package conexaosolidaria.repository;

import conexaosolidaria.exception.CadastroUsuarioException;
import conexaosolidaria.model.TipoUsuario;
import conexaosolidaria.model.Usuario;
import conexaosolidaria.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class UsuarioRepository {
    private final ConexaoBanco conexaoBanco;

    public UsuarioRepository(ConexaoBanco conexaoBanco) {
        this.conexaoBanco = conexaoBanco;
    }

    public Usuario salvar(Usuario usuario) {
        String sql = """
                INSERT INTO usuario (nome, email, senha, telefone, tipo_usuario, ativo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = conexaoBanco.obterConexao();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getTelefone());
            statement.setString(5, usuario.getTipoUsuario().name());
            statement.setBoolean(6, usuario.isAtivo());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                }
            }

            if (usuario.getId() == null) {
                return buscarPorEmail(usuario.getEmail()).orElse(usuario);
            }

            return buscarPorId(usuario.getId()).orElse(usuario);
        } catch (SQLException exception) {
            throw new CadastroUsuarioException("Erro ao salvar usuario no banco de dados.", exception);
        }
    }

    public boolean existePorEmail(String email) {
        String sql = "SELECT 1 FROM usuario WHERE LOWER(email) = LOWER(?)";

        try (Connection connection = conexaoBanco.obterConexao();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new CadastroUsuarioException("Erro ao verificar e-mail no banco de dados.", exception);
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        String sql = """
                SELECT id, nome, email, senha, telefone, tipo_usuario, ativo, data_cadastro
                FROM usuario
                WHERE id = ?
                """;

        try (Connection connection = conexaoBanco.obterConexao();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearUsuario(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new CadastroUsuarioException("Erro ao buscar usuario por id.", exception);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = """
                SELECT id, nome, email, senha, telefone, tipo_usuario, ativo, data_cadastro
                FROM usuario
                WHERE LOWER(email) = LOWER(?)
                """;

        try (Connection connection = conexaoBanco.obterConexao();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearUsuario(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new CadastroUsuarioException("Erro ao buscar usuario por e-mail.", exception);
        }
    }

    private Usuario mapearUsuario(ResultSet resultSet) throws SQLException {
        Timestamp dataCadastro = resultSet.getTimestamp("data_cadastro");

        return new Usuario(
                resultSet.getLong("id"),
                resultSet.getString("nome"),
                resultSet.getString("email"),
                resultSet.getString("senha"),
                resultSet.getString("telefone"),
                TipoUsuario.valueOf(resultSet.getString("tipo_usuario")),
                resultSet.getBoolean("ativo"),
                dataCadastro == null ? null : dataCadastro.toLocalDateTime()
        );
    }
}
