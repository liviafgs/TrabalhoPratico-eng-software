package conexaosolidaria.service;

import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Oferta;

/**
 * Resultado da avaliacao de uma oferta contra uma demanda.
 * quantidadeAtendivel e o quanto da demanda a oferta consegue cobrir hoje.
 */
public record Correspondencia(
        Oferta oferta,
        Demanda demanda,
        double quantidadeDisponivel,
        double quantidadeAtendivel,
        NivelAtendimento nivel) {

    public enum NivelAtendimento {
        /** A oferta cobre toda a quantidade da demanda. */
        TOTAL,
        /** A oferta cobre apenas parte da quantidade da demanda. */
        PARCIAL
    }
}
