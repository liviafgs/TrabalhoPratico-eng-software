package conexaosolidaria.model;

import java.time.LocalDateTime;

public class Usuario {
    private final long id;
    private final String nome;
    private final String email;
    private final String senhaHash;
    private final PerfilUsuario perfil;
    private final LocalDateTime criadoEm;

    public Usuario(long id, String nome, String email, String senhaHash, PerfilUsuario perfil, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.criadoEm = criadoEm;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
