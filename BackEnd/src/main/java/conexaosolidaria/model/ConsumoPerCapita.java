package conexaosolidaria.model;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;

public class ConsumoPerCapita {
    private final long id;
    private final String alimento;
    private final String unidadeMedida;
    private final double quantidadePorPessoa;
    private final LocalDateTime criadoEm;

    public ConsumoPerCapita(long id, String alimento, String unidadeMedida, double quantidadePorPessoa,
            LocalDateTime criadoEm) {
        this.id = id;
        this.alimento = alimento;
        this.unidadeMedida = unidadeMedida;
        this.quantidadePorPessoa = quantidadePorPessoa;
        this.criadoEm = criadoEm;
    }

    public long getId() {
        return id;
    }

    public String getAlimento() {
        return alimento;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public double getQuantidadePorPessoa() {
        return quantidadePorPessoa;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public static String chave(String alimento, String unidadeMedida) {
        return normalizar(alimento) + "|" + normalizar(unidadeMedida);
    }

    private static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT);
    }
}
