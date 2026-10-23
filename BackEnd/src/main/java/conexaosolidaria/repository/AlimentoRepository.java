package conexaosolidaria.repository;

import conexaosolidaria.model.Alimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class AlimentoRepository {
    private final ConcurrentMap<Long, Alimento> alimentosPorId = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> idsPorNome = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public Alimento save(Alimento alimento) {
        long id = alimento.getId() == 0 ? sequence.getAndIncrement() : alimento.getId();
        Alimento salvo = new Alimento(
                id,
                alimento.getNome(),
                alimento.getCategoria(),
                alimento.getUnidadeMedida(),
                alimento.getDescricao(),
                alimento.getCriadoEm());

        alimentosPorId.put(salvo.getId(), salvo);
        idsPorNome.put(normalizeNome(salvo.getNome()), salvo.getId());
        return salvo;
    }

    public Optional<Alimento> findByNome(String nome) {
        Long id = idsPorNome.get(normalizeNome(nome));
        return id == null ? Optional.empty() : Optional.ofNullable(alimentosPorId.get(id));
    }

    public List<Alimento> findAll() {
        return new ArrayList<>(alimentosPorId.values());
    }

    public boolean existsByNome(String nome) {
        return idsPorNome.containsKey(normalizeNome(nome));
    }

    private String normalizeNome(String nome) {
        return nome == null ? "" : nome.trim().toLowerCase();
    }
}
