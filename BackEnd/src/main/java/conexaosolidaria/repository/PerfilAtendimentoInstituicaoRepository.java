package conexaosolidaria.repository;

import conexaosolidaria.model.PerfilAtendimentoInstituicao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class PerfilAtendimentoInstituicaoRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, PerfilAtendimentoInstituicao> perfisPorId;
    private final ConcurrentMap<Long, Long> idsPorInstituicao = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public PerfilAtendimentoInstituicaoRepository() {
        this(null);
    }

    public PerfilAtendimentoInstituicaoRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        perfisPorId = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("perfil_atendimento", PerfilAtendimentoInstituicao::getId,
                        PerfilAtendimentoInstituicao.class);
        perfisPorId.values().forEach(perfil ->
                idsPorInstituicao.put(perfil.getIdInstituicao(), perfil.getId()));
    }

    public PerfilAtendimentoInstituicao save(PerfilAtendimentoInstituicao perfil) {
        long id = perfil.getId() == 0 ? nextId() : perfil.getId();
        PerfilAtendimentoInstituicao salvo = new PerfilAtendimentoInstituicao(
                id,
                perfil.getIdInstituicao(),
                perfil.getNumeroBeneficiarios(),
                perfil.getCapacidadeAtendimento(),
                perfil.getPublicoAtendido(),
                perfil.getFrequenciaAtendimento(),
                perfil.getCriadoEm());

        perfisPorId.put(salvo.getId(), salvo);
        idsPorInstituicao.put(salvo.getIdInstituicao(), salvo.getId());
        return salvo;
    }

    public Optional<PerfilAtendimentoInstituicao> findByInstituicaoId(long idInstituicao) {
        Long id = idsPorInstituicao.get(idInstituicao);
        return id == null ? Optional.empty() : Optional.ofNullable(perfisPorId.get(id));
    }

    public List<PerfilAtendimentoInstituicao> findAll() {
        return new ArrayList<>(perfisPorId.values());
    }

    public boolean existsByInstituicaoId(long idInstituicao) {
        return idsPorInstituicao.containsKey(idInstituicao);
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
