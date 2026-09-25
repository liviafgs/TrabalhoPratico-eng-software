package conexaosolidaria.security;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.Permissao;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class AccessControl {
    private final Map<PerfilUsuario, Set<Permissao>> permissoesPorPerfil;

    public AccessControl() {
        permissoesPorPerfil = new EnumMap<>(PerfilUsuario.class);
        permissoesPorPerfil.put(PerfilUsuario.ESTABELECIMENTO_DOADOR, EnumSet.of(
                Permissao.CADASTRAR_ESTABELECIMENTO,
                Permissao.CONSULTAR_ESTABELECIMENTOS,
                Permissao.ATUALIZAR_ESTABELECIMENTO,
                Permissao.CADASTRAR_ALIMENTO,
                Permissao.CONSULTAR_ALIMENTOS,
                Permissao.CADASTRAR_OFERTA,
                Permissao.CONSULTAR_OFERTAS,
                Permissao.CONFIRMAR_RETIRADA_OFERTA,
                Permissao.CONSULTAR_DEMANDAS));
        permissoesPorPerfil.put(PerfilUsuario.INSTITUICAO_BENEFICIARIA, EnumSet.of(
                Permissao.CADASTRAR_INSTITUICAO,
                Permissao.CONSULTAR_INSTITUICOES,
                Permissao.CADASTRAR_NUTRICIONISTA,
                Permissao.CONSULTAR_NUTRICIONISTAS,
                Permissao.CADASTRAR_PERFIL_ATENDIMENTO,
                Permissao.CONSULTAR_PERFIL_ATENDIMENTO,
                Permissao.HABILITAR_INSTITUICAO,
                Permissao.CONSULTAR_ALIMENTOS,
                Permissao.CONSULTAR_OFERTAS,
                Permissao.SOLICITAR_ALIMENTO,
                Permissao.CANCELAR_SOLICITACAO_OFERTA,
                Permissao.CADASTRAR_DEMANDA));
        permissoesPorPerfil.put(PerfilUsuario.NUTRICIONISTA, EnumSet.of(
                Permissao.CONSULTAR_ESTABELECIMENTOS,
                Permissao.CONSULTAR_INSTITUICOES,
                Permissao.CONSULTAR_NUTRICIONISTAS,
                Permissao.CONSULTAR_PERFIL_ATENDIMENTO,
                Permissao.HABILITAR_INSTITUICAO));
        permissoesPorPerfil.put(PerfilUsuario.ADMINISTRADOR, EnumSet.allOf(Permissao.class));
    }

    public void require(AuthenticatedUser user, Permissao permissao) {
        if (!hasPermission(user, permissao)) {
            throw new ApiException(403, "Acesso negado para o perfil " + user.perfil().getDescricao() + ".");
        }
    }

    public boolean hasPermission(AuthenticatedUser user, Permissao permissao) {
        if (user == null || permissao == null) {
            return false;
        }

        return permissionsOf(user.perfil()).contains(permissao);
    }

    public Set<Permissao> permissionsOf(PerfilUsuario perfil) {
        return permissoesPorPerfil.getOrDefault(perfil, Set.of());
    }
}
