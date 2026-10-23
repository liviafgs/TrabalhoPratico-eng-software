package conexaosolidaria.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class NutricionistaResponsavel {
    private final long id;
    private final long idUsuario;
    private final long idInstituicao;
    private final String nome;
    private final String crn;
    private final String contato;
    private final LocalDateTime criadoEm;

    public NutricionistaResponsavel(long id, long idUsuario, long idInstituicao, String nome, String crn,
            String contato, LocalDateTime criadoEm) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idInstituicao = idInstituicao;
        this.nome = nome;
        this.crn = crn;
        this.contato = contato;
        this.criadoEm = criadoEm;
    }

    public long getId() {
        return id;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public long getIdInstituicao() {
        return idInstituicao;
    }

    public String getNome() {
        return nome;
    }

    public String getCrn() {
        return crn;
    }

    public String getContato() {
        return contato;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof NutricionistaResponsavel that))
            return false;
        return id == that.id && idUsuario == that.idUsuario && idInstituicao == that.idInstituicao
                && Objects.equals(nome, that.nome) && Objects.equals(crn, that.crn)
                && Objects.equals(contato, that.contato) && Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUsuario, idInstituicao, nome, crn, contato, criadoEm);
    }
}
