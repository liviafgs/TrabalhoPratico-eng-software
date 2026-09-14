package conexaosolidaria.dto;

import conexaosolidaria.model.TipoUsuario;

public class CadastroUsuarioRequest {
    private final String nome;
    private final String email;
    private final String senha;
    private final String telefone;
    private final TipoUsuario tipoUsuario;

    public CadastroUsuarioRequest(String nome, String email, String senha, String telefone, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
}
