package conexaosolidaria.repository;

import conexaosolidaria.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class UsuarioRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, Usuario> usuariosPorId;
    private final ConcurrentMap<String, Long> idsPorEmail = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public UsuarioRepository() {
        this(null);
    }

    public UsuarioRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        usuariosPorId = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("usuario", Usuario::getId, Usuario.class);
        usuariosPorId.values().forEach(usuario ->
                idsPorEmail.put(normalizeEmail(usuario.getEmail()), usuario.getId()));
    }

    public Usuario save(Usuario usuario) {
        long id = usuario.getId() == 0 ? nextId() : usuario.getId();
        Usuario salvo = new Usuario(
                id,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPerfil(),
                usuario.getCriadoEm()
        );

        usuariosPorId.put(salvo.getId(), salvo);
        idsPorEmail.put(normalizeEmail(salvo.getEmail()), salvo.getId());
        return salvo;
    }

    public Optional<Usuario> findByEmail(String email) {
        Long id = idsPorEmail.get(normalizeEmail(email));
        return id == null ? Optional.empty() : Optional.ofNullable(usuariosPorId.get(id));
    }

    public boolean existsByEmail(String email) {
        return idsPorEmail.containsKey(normalizeEmail(email));
    }

    public List<Usuario> findAll() {
        return new ArrayList<>(usuariosPorId.values());
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
