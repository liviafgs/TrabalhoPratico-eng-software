package conexaosolidaria.repository;

import conexaosolidaria.model.NutricionistaResponsavel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class NutricionistaRepository {
    private final ConcurrentMap<Long, NutricionistaResponsavel> nutricionistasPorId = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Long> idsPorUsuario = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Long> idsPorInstituicao = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public NutricionistaResponsavel save(NutricionistaResponsavel nutricionista) {
        long id = nutricionista.getId() == 0 ? sequence.getAndIncrement() : nutricionista.getId();
        NutricionistaResponsavel salvo = new NutricionistaResponsavel(
                id,
                nutricionista.getIdUsuario(),
                nutricionista.getIdInstituicao(),
                nutricionista.getNome(),
                nutricionista.getCrn(),
                nutricionista.getContato(),
                nutricionista.getCriadoEm());

        nutricionistasPorId.put(salvo.getId(), salvo);
        idsPorUsuario.put(salvo.getIdUsuario(), salvo.getId());
        idsPorInstituicao.put(salvo.getIdInstituicao(), salvo.getId());
        return salvo;
    }

    public Optional<NutricionistaResponsavel> findById(long id) {
        return Optional.ofNullable(nutricionistasPorId.get(id));
    }

    public Optional<NutricionistaResponsavel> findByUsuarioId(long idUsuario) {
        Long id = idsPorUsuario.get(idUsuario);
        return id == null ? Optional.empty() : Optional.ofNullable(nutricionistasPorId.get(id));
    }

    public Optional<NutricionistaResponsavel> findByInstituicaoId(long idInstituicao) {
        Long id = idsPorInstituicao.get(idInstituicao);
        return id == null ? Optional.empty() : Optional.ofNullable(nutricionistasPorId.get(id));
    }

    public List<NutricionistaResponsavel> findAll() {
        return new ArrayList<>(nutricionistasPorId.values());
    }

    public boolean existsByUsuario(long idUsuario) {
        return idsPorUsuario.containsKey(idUsuario);
    }

    public boolean existsByInstituicao(long idInstituicao) {
        return idsPorInstituicao.containsKey(idInstituicao);
    }
}
