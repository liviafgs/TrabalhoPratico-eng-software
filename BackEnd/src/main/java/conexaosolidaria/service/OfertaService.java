package conexaosolidaria.service;

import conexaosolidaria.dto.AuthenticatedUser;
import conexaosolidaria.exception.ApiException;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.Permissao;
import conexaosolidaria.repository.OfertaRepository;
import conexaosolidaria.security.AccessControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class OfertaService {
    private final OfertaRepository ofertaRepository;
    private final AccessControl accessControl;

    public OfertaService(OfertaRepository ofertaRepository, AccessControl accessControl) {
        this.ofertaRepository = ofertaRepository;
        this.accessControl = accessControl;
    }

    public Oferta cadastrar(AuthenticatedUser user, String alimento, double quantidade, String unidadeMedida,
            LocalDate retiradaAte) {
        return cadastrar(user, 0L, alimento, quantidade, unidadeMedida, retiradaAte, null, null, null);
    }

    public Oferta cadastrar(AuthenticatedUser user, long idAlimento, String alimento, double quantidade,
            String unidadeMedida, LocalDate retiradaAte, LocalDate validade, LocalDateTime dataHoraRetirada,
            String localRetirada) {
        accessControl.require(user, Permissao.CADASTRAR_OFERTA);
        validarOferta(alimento, quantidade, unidadeMedida, retiradaAte);

        return ofertaRepository.save(new Oferta(
                0,
                user.id(),
                idAlimento,
                alimento.trim(),
                quantidade,
                unidadeMedida.trim(),
                retiradaAte,
                validade,
                dataHoraRetirada,
                localRetirada == null ? null : localRetirada.trim()));
    }

    public List<Oferta> listar(AuthenticatedUser user) {
        accessControl.require(user, Permissao.CONSULTAR_OFERTAS);
        return ofertaRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Oferta::getId))
                .toList();
    }

    private void validarOferta(String alimento, double quantidade, String unidadeMedida, LocalDate retiradaAte) {
        if (alimento == null || alimento.isBlank()) {
            throw new ApiException(400, "Alimento e obrigatorio.");
        }
        if (quantidade <= 0) {
            throw new ApiException(400, "Quantidade deve ser maior que zero.");
        }
        if (unidadeMedida == null || unidadeMedida.isBlank()) {
            throw new ApiException(400, "Unidade de medida e obrigatoria.");
        }
        if (retiradaAte == null) {
            throw new ApiException(400, "Data limite para retirada e obrigatoria.");
        }
    }
}
