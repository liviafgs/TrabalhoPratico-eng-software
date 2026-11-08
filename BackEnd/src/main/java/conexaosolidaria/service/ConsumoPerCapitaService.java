package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.ConsumoPerCapita;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.repository.ConsumoPerCapitaRepository;
import conexaosolidaria.security.AccessControl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ConsumoPerCapitaService {
    private final ConsumoPerCapitaRepository consumoRepository;
    private final AccessControl accessControl;

    public ConsumoPerCapitaService(ConsumoPerCapitaRepository consumoRepository, AccessControl accessControl) {
        this.consumoRepository = consumoRepository;
        this.accessControl = accessControl;
    }

    public ConsumoPerCapita cadastrar(AuthenticatedUser user, String alimento, String unidadeMedida,
            double quantidadePorPessoa) {
        accessControl.require(user, Permissao.CADASTRAR_CONSUMO_PER_CAPITA);

        if (alimento == null || alimento.isBlank()) {
            throw new ApiException(400, "Alimento e obrigatorio.");
        }
        if (unidadeMedida == null || unidadeMedida.isBlank()) {
            throw new ApiException(400, "Unidade de medida e obrigatoria.");
        }
        if (quantidadePorPessoa <= 0) {
            throw new ApiException(400, "Consumo per capita deve ser maior que zero.");
        }

        return consumoRepository.save(new ConsumoPerCapita(
                0,
                alimento.trim(),
                unidadeMedida.trim(),
                quantidadePorPessoa,
                LocalDateTime.now()));
    }

    public List<ConsumoPerCapita> listar(AuthenticatedUser user) {
        accessControl.require(user, Permissao.CONSULTAR_CONSUMO_PER_CAPITA);
        return consumoRepository.findAll().stream()
                .sorted(Comparator.comparingLong(ConsumoPerCapita::getId))
                .toList();
    }

    public Optional<ConsumoPerCapita> buscar(String alimento, String unidadeMedida) {
        if (alimento == null || alimento.isBlank() || unidadeMedida == null || unidadeMedida.isBlank()) {
            return Optional.empty();
        }
        return consumoRepository.findByAlimentoEUnidade(alimento, unidadeMedida);
    }
}
