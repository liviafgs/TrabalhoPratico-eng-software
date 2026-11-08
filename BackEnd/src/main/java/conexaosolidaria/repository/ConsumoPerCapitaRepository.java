package conexaosolidaria.repository;

import conexaosolidaria.model.ConsumoPerCapita;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumoPerCapitaRepository {
    private final ConcurrentMap<String, ConsumoPerCapita> consumos = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public ConsumoPerCapita save(ConsumoPerCapita consumo) {
        String chave = ConsumoPerCapita.chave(consumo.getAlimento(), consumo.getUnidadeMedida());
        ConsumoPerCapita existente = consumos.get(chave);
        long id = existente != null ? existente.getId() : sequence.getAndIncrement();

        ConsumoPerCapita salvo = new ConsumoPerCapita(
                id,
                consumo.getAlimento(),
                consumo.getUnidadeMedida(),
                consumo.getQuantidadePorPessoa(),
                consumo.getCriadoEm());
        consumos.put(chave, salvo);
        return salvo;
    }

    public Optional<ConsumoPerCapita> findByAlimentoEUnidade(String alimento, String unidadeMedida) {
        return Optional.ofNullable(consumos.get(ConsumoPerCapita.chave(alimento, unidadeMedida)));
    }

    public List<ConsumoPerCapita> findAll() {
        return new ArrayList<>(consumos.values());
    }
}
