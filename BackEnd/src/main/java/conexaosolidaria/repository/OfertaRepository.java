package conexaosolidaria.repository;

import conexaosolidaria.model.Oferta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class OfertaRepository {
    private final ConcurrentMap<Long, Oferta> ofertas = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public Oferta save(Oferta oferta) {
        long id = oferta.getId() == 0 ? sequence.getAndIncrement() : oferta.getId();
        Oferta salva = new Oferta(
                id,
                oferta.getIdUsuarioDoador(),
                oferta.getIdAlimento(),
                oferta.getAlimento(),
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
}
