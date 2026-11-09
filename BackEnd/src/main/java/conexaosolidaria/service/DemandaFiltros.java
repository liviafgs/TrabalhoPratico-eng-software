package conexaosolidaria.service;

import java.time.LocalDate;

/**
 * Filtros da consulta de demandas.
 * - prazoDe / prazoAte: intervalo (inclusive) para o prazo da demanda.
 * - localizacao: trecho do endereco da instituicao que fez a demanda.
 * - disponivel: true para demandas com prazo vigente, false para demandas com prazo vencido.
 */
public record DemandaFiltros(
        String alimento,
        String prioridade,
        LocalDate prazoDe,
        LocalDate prazoAte,
        String localizacao,
        Boolean disponivel) {

    public static DemandaFiltros vazios() {
        return new DemandaFiltros(null, null, null, null, null, true);
    }

    static boolean contem(String texto, String trecho) {
        return BuscaTexto.contem(texto, trecho);
    }
}
