package conexaosolidaria.model;

public enum Permissao {
    CADASTRAR_ESTABELECIMENTO("Cadastrar estabelecimento"),
    CONSULTAR_ESTABELECIMENTOS("Consultar estabelecimentos"),
    ATUALIZAR_ESTABELECIMENTO("Atualizar estabelecimento"),
    CADASTRAR_INSTITUICAO("Cadastrar instituicao"),
    CONSULTAR_INSTITUICOES("Consultar instituicoes"),
    CADASTRAR_NUTRICIONISTA("Cadastrar nutricionista responsavel"),
    CONSULTAR_NUTRICIONISTAS("Consultar nutricionistas"),
    CADASTRAR_ALIMENTO("Cadastrar alimento"),
    CONSULTAR_ALIMENTOS("Consultar alimentos"),
    CADASTRAR_PERFIL_ATENDIMENTO("Cadastrar perfil de atendimento"),
    CONSULTAR_PERFIL_ATENDIMENTO("Consultar perfil de atendimento"),
    HABILITAR_INSTITUICAO("Habilitar instituicao"),
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
