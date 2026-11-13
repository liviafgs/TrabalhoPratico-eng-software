package conexaosolidaria.model;

import java.time.LocalDate;

public class Demanda implements java.io.Serializable {
    private final long id;
    private final long idUsuarioInstituicao;
    private final String alimento;
    private final double quantidade;
    private final String unidadeMedida;
    private final LocalDate prazo;
    private final String prioridade;
    private final String finalidade;
    private final int beneficiarios;
    private final Double consumoPerCapita;
    private final Double quantidadeReferencia;
    private final boolean acimaDaReferencia;
    private final String justificativa;

    public Demanda(long id, long idUsuarioInstituicao, String alimento, double quantidade, String unidadeMedida,
            LocalDate prazo, String prioridade) {
        this(id, idUsuarioInstituicao, alimento, quantidade, unidadeMedida, prazo, prioridade,
                null, 0, null, null, false, null);
    }

    public Demanda(long id, long idUsuarioInstituicao, String alimento, double quantidade, String unidadeMedida,
            LocalDate prazo, String prioridade, String finalidade, int beneficiarios, Double consumoPerCapita,
            Double quantidadeReferencia, boolean acimaDaReferencia, String justificativa) {
        this.id = id;
        this.idUsuarioInstituicao = idUsuarioInstituicao;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.prazo = prazo;
        this.prioridade = prioridade;
        this.finalidade = finalidade;
        this.beneficiarios = beneficiarios;
        this.consumoPerCapita = consumoPerCapita;
        this.quantidadeReferencia = quantidadeReferencia;
        this.acimaDaReferencia = acimaDaReferencia;
        this.justificativa = justificativa;
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

    public String getFinalidade() {
        return finalidade;
    }

    public int getBeneficiarios() {
        return beneficiarios;
    }

    public Double getConsumoPerCapita() {
        return consumoPerCapita;
    }

    public Double getQuantidadeReferencia() {
        return quantidadeReferencia;
    }

    public boolean isAcimaDaReferencia() {
        return acimaDaReferencia;
    }

    public String getJustificativa() {
        return justificativa;
    }
}
