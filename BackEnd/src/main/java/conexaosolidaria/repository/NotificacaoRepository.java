package conexaosolidaria.repository;

import conexaosolidaria.model.Notificacao;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class NotificacaoRepository {
    private final DatabaseStore databaseStore;
    private final ConcurrentMap<Long, Notificacao> notificacoes;
    private final AtomicLong sequence = new AtomicLong(1);

    public NotificacaoRepository() {
        this(null);
    }

    public NotificacaoRepository(DatabaseStore databaseStore) {
        this.databaseStore = databaseStore;
        notificacoes = databaseStore == null ? new ConcurrentHashMap<>()
                : databaseStore.map("notificacao", Notificacao::getId, Notificacao.class);
    }

    public Notificacao save(Notificacao notificacao) {
        long id = notificacao.getId() == 0 ? nextId() : notificacao.getId();
        Notificacao salva = new Notificacao(
                id,
                notificacao.getIdUsuarioDestino(),
                notificacao.getTipo(),
                notificacao.getMensagem(),
                notificacao.getIdSolicitacao(),
                notificacao.getIdOferta(),
                notificacao.getIdDemanda(),
                notificacao.getCriadaEm(),
                notificacao.getLidaEm());
        notificacoes.put(id, salva);
        return salva;
    }

    public Optional<Notificacao> findById(long id) {
        return Optional.ofNullable(notificacoes.get(id));
    }

    /** Notificacoes do usuario, da mais recente para a mais antiga. */
    public List<Notificacao> findByUsuario(long idUsuario) {
        return notificacoes.values().stream()
                .filter(notificacao -> notificacao.getIdUsuarioDestino() == idUsuario)
                .sorted(Comparator.comparingLong(Notificacao::getId).reversed())
                .toList();
    }

    private long nextId() {
        return databaseStore == null ? sequence.getAndIncrement() : databaseStore.nextId();
    }
}
