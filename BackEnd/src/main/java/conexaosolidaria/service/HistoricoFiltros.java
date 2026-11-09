package conexaosolidaria.service;

import conexaosolidaria.model.StatusSolicitacaoOferta;

import java.time.LocalDate;
import java.util.Set;

/**
 * Filtros do historico de doacoes.
 * - alimento, instituicao e estabelecimento: trecho do nome, sem distinguir maiusculas e acentos.
 * - status: um ou mais status; vazio traz todos.
 * - de / ate: intervalo (inclusive) da data da ultima movimentacao da solicitacao.
 */
public record HistoricoFiltros(
        String alimento,
        String instituicao,
        String estabelecimento,
        Set<StatusSolicitacaoOferta> status,
        LocalDate de,
        LocalDate ate) {

    public static HistoricoFiltros vazios() {
        return new HistoricoFiltros(null, null, null, Set.of(), null, null);
    }
}
