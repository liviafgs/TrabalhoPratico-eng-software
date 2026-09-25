package conexaosolidaria.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Oferta {
    private final long id;
    private final long idUsuarioDoador;
    private final long idAlimento;
    private final String alimento;
    private final double quantidade;
    private final String unidadeMedida;
    private final LocalDate retiradaAte;
    private final LocalDate validade;
    private final LocalDateTime dataHoraRetirada;
    private final String localRetirada;

    public Oferta(long id, long idUsuarioDoador, String alimento, double quantidade, String unidadeMedida,
            LocalDate retiradaAte) {
        this(id, idUsuarioDoador, 0L, alimento, quantidade, unidadeMedida, retiradaAte, null, null, null);
    }

    public Oferta(long id, long idUsuarioDoador, long idAlimento, String alimento, double quantidade,
            String unidadeMedida, LocalDate retiradaAte, LocalDate validade, LocalDateTime dataHoraRetirada,
            String localRetirada) {
        this.id = id;
        this.idUsuarioDoador = idUsuarioDoador;
        this.idAlimento = idAlimento;
        this.alimento = alimento;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.retiradaAte = retiradaAte;
        this.validade = validade;
        this.dataHoraRetirada = dataHoraRetirada;
        this.localRetirada = localRetirada;
    }

    public long getId() {
        return id;
    }

    public long getIdUsuarioDoador() {
        return idUsuarioDoador;
    }

    public long getIdAlimento() {
        return idAlimento;
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

    public LocalDate getValidade() {
        return validade;
    }

    public LocalDateTime getDataHoraRetirada() {
        return dataHoraRetirada;
    }

    public String getLocalRetirada() {
        return localRetirada;
    }
}
