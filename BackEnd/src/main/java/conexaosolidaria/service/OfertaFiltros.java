package main.java.conexaosolidaria.service;

import java.time.LocalDate;

public record OfertaFiltros(
        String alimento,
        String categoria,
        String localizacao,
        LocalDate data,
        Boolean disponivel) {

    public static OfertaFiltros vazios() {
        return new OfertaFiltros(null, null, null, null, true);
    }
}
