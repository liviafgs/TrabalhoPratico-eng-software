package conexaosolidaria.model;

import java.time.LocalDate;

public class Oferta {
    private final long id;
    private final long idUsuarioDoador;
    private final String alimento;
    private final double quantidade;
    private final String unidadeMedida;
    private final LocalDate retiradaAte;

    public Oferta(long id, long idUsuarioDoador, String alimento, double quantidade, String unidadeMedida, LocalDate retiradaAte) {
        this.id = id;
        this.idUsuarioDoador = idUsuarioDoador;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.retiradaAte = retiradaAte;
    }

    public long getId() {
        return id;
    }

    public long getIdUsuarioDoador() {
        return idUsuarioDoador;
    }

    public String getAlimento() {
        return alimento;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public LocalDate getRetiradaAte() {
        return retiradaAte;
    }
}
