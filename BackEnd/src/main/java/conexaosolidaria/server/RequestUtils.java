package conexaosolidaria.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RequestUtils {
    private RequestUtils() {
    }

    public static Map<String, String> readJsonObject(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return parseSimpleJsonObject(body);
    }

    public static String authorizationHeader(HttpExchange exchange) {
        return exchange.getRequestHeaders().getFirst("Authorization");
    }

    public static String required(Map<String, String> body, String fieldName) {
        String value = body.get(fieldName);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo obrigatorio: " + fieldName);
        }
        return value;
    }

    public static double requiredDouble(Map<String, String> body, String fieldName) {
        try {
            return Double.parseDouble(required(body, fieldName));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo numerico invalido: " + fieldName);
        }
    }

    private static Map<String, String> parseSimpleJsonObject(String json) {
        Map<String, String> values = new LinkedHashMap<>();
        if (json == null || json.isBlank()) {
            return values;
        }

        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Envie um objeto JSON valido.");
        }

        int index = 1;
        while (index < trimmed.length() - 1) {
            index = skipWhitespaceAndComma(trimmed, index);
            if (index >= trimmed.length() - 1) {
                break;
            }

            ParsedString key = readQuotedString(trimmed, index);
            index = skipWhitespace(trimmed, key.nextIndex());
            if (index >= trimmed.length() || trimmed.charAt(index) != ':') {
                throw new IllegalArgumentException("JSON invalido: esperado ':' apos a chave.");
            }

            index = skipWhitespace(trimmed, index + 1);
            ParsedValue value = readValue(trimmed, index);
            values.put(key.value(), value.value());
            index = value.nextIndex();
        }

        return values;
    }

    private static ParsedString readQuotedString(String text, int start) {
        if (start >= text.length() || text.charAt(start) != '"') {
            throw new IllegalArgumentException("JSON invalido: esperado texto entre aspas.");
        }

        StringBuilder value = new StringBuilder();
        boolean escaping = false;
        for (int i = start + 1; i < text.length(); i++) {
            char current = text.charAt(i);
            if (escaping) {
                value.append(switch (current) {
                    case '"', '\\', '/' -> current;
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    default -> current;
                });
                escaping = false;
                continue;
            }

            if (current == '\\') {
                escaping = true;
            } else if (current == '"') {
                return new ParsedString(value.toString(), i + 1);
            } else {
                value.append(current);
            }
        }

        throw new IllegalArgumentException("JSON invalido: texto sem fechamento de aspas.");
    }

    private static ParsedValue readValue(String text, int start) {
        if (start < text.length() && text.charAt(start) == '"') {
            ParsedString parsed = readQuotedString(text, start);
            return new ParsedValue(parsed.value(), parsed.nextIndex());
        }

        StringBuilder value = new StringBuilder();
        int index = start;
        while (index < text.length()) {
            char current = text.charAt(index);
            if (current == ',' || current == '}') {
                break;
            }
            value.append(current);
            index++;
        }

        return new ParsedValue(value.toString().trim(), index);
    }

    private static int skipWhitespaceAndComma(String text, int index) {
        while (index < text.length()) {
            char current = text.charAt(index);
            if (!Character.isWhitespace(current) && current != ',') {
                break;
            }
            index++;
        }
        return index;
    }

    private static int skipWhitespace(String text, int index) {
        while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
            index++;
        }
        return index;
    }

    private record ParsedString(String value, int nextIndex) {
    }

    private record ParsedValue(String value, int nextIndex) {
    }
}
