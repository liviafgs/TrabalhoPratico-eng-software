package conexaosolidaria.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Instituicao {
    private final long id;
    private final long idUsuario;
    private final String nome;
    private final String cnpj;
    private final String endereco;
    private final String contato;
    private final StatusInstituicao status;
    private final LocalDateTime criadoEm;

    public Instituicao(long id, long idUsuario, String nome, String cnpj, String endereco, String contato,
            LocalDateTime criadoEm) {
        this(id, idUsuario, nome, cnpj, endereco, contato, StatusInstituicao.PENDENTE, criadoEm);
    }

    public Instituicao(long id, long idUsuario, String nome, String cnpj, String endereco, String contato,
            StatusInstituicao status, LocalDateTime criadoEm) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.contato = contato;
        this.status = status == null ? StatusInstituicao.PENDENTE : status;
        this.criadoEm = criadoEm;
    }

    public long getId() {
        return id;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getContato() {
        return contato;
    }

    public StatusInstituicao getStatus() {
        return status;
    }

    public boolean isHabilitada() {
        return status == StatusInstituicao.HABILITADA;
    }

    public Instituicao comStatus(StatusInstituicao novoStatus) {
        return new Instituicao(id, idUsuario, nome, cnpj, endereco, contato, novoStatus, criadoEm);
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Instituicao that))
            return false;
        return id == that.id && idUsuario == that.idUsuario && Objects.equals(nome, that.nome)
                && Objects.equals(cnpj, that.cnpj) && Objects.equals(endereco, that.endereco)
                && Objects.equals(contato, that.contato) && status == that.status
                && Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUsuario, nome, cnpj, endereco, contato, status, criadoEm);
    }
}
