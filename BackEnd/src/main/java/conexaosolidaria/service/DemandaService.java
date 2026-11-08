package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.repository.DemandaRepository;
import conexaosolidaria.security.AccessControl;
import conexaosolidaria.service.CoerenciaDemandaService.ResultadoCoerencia;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class DemandaService {
    private static final Set<String> PRIORIDADES = Set.of("ALTA", "MEDIA", "BAIXA");

    private final DemandaRepository demandaRepository;
    private final AccessControl accessControl;
    private final CoerenciaDemandaService coerenciaService;

    public DemandaService(DemandaRepository demandaRepository, AccessControl accessControl,
            CoerenciaDemandaService coerenciaService) {
        this.demandaRepository = demandaRepository;
        this.accessControl = accessControl;
        this.coerenciaService = coerenciaService;
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

        return demandaRepository.save(new Demanda(
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

    public List<Demanda> listar(AuthenticatedUser user) {
        accessControl.require(user, Permissao.CONSULTAR_DEMANDAS);
        return demandaRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Demanda::getId))
                .toList();
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
