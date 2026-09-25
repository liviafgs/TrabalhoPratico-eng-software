package main.java.conexaosolidaria.repository;

import conexaosolidaria.model.SolicitacaoOferta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class SolicitacaoOfertaRepository {
    private final ConcurrentMap<Long, SolicitacaoOferta> solicitacoes = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public SolicitacaoOferta save(SolicitacaoOferta solicitacao) {
        long id = solicitacao.getId() == 0 ? sequence.getAndIncrement() : solicitacao.getId();
        SolicitacaoOferta salva = new SolicitacaoOferta(
                id,
                solicitacao.getIdOferta(),
                solicitacao.getIdUsuarioInstituicao(),
                solicitacao.getQuantidade(),
                solicitacao.getStatus(),
                solicitacao.getCriadoEm());
        solicitacoes.put(id, salva);
        return salva;
    }

    public Optional<SolicitacaoOferta> findById(long id) {
        return Optional.ofNullable(solicitacoes.get(id));
    }

    public List<SolicitacaoOferta> findAll() {
        return new ArrayList<>(solicitacoes.values());
    }
}
