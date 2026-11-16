package conexaosolidaria.repository;

import conexaosolidaria.model.Instituicao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InstituicaoRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, Instituicao> instituicoesPorId;
    private final ConcurrentMap<Long, Long> idsPorUsuario = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> idsPorCnpj = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public InstituicaoRepository() {
        this(null);
    }

    public InstituicaoRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        instituicoesPorId = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("instituicao", Instituicao::getId, Instituicao.class);
        instituicoesPorId.values().forEach(instituicao -> {
            idsPorUsuario.put(instituicao.getIdUsuario(), instituicao.getId());
            idsPorCnpj.put(normalizeCnpj(instituicao.getCnpj()), instituicao.getId());
        });
    }

    public Instituicao save(Instituicao instituicao) {
        long id = instituicao.getId() == 0 ? nextId() : instituicao.getId();
        Instituicao salvo = new Instituicao(
                id,
                instituicao.getIdUsuario(),
                instituicao.getNome(),
                instituicao.getCnpj(),
                instituicao.getEndereco(),
                instituicao.getContato(),
                instituicao.getStatus(),
                instituicao.getCriadoEm());

        instituicoesPorId.put(salvo.getId(), salvo);
        idsPorUsuario.put(salvo.getIdUsuario(), salvo.getId());
        idsPorCnpj.put(normalizeCnpj(salvo.getCnpj()), salvo.getId());
        return salvo;
    }

    public Optional<Instituicao> findById(long id) {
        return Optional.ofNullable(instituicoesPorId.get(id));
    }

    public Optional<Instituicao> findByUsuarioId(long idUsuario) {
        Long id = idsPorUsuario.get(idUsuario);
        return id == null ? Optional.empty() : Optional.ofNullable(instituicoesPorId.get(id));
    }

    public List<Instituicao> findAll() {
        return new ArrayList<>(instituicoesPorId.values());
    }

    public boolean existsByUsuario(long idUsuario) {
        return idsPorUsuario.containsKey(idUsuario);
    }

    public boolean existsByCnpj(String cnpj) {
        return idsPorCnpj.containsKey(normalizeCnpj(cnpj));
    }

    public boolean existsById(long id) {
        return instituicoesPorId.containsKey(id);
    }

    private String normalizeCnpj(String cnpj) {
        return cnpj == null ? "" : cnpj.replaceAll("\\D", "");
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
