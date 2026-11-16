package conexaosolidaria.repository;

import conexaosolidaria.model.Demanda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class DemandaRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, Demanda> demandas;
    private final AtomicLong sequence = new AtomicLong(1);

    public DemandaRepository() {
        this(null);
    }

    public DemandaRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        demandas = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("demanda", Demanda::getId, Demanda.class);
    }

    public Demanda save(Demanda demanda) {
        long id = demanda.getId() == 0 ? nextId() : demanda.getId();
        Demanda salva = new Demanda(
                id,
                demanda.getIdUsuarioInstituicao(),
                demanda.getAlimento(),
                demanda.getQuantidade(),
                demanda.getUnidadeMedida(),
                demanda.getPrazo(),
                demanda.getPrioridade(),
                demanda.getFinalidade(),
                demanda.getBeneficiarios(),
                demanda.getConsumoPerCapita(),
                demanda.getQuantidadeReferencia(),
                demanda.isAcimaDaReferencia(),
                demanda.getJustificativa());

        demandas.put(salva.getId(), salva);
        return salva;
    }

    public List<Demanda> findAll() {
        return new ArrayList<>(demandas.values());
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
