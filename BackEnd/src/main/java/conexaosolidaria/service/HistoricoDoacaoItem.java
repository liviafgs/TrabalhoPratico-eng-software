package conexaosolidaria.service;

import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.SolicitacaoOferta;

import java.time.LocalDateTime;

/** Uma solicitacao com os dados da oferta e dos participantes, para consulta do historico. */
public record HistoricoDoacaoItem(
        SolicitacaoOferta solicitacao,
        Oferta oferta,
        String instituicaoNome,
        String estabelecimentoNome,
        LocalDateTime atualizadoEm) {
}
