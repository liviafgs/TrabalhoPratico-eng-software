package conexaosolidaria.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Locale;

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
        if (trecho == null || trecho.isBlank()) {
            return true;
        }
        if (texto == null) {
            return false;
        }
        return normalizar(texto).contains(normalizar(trecho));
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
