package conexaosolidaria.model;

import java.time.LocalDateTime;

/**
 * Registro de uma mudanca de status de uma solicitacao (RN-11). statusAnterior
 * e nulo na criacao.
 */
public record HistoricoSolicitacao(
                long id,
                long idSolicitacao,
                StatusSolicitacaoOferta statusAnterior,
                StatusSolicitacaoOferta statusNovo,
                long idUsuario,
                LocalDateTime registradoEm) implements java.io.Serializable {
}
