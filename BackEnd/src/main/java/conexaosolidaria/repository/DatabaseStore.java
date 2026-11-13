package conexaosolidaria.repository;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class DatabaseStore {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseStore(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DatabaseStore configured() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseStore.class.getResourceAsStream("/application.properties")) {
            if (input == null) {
                throw new IllegalStateException("application.properties nao foi encontrado.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel carregar a configuracao do banco.", e);
        }

        return new DatabaseStore(
                setting("DB_URL", "spring.datasource.url", properties),
                setting("DB_USERNAME", "spring.datasource.username", properties),
                setting("DB_PASSWORD", "spring.datasource.password", properties));
    }

    public void migrate() {
        try (Connection connection = openConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);
            try {
                liquibase.update(new Contexts(), new LabelExpression());
            } finally {
                liquibase.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Nao foi possivel conectar ou migrar o banco PostgreSQL.", e);
        }
    }

    public <K, V> ConcurrentMap<K, V> map(String entityType, Function<V, K> keyFromValue, Class<V> valueType) {
        return new PersistentConcurrentMap<>(this, entityType, keyFromValue, valueType);
    }

    public long nextId() {
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT nextval('backend_entity_id_seq')");
                ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getLong(1);
        } catch (SQLException e) {
            throw databaseError("Nao foi possivel gerar o identificador do registro.", e);
        }
    }

    <K, V> List<V> load(String entityType, Function<V, K> keyFromValue, Class<V> valueType) {
        List<V> values = new ArrayList<>();
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT payload FROM backend_entity_store WHERE entity_type = ?")) {
            statement.setString(1, entityType);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    V value = deserialize(result.getBytes("payload"), valueType);
                    keyFromValue.apply(value);
                    values.add(value);
                }
            }
            return values;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw databaseError("Nao foi possivel carregar os registros " + entityType + ".", e);
        }
    }

    void save(String entityType, Object key, Object value) {
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO backend_entity_store (entity_type, entity_key, payload) VALUES (?, ?, ?) "
                                + "ON CONFLICT (entity_type, entity_key) DO UPDATE SET "
                                + "payload = EXCLUDED.payload, updated_at = CURRENT_TIMESTAMP")) {
            statement.setString(1, entityType);
            statement.setString(2, String.valueOf(key));
            statement.setBytes(3, serialize(value));
            statement.executeUpdate();
        } catch (SQLException | IOException e) {
            throw databaseError("Nao foi possivel salvar o registro " + entityType + ".", e);
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static String setting(String environmentName, String propertyName, Properties properties) {
        String environmentValue = System.getenv(environmentName);
        return environmentValue == null || environmentValue.isBlank()
                ? properties.getProperty(propertyName)
                : environmentValue;
    }

    private static byte[] serialize(Object value) throws IOException {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
            return bytes.toByteArray();
        }
    }

    private static <V> V deserialize(byte[] bytes, Class<V> valueType)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            input.setObjectInputFilter(info -> {
                Class<?> type = info.serialClass();
                if (type == null) {
                    return ObjectInputFilter.Status.UNDECIDED;
                }
                String name = type.getName();
                return type.isPrimitive() || name.startsWith("conexaosolidaria.model.")
                        || name.startsWith("java.lang.") || name.startsWith("java.time.")
                        ? ObjectInputFilter.Status.ALLOWED
                        : ObjectInputFilter.Status.REJECTED;
            });
            Object value = input.readObject();
            if (!valueType.isInstance(value)) {
                throw new IOException("Tipo de registro inesperado no banco.");
            }
            return valueType.cast(value);
        }
    }

    private static IllegalStateException databaseError(String message, Exception cause) {
        return new IllegalStateException(message, cause);
    }
}