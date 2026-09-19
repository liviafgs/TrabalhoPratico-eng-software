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
                Permissao.CADASTRAR_OFERTA,
                Permissao.CONSULTAR_DEMANDAS
        ));
        permissoesPorPerfil.put(PerfilUsuario.INSTITUICAO_BENEFICIARIA, EnumSet.of(
                Permissao.CONSULTAR_OFERTAS,
                Permissao.SOLICITAR_ALIMENTO,
                Permissao.CADASTRAR_DEMANDA
        ));
        permissoesPorPerfil.put(PerfilUsuario.NUTRICIONISTA, EnumSet.noneOf(Permissao.class));
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
