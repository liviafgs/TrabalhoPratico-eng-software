package conexaosolidaria.service;

import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.StatusSolicitacaoOferta;

/** Evento publicado quando uma solicitacao e criada ou muda de status. statusAnterior e nulo na criacao. */
public record MudancaStatus(SolicitacaoOferta solicitacao, StatusSolicitacaoOferta statusAnterior, long idUsuarioAutor) {
}
