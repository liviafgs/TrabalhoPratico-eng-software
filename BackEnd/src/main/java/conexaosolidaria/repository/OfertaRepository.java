package conexaosolidaria.repository;

import conexaosolidaria.model.Oferta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class OfertaRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, Oferta> ofertas;
    private final AtomicLong sequence = new AtomicLong(1);

    public OfertaRepository() {
        this(null);
    }

    public OfertaRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        ofertas = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("oferta", Oferta::getId, Oferta.class);
    }

    public Oferta save(Oferta oferta) {
        long id = oferta.getId() == 0 ? nextId() : oferta.getId();
        Oferta salva = new Oferta(
                id,
                oferta.getIdUsuarioDoador(),
                oferta.getIdAlimento(),
                oferta.getAlimento(),
                oferta.getCategoria(),
                oferta.getQuantidade(),
                oferta.getUnidadeMedida(),
                oferta.getRetiradaAte(),
                oferta.getValidade(),
                oferta.getDataHoraRetirada(),
                oferta.getLocalRetirada());

        ofertas.put(salva.getId(), salva);
        return salva;
    }

    public List<Oferta> findAll() {
        return new ArrayList<>(ofertas.values());
    }

    public Optional<Oferta> findById(long id) {
        return Optional.ofNullable(ofertas.get(id));
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
