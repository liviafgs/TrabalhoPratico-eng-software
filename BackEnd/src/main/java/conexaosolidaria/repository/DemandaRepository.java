package conexaosolidaria.repository;

import conexaosolidaria.model.Demanda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class DemandaRepository {
    private final ConcurrentMap<Long, Demanda> demandas = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public Demanda save(Demanda demanda) {
        long id = demanda.getId() == 0 ? sequence.getAndIncrement() : demanda.getId();
        Demanda salva = new Demanda(
                id,
                demanda.getIdUsuarioInstituicao(),
                demanda.getAlimento(),
                demanda.getQuantidade(),
                demanda.getUnidadeMedida(),
                demanda.getPrazo(),
                demanda.getPrioridade()
        );

        demandas.put(salva.getId(), salva);
        return salva;
    }

    public List<Demanda> findAll() {
        return new ArrayList<>(demandas.values());
    }
}
