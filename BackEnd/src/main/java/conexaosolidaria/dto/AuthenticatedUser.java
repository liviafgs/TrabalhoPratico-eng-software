package conexaosolidaria.dto;

import conexaosolidaria.model.PerfilUsuario;

public record AuthenticatedUser(long id, String nome, String email, PerfilUsuario perfil) {
}
