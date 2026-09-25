package conexaosolidaria.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class PerfilAtendimentoInstituicao {
    private final long id;
    private final long idInstituicao;
    private final int numeroBeneficiarios;
    private final int capacidadeAtendimento;
    private final String publicoAtendido;
    private final String frequenciaAtendimento;
    private final LocalDateTime criadoEm;

    public PerfilAtendimentoInstituicao(long id, long idInstituicao, int numeroBeneficiarios, int capacidadeAtendimento,
            String publicoAtendido, String frequenciaAtendimento, LocalDateTime criadoEm) {
        this.id = id;
        this.idInstituicao = idInstituicao;
        this.numeroBeneficiarios = numeroBeneficiarios;
        this.capacidadeAtendimento = capacidadeAtendimento;
        this.publicoAtendido = publicoAtendido;
        this.frequenciaAtendimento = frequenciaAtendimento;
        this.criadoEm = criadoEm;
    }

    public long getId() {
        return id;
    }

    public long getIdInstituicao() {
        return idInstituicao;
    }

    public int getNumeroBeneficiarios() {
        return numeroBeneficiarios;
    }

    public int getCapacidadeAtendimento() {
        return capacidadeAtendimento;
    }

    public String getPublicoAtendido() {
        return publicoAtendido;
    }

    public String getFrequenciaAtendimento() {
        return frequenciaAtendimento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PerfilAtendimentoInstituicao that))
            return false;
        return id == that.id && idInstituicao == that.idInstituicao && numeroBeneficiarios == that.numeroBeneficiarios
                && capacidadeAtendimento == that.capacidadeAtendimento
                && Objects.equals(publicoAtendido, that.publicoAtendido)
                && Objects.equals(frequenciaAtendimento, that.frequenciaAtendimento)
                && Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idInstituicao, numeroBeneficiarios, capacidadeAtendimento, publicoAtendido,
                frequenciaAtendimento, criadoEm);
    }
}
