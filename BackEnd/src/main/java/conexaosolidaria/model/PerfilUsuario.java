package conexaosolidaria.model;

import java.util.Locale;

public enum PerfilUsuario {
    ESTABELECIMENTO_DOADOR("Estabelecimento doador"),
    INSTITUICAO_BENEFICIARIA("Instituicao beneficiaria"),
    NUTRICIONISTA("Nutricionista responsavel"),
    ADMINISTRADOR("Administrador");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static PerfilUsuario from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Perfil de usuario e obrigatorio.");
        }

        String normalized = value.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');

        for (PerfilUsuario perfil : values()) {
            if (perfil.name().equals(normalized)) {
                return perfil;
            }
        }

        throw new IllegalArgumentException("Perfil de usuario invalido: " + value);
    }
}
