package conexaosolidaria.service;

import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Alimento;
import conexaosolidaria.repository.AlimentoRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class AlimentoService {
    private final AlimentoRepository alimentoRepository;

    public AlimentoService(AlimentoRepository alimentoRepository) {
        this.alimentoRepository = alimentoRepository;
    }

    public Alimento cadastrar(String nome, String categoria, String unidadeMedida) {
        validarTexto(nome, "Nome do alimento");
        validarTexto(categoria, "Categoria");
        validarTexto(unidadeMedida, "Unidade de medida");

        if (alimentoRepository.existsByNome(nome)) {
            throw new ApiException(409, "Ja existe um alimento cadastrado com este nome.");
        }

        return alimentoRepository.save(new Alimento(
                0,
                nome.trim(),
                categoria.trim(),
                unidadeMedida.trim(),
                null,
                LocalDateTime.now()));
    }

    public Alimento consultarPorNome(String nome) {
        return alimentoRepository.findByNome(nome)
                .orElseThrow(() -> new ApiException(404, "Alimento nao encontrado."));
    }

    public List<Alimento> listarTodos() {
        return alimentoRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Alimento::getId))
                .toList();
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ApiException(400, campo + " e obrigatorio.");
        }
    }
}
