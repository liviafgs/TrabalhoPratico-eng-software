package conexaosolidaria.service;

import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.ConsumoPerCapita;

import java.math.BigDecimal;

/**
 * Regra unica de coerencia da demanda (RF-18, RN-07, RN-08 e RN-13).
 * A quantidade de referencia e usada como parametro de coerencia, nao como cota.
 */
public class CoerenciaDemandaService {
    /** Quantidade ate 20% acima da referencia ainda e considerada coerente. */
    public static final double TOLERANCIA = 1.2;

    private final ConsumoPerCapitaService consumoPerCapitaService;

    public CoerenciaDemandaService(ConsumoPerCapitaService consumoPerCapitaService) {
        this.consumoPerCapitaService = consumoPerCapitaService;
    }

    public ResultadoCoerencia analisar(String alimento, String unidadeMedida, double quantidade,
            int beneficiarios, Double consumoInformado) {
        if (consumoInformado != null && consumoInformado <= 0) {
            throw new ApiException(400, "Consumo per capita deve ser maior que zero.");
        }

        Double consumo = consumoInformado;
        if (consumo == null) {
            consumo = consumoPerCapitaService.buscar(alimento, unidadeMedida)
                    .map(ConsumoPerCapita::getQuantidadePorPessoa)
                    .orElse(null);
        }

        if (consumo == null) {
            return new ResultadoCoerencia(null, null, false,
                    "Nao ha consumo per capita definido para este alimento e unidade; "
                            + "a coerencia da quantidade nao foi verificada.");
        }

        double referencia = beneficiarios * consumo;
        boolean acima = quantidade > referencia * TOLERANCIA + 1e-9;
        String mensagem = acima
                ? "Quantidade solicitada acima da referencia de " + formatar(referencia)
                        + " " + unidadeMedida.trim() + ". Informe uma justificativa para registrar a demanda."
                : "Quantidade dentro da referencia de " + formatar(referencia) + " " + unidadeMedida.trim() + ".";

        return new ResultadoCoerencia(consumo, referencia, acima, mensagem);
    }

    private static String formatar(double valor) {
        return BigDecimal.valueOf(valor).stripTrailingZeros().toPlainString();
    }

    public record ResultadoCoerencia(Double consumoPerCapita, Double quantidadeReferencia,
            boolean acimaDaReferencia, String mensagem) {
    }
}
