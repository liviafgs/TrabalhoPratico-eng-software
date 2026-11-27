package conexaosolidaria.repository;

import conexaosolidaria.model.HistoricoSolicitacao;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class HistoricoSolicitacaoRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, HistoricoSolicitacao> registros;
    private final AtomicLong sequence = new AtomicLong(1);

    public HistoricoSolicitacaoRepository() {
        this(null);
    }

    public HistoricoSolicitacaoRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        registros = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("historico_solicitacao", HistoricoSolicitacao::id, HistoricoSolicitacao.class);
    }

    public HistoricoSolicitacao save(HistoricoSolicitacao registro) {
        HistoricoSolicitacao salvo = new HistoricoSolicitacao(
                nextId(),
                registro.idSolicitacao(),
                registro.statusAnterior(),
                registro.statusNovo(),
                registro.idUsuario(),
                registro.registradoEm());
        registros.put(salvo.id(), salvo);
        return salvo;
    }

    public List<HistoricoSolicitacao> findBySolicitacao(long idSolicitacao) {
        return registros.values().stream()
                .filter(registro -> registro.idSolicitacao() == idSolicitacao)
                .sorted(Comparator.comparingLong(HistoricoSolicitacao::id))
                .toList();
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
