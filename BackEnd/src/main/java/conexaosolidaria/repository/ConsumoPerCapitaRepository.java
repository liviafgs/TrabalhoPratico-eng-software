package conexaosolidaria.repository;

import conexaosolidaria.model.ConsumoPerCapita;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumoPerCapitaRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<String, ConsumoPerCapita> consumos;
    private final AtomicLong sequence = new AtomicLong(1);

    public ConsumoPerCapitaRepository() {
        this(null);
    }

    public ConsumoPerCapitaRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        consumos = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("consumo_per_capita",
                        consumo -> ConsumoPerCapita.chave(consumo.getAlimento(), consumo.getUnidadeMedida()),
                        ConsumoPerCapita.class);
    }

    public ConsumoPerCapita save(ConsumoPerCapita consumo) {
        String chave = ConsumoPerCapita.chave(consumo.getAlimento(), consumo.getUnidadeMedida());
        ConsumoPerCapita existente = consumos.get(chave);
        long id = existente != null ? existente.getId() : nextId();

        ConsumoPerCapita salvo = new ConsumoPerCapita(
                id,
                consumo.getAlimento(),
                consumo.getUnidadeMedida(),
                consumo.getQuantidadePorPessoa(),
                consumo.getCriadoEm());
        consumos.put(chave, salvo);
        return salvo;
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }

    public Optional<ConsumoPerCapita> findByAlimentoEUnidade(String alimento, String unidadeMedida) {
        return Optional.ofNullable(consumos.get(ConsumoPerCapita.chave(alimento, unidadeMedida)));
    }

    public List<ConsumoPerCapita> findAll() {
        return new ArrayList<>(consumos.values());
    }
}
