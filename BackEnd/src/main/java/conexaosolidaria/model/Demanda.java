package conexaosolidaria.model;

import java.time.LocalDate;

public class Demanda {
    private final long id;
    private final long idUsuarioInstituicao;
    private final String alimento;
    private final double quantidade;
    private final String unidadeMedida;
    private final LocalDate prazo;
    private final String prioridade;

    public Demanda(long id, long idUsuarioInstituicao, String alimento, double quantidade, String unidadeMedida, LocalDate prazo, String prioridade) {
        this.id = id;
        this.idUsuarioInstituicao = idUsuarioInstituicao;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.prazo = prazo;
        this.prioridade = prioridade;
    }

    public long getId() {
        return id;
    }

    public long getIdUsuarioInstituicao() {
        return idUsuarioInstituicao;
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

    public LocalDate getPrazo() {
        return prazo;
    }

    public String getPrioridade() {
        return prioridade;
    }
}
