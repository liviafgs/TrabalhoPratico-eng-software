package conexaosolidaria.service;

import java.text.Normalizer;
import java.util.Locale;

/** Comparacao de texto usada nos filtros de consulta: ignora maiusculas e acentos. */
final class BuscaTexto {
    private BuscaTexto() {
    }

    static boolean contem(String texto, String trecho) {
        if (trecho == null || trecho.isBlank()) {
            return true;
        }
        if (texto == null) {
            return false;
        }
        return normalizar(texto).contains(normalizar(trecho));
    }

    /** Texto igual ao outro, ignorando maiusculas e acentos. */
    static boolean igual(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return normalizar(a).equals(normalizar(b));
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
