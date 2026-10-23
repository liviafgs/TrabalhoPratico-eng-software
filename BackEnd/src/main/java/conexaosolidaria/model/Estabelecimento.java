package conexaosolidaria.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Estabelecimento {
    private final long id;
    private final long idUsuario;
    private final String nome;
    private final String cnpj;
    private final String endereco;
    private final String contato;
    private final LocalDateTime criadoEm;

    public Estabelecimento(long id, long idUsuario, String nome, String cnpj, String endereco, String contato,
            LocalDateTime criadoEm) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.contato = contato;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Estabelecimento that))
            return false;
        return id == that.id && idUsuario == that.idUsuario && Objects.equals(nome, that.nome)
                && Objects.equals(cnpj, that.cnpj) && Objects.equals(endereco, that.endereco)
                && Objects.equals(contato, that.contato) && Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUsuario, nome, cnpj, endereco, contato, criadoEm);
    }
}
