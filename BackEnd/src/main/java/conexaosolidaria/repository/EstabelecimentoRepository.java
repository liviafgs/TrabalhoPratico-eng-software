package conexaosolidaria.repository;

import conexaosolidaria.model.Estabelecimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class EstabelecimentoRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, Estabelecimento> estabelecimentosPorId;
    private final ConcurrentMap<Long, Long> idsPorUsuario = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> idsPorCnpj = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public EstabelecimentoRepository() {
        this(null);
    }

    public EstabelecimentoRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        estabelecimentosPorId = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("estabelecimento", Estabelecimento::getId, Estabelecimento.class);
        estabelecimentosPorId.values().forEach(estabelecimento -> {
            idsPorUsuario.put(estabelecimento.getIdUsuario(), estabelecimento.getId());
            idsPorCnpj.put(normalizeCnpj(estabelecimento.getCnpj()), estabelecimento.getId());
        });
    }

    public Estabelecimento save(Estabelecimento estabelecimento) {
        long id = estabelecimento.getId() == 0 ? nextId() : estabelecimento.getId();
        Estabelecimento salvo = new Estabelecimento(
                id,
                estabelecimento.getIdUsuario(),
                estabelecimento.getNome(),
                estabelecimento.getCnpj(),
                estabelecimento.getEndereco(),
                estabelecimento.getContato(),
                estabelecimento.getCriadoEm());

        estabelecimentosPorId.put(salvo.getId(), salvo);
        idsPorUsuario.put(salvo.getIdUsuario(), salvo.getId());
        idsPorCnpj.put(normalizeCnpj(salvo.getCnpj()), salvo.getId());
        return salvo;
    }

    public Optional<Estabelecimento> findById(long id) {
        return Optional.ofNullable(estabelecimentosPorId.get(id));
    }

    public Optional<Estabelecimento> findByUsuarioId(long idUsuario) {
        Long id = idsPorUsuario.get(idUsuario);
        return id == null ? Optional.empty() : Optional.ofNullable(estabelecimentosPorId.get(id));
    }

    public List<Estabelecimento> findAll() {
        return new ArrayList<>(estabelecimentosPorId.values());
    }

    public boolean existsByUsuario(long idUsuario) {
        return idsPorUsuario.containsKey(idUsuario);
    }

    public boolean existsByCnpj(String cnpj) {
        return idsPorCnpj.containsKey(normalizeCnpj(cnpj));
    }

    private String normalizeCnpj(String cnpj) {
        return cnpj == null ? "" : cnpj.replaceAll("\\D", "");
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
