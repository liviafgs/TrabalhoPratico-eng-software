package conexaosolidaria.dto;

import conexaosolidaria.model.Usuario;

public record UsuarioResponse(long id, String nome, String email, String perfil, String perfilDescricao) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                usuario.getPerfil().getDescricao()
        );
    }
}
