package conexaosolidaria.repository;

import conexaosolidaria.model.SolicitacaoOferta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class SolicitacaoOfertaRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, SolicitacaoOferta> solicitacoes;
    private final AtomicLong sequence = new AtomicLong(1);

    public SolicitacaoOfertaRepository() {
        this(null);
    }

    public SolicitacaoOfertaRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        solicitacoes = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("solicitacao_oferta", SolicitacaoOferta::getId, SolicitacaoOferta.class);
    }

    public SolicitacaoOferta save(SolicitacaoOferta solicitacao) {
        long id = solicitacao.getId() == 0 ? nextId() : solicitacao.getId();
        SolicitacaoOferta salva = new SolicitacaoOferta(
                id,
                solicitacao.getIdOferta(),
                solicitacao.getIdUsuarioInstituicao(),
                solicitacao.getQuantidade(),
                solicitacao.getStatus(),
                solicitacao.getCriadoEm(),
                solicitacao.getRetiradaAgendadaPara());
        solicitacoes.put(id, salva);
        return salva;
    }

    public Optional<SolicitacaoOferta> findById(long id) {
        return Optional.ofNullable(solicitacoes.get(id));
    }

    public List<SolicitacaoOferta> findAll() {
        return new ArrayList<>(solicitacoes.values());
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
