package conexaosolidaria.repository;

import conexaosolidaria.model.PerfilAtendimentoInstituicao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class PerfilAtendimentoInstituicaoRepository {
    private final ConcurrentMap<Long, PerfilAtendimentoInstituicao> perfisPorId = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Long> idsPorInstituicao = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public PerfilAtendimentoInstituicao save(PerfilAtendimentoInstituicao perfil) {
        long id = perfil.getId() == 0 ? sequence.getAndIncrement() : perfil.getId();
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
}
