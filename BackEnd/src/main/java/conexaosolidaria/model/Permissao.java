package conexaosolidaria.model;

public enum Permissao {
    CADASTRAR_OFERTA("Cadastrar oferta de alimentos"),
    CONSULTAR_OFERTAS("Consultar ofertas disponiveis"),
    SOLICITAR_ALIMENTO("Solicitar alimento"),
    CADASTRAR_DEMANDA("Cadastrar demanda"),
    CONSULTAR_DEMANDAS("Consultar demandas"),
    CONSULTAR_USUARIOS("Consultar usuarios cadastrados"),
    GERENCIAR_PLATAFORMA("Gerenciar plataforma");

    private final String descricao;

    Permissao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
