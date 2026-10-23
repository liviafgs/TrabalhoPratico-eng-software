package conexaosolidaria.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Alimento {
    private final long id;
    private final String nome;
    private final String categoria;
    private final String unidadeMedida;
    private final String descricao;
    private final LocalDateTime criadoEm;

    public Alimento(long id, String nome, String categoria, String unidadeMedida, String descricao,
            LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.unidadeMedida = unidadeMedida;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Alimento alimento))
            return false;
        return id == alimento.id && Objects.equals(nome, alimento.nome) && Objects.equals(categoria, alimento.categoria)
                && Objects.equals(unidadeMedida, alimento.unidadeMedida)
                && Objects.equals(descricao, alimento.descricao) && Objects.equals(criadoEm, alimento.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, categoria, unidadeMedida, descricao, criadoEm);
    }
}
