package conexaosolidaria.model;

import java.util.Locale;

public enum StatusInstituicao {
    PENDENTE("Pendente"),
    HABILITADA("Habilitada");

    private final String descricao;

    StatusInstituicao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusInstituicao from(String value) {
        if (value == null || value.isBlank()) {
            return PENDENTE;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        for (StatusInstituicao status : values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Status da instituicao invalido: " + value);
    }
}
