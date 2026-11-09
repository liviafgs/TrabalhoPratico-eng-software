package conexaosolidaria.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Lista de ouvintes de um evento do dominio (padrao Observer). Um ouvinte com defeito nao pode
 * desfazer a operacao principal, entao a falha e registrada e a operacao segue.
 */
final class Ouvintes<T> {
    private final List<Consumer<T>> ouvintes = new CopyOnWriteArrayList<>();

    void adicionar(Consumer<T> ouvinte) {
        ouvintes.add(ouvinte);
    }

    void publicar(T evento) {
        for (Consumer<T> ouvinte : ouvintes) {
            try {
                ouvinte.accept(evento);
            } catch (RuntimeException e) {
                System.err.println("Falha ao processar evento: " + e.getMessage());
            }
        }
    }
}
