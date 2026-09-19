package conexaosolidaria.exception;

public class CadastroUsuarioException extends RuntimeException {
    public CadastroUsuarioException(String message) {
        super(message);
    }

    public CadastroUsuarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
