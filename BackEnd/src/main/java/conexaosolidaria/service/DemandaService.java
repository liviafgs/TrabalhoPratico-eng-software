package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.PerfilUsuario;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.service.CoerenciaDemandaService.ResultadoCoerencia;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DemandaService {
    private static final Set<String> PRIORIDADES = Set.of("ALTA", "MEDIA", "BAIXA");

    private final Ouvintes<Demanda> ouvintesCadastro = new Ouvintes<>();
    private final DemandaRepository demandaRepository;
    private final AccessControl accessControl;
    private final CoerenciaDemandaService coerenciaService;
    private final InstituicaoRepository instituicaoRepository;

    public DemandaService(DemandaRepository demandaRepository, AccessControl accessControl,
            CoerenciaDemandaService coerenciaService, InstituicaoRepository instituicaoRepository) {
        this.demandaRepository = demandaRepository;
        this.accessControl = accessControl;
        this.coerenciaService = coerenciaService;
        this.instituicaoRepository = instituicaoRepository;
    }

    public Demanda cadastrar(AuthenticatedUser user, String alimento, double quantidade, String unidadeMedida,
            LocalDate prazo, String prioridade, String finalidade, int beneficiarios, Double consumoPerCapita,
            String justificativa) {
        accessControl.require(user, Permissao.CADASTRAR_DEMANDA);
        validarDemanda(alimento, quantidade, unidadeMedida, prazo, prioridade);
        validarFinalidadeEBeneficiarios(finalidade, beneficiarios);

        ResultadoCoerencia coerencia = coerenciaService.analisar(
                alimento, unidadeMedida, quantidade, beneficiarios, consumoPerCapita);
        boolean temJustificativa = justificativa != null && !justificativa.isBlank();
        if (coerencia.acimaDaReferencia() && !temJustificativa) {
            throw new ApiException(409, coerencia.mensagem());
        }

        Demanda salva = demandaRepository.save(new Demanda(
                0,
                user.id(),
                alimento.trim(),
                quantidade,
                unidadeMedida.trim(),
                prazo,
                prioridade.trim().toUpperCase(),
                finalidade.trim(),
                beneficiarios,
                coerencia.consumoPerCapita(),
                coerencia.quantidadeReferencia(),
                coerencia.acimaDaReferencia(),
                coerencia.acimaDaReferencia() ? justificativa.trim() : null
        ));
        ouvintesCadastro.publicar(salva);
        return salva;
    }

    /** Registra um ouvinte chamado depois de cada demanda cadastrada. */
    public void aoCadastrar(Consumer<Demanda> ouvinte) {
        ouvintesCadastro.adicionar(ouvinte);
    }

    public ResultadoCoerencia verificarCoerencia(AuthenticatedUser user, String alimento, double quantidade,
            String unidadeMedida, int beneficiarios, Double consumoPerCapita) {
        accessControl.require(user, Permissao.CADASTRAR_DEMANDA);
        if (alimento == null || alimento.isBlank()) {
            throw new ApiException(400, "Alimento e obrigatorio.");
        }
        if (quantidade <= 0) {
            throw new ApiException(400, "Quantidade deve ser maior que zero.");
        }
        if (unidadeMedida == null || unidadeMedida.isBlank()) {
            throw new ApiException(400, "Unidade de medida e obrigatoria.");
        }
        if (beneficiarios <= 0) {
            throw new ApiException(400, "Quantidade de beneficiarios deve ser maior que zero.");
        }

        return coerenciaService.analisar(alimento, unidadeMedida, quantidade, beneficiarios, consumoPerCapita);
    }

    public List<DemandaConsulta> listar(AuthenticatedUser user) {
        return listar(user, DemandaFiltros.vazios());
    }

    /**
     * Instituicoes veem apenas as proprias demandas; estabelecimentos e administradores veem todas.
     */
    public List<DemandaConsulta> listar(AuthenticatedUser user, DemandaFiltros filtros) {
        accessControl.require(user, Permissao.CONSULTAR_DEMANDAS);
        DemandaFiltros filtrosSeguros = filtros == null ? DemandaFiltros.vazios() : filtros;
        validarFiltros(filtrosSeguros);

        LocalDate hoje = LocalDate.now();
        return demandaRepository.findAll().stream()
                .filter(demanda -> user.perfil() != PerfilUsuario.INSTITUICAO_BENEFICIARIA
                        || demanda.getIdUsuarioInstituicao() == user.id())
                .map(this::comInstituicao)
                .filter(consulta -> corresponde(consulta, filtrosSeguros, hoje))
                .sorted(Comparator.comparingLong(consulta -> consulta.demanda().getId()))
                .toList();
    }

    private DemandaConsulta comInstituicao(Demanda demanda) {
        Instituicao instituicao = instituicaoRepository.findByUsuarioId(demanda.getIdUsuarioInstituicao())
                .orElse(null);
        return new DemandaConsulta(
                demanda,
                instituicao == null ? null : instituicao.getNome(),
                instituicao == null ? null : instituicao.getEndereco());
    }

    private boolean corresponde(DemandaConsulta consulta, DemandaFiltros filtros, LocalDate hoje) {
        Demanda demanda = consulta.demanda();
        boolean vigente = !demanda.getPrazo().isBefore(hoje);
        if (Boolean.TRUE.equals(filtros.disponivel()) && !vigente) {
            return false;
        }
        if (Boolean.FALSE.equals(filtros.disponivel()) && vigente) {
            return false;
        }
        if (filtros.prioridade() != null && !filtros.prioridade().isBlank()
                && !demanda.getPrioridade().equals(filtros.prioridade().trim().toUpperCase())) {
            return false;
        }
        if (filtros.prazoDe() != null && demanda.getPrazo().isBefore(filtros.prazoDe())) {
            return false;
        }
        if (filtros.prazoAte() != null && demanda.getPrazo().isAfter(filtros.prazoAte())) {
            return false;
        }
        return DemandaFiltros.contem(demanda.getAlimento(), filtros.alimento())
                && DemandaFiltros.contem(consulta.instituicaoEndereco(), filtros.localizacao());
    }

    private void validarFiltros(DemandaFiltros filtros) {
        if (filtros.prioridade() != null && !filtros.prioridade().isBlank()
                && !PRIORIDADES.contains(filtros.prioridade().trim().toUpperCase())) {
            throw new ApiException(400, "Prioridade deve ser ALTA, MEDIA ou BAIXA.");
        }
        if (filtros.prazoDe() != null && filtros.prazoAte() != null && filtros.prazoDe().isAfter(filtros.prazoAte())) {
            throw new ApiException(400, "A data inicial do prazo nao pode ser posterior a data final.");
        }
    }

    private void validarFinalidadeEBeneficiarios(String finalidade, int beneficiarios) {
        if (finalidade == null || finalidade.isBlank()) {
            throw new ApiException(400, "Finalidade e obrigatoria.");
        }
        if (beneficiarios <= 0) {
            throw new ApiException(400, "Quantidade de beneficiarios deve ser maior que zero.");
        }
    }

    private void validarDemanda(String alimento, double quantidade, String unidadeMedida, LocalDate prazo, String prioridade) {
        if (alimento == null || alimento.isBlank()) {
            throw new ApiException(400, "Alimento e obrigatorio.");
        }
        if (quantidade <= 0) {
            throw new ApiException(400, "Quantidade deve ser maior que zero.");
        }
        if (unidadeMedida == null || unidadeMedida.isBlank()) {
            throw new ApiException(400, "Unidade de medida e obrigatoria.");
        }
        if (prazo == null) {
            throw new ApiException(400, "Prazo e obrigatorio.");
        }
        if (prioridade == null || !PRIORIDADES.contains(prioridade.trim().toUpperCase())) {
            throw new ApiException(400, "Prioridade deve ser ALTA, MEDIA ou BAIXA.");
        }
    }
}
