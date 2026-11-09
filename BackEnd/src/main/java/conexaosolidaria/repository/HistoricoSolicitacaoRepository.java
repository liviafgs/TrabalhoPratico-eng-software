package conexaosolidaria.repository;

import conexaosolidaria.model.HistoricoSolicitacao;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class HistoricoSolicitacaoRepository {
    private final List<HistoricoSolicitacao> registros = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public HistoricoSolicitacao save(HistoricoSolicitacao registro) {
        HistoricoSolicitacao salvo = new HistoricoSolicitacao(
                sequence.getAndIncrement(),
                registro.idSolicitacao(),
                registro.statusAnterior(),
                registro.statusNovo(),
                registro.idUsuario(),
                registro.registradoEm());
        registros.add(salvo);
        return salvo;
    }

    public List<HistoricoSolicitacao> findBySolicitacao(long idSolicitacao) {
        return registros.stream()
                .filter(registro -> registro.idSolicitacao() == idSolicitacao)
                .sorted(Comparator.comparingLong(HistoricoSolicitacao::id))
                .toList();
    }
}
