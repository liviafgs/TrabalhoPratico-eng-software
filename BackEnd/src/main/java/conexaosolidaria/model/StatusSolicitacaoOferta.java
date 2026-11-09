package conexaosolidaria.model;

/**
 * Estados de uma solicitacao de oferta.
 * SOLICITADA e o estado "pendente": a solicitacao foi registrada e aguarda resposta do estabelecimento.
 */
public enum StatusSolicitacaoOferta {
    SOLICITADA,
    RETIRADA,
    CANCELADA,
    ACEITA,
    RECUSADA,
    AGENDADA,
    CONCLUIDA;

    /**
     * Fluxo: SOLICITADA -> ACEITA ou RECUSADA; ACEITA -> AGENDADA; AGENDADA -> RETIRADA; RETIRADA -> CONCLUIDA.
     * A retirada tambem pode ser confirmada a partir de SOLICITADA ou ACEITA, e a solicitacao pode ser
     * cancelada ate a retirada. RECUSADA, CANCELADA e CONCLUIDA sao estados finais.
     */
    public boolean podeMudarPara(StatusSolicitacaoOferta novo) {
        return switch (this) {
            case SOLICITADA -> novo == ACEITA || novo == RECUSADA || novo == CANCELADA || novo == RETIRADA;
            case ACEITA -> novo == AGENDADA || novo == CANCELADA || novo == RETIRADA;
            case AGENDADA -> novo == AGENDADA || novo == CANCELADA || novo == RETIRADA;
            case RETIRADA -> novo == CONCLUIDA;
            case RECUSADA, CANCELADA, CONCLUIDA -> false;
        };
    }

    /** Solicitacoes recusadas ou canceladas devolvem a quantidade para a oferta. */
    public boolean reservaQuantidade() {
        return this != RECUSADA && this != CANCELADA;
    }
}
