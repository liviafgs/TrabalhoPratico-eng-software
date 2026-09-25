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
import java.util.Locale;

public class OfertaService {
    private final OfertaRepository ofertaRepository;
    private final AccessControl accessControl;
    private final SolicitacaoOfertaService solicitacaoService;

    public OfertaService(OfertaRepository ofertaRepository, AccessControl accessControl) {
        this(ofertaRepository, accessControl, null);
    }

    public OfertaService(OfertaRepository ofertaRepository, AccessControl accessControl,
            SolicitacaoOfertaService solicitacaoService) {
        this.ofertaRepository = ofertaRepository;
        this.accessControl = accessControl;
        this.solicitacaoService = solicitacaoService;
    }

    public Oferta cadastrar(AuthenticatedUser user, String alimento, double quantidade, String unidadeMedida,
            LocalDate retiradaAte) {
        return cadastrar(user, 0L, alimento, quantidade, unidadeMedida, retiradaAte, null, null, null);
    }

    public Oferta cadastrar(AuthenticatedUser user, long idAlimento, String alimento, double quantidade,
            String unidadeMedida, LocalDate retiradaAte, LocalDate validade, LocalDateTime dataHoraRetirada,
            String localRetirada) {
        return cadastrar(user, idAlimento, alimento, null, quantidade, unidadeMedida, retiradaAte, validade,
                dataHoraRetirada, localRetirada);
    }

    public Oferta cadastrar(AuthenticatedUser user, long idAlimento, String alimento, String categoria,
            double quantidade, String unidadeMedida, LocalDate retiradaAte, LocalDate validade,
            LocalDateTime dataHoraRetirada, String localRetirada) {
        accessControl.require(user, Permissao.CADASTRAR_OFERTA);
        validarOferta(alimento, quantidade, unidadeMedida, retiradaAte);

        return ofertaRepository.save(new Oferta(
                0,
                user.id(),
                idAlimento,
                alimento.trim(),
                categoria == null ? null : categoria.trim(),
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

    public List<Oferta> consultarDisponiveis(AuthenticatedUser user, OfertaFiltros filtros) {
        accessControl.require(user, Permissao.CONSULTAR_OFERTAS);
        OfertaFiltros filtroSeguro = filtros == null ? OfertaFiltros.vazios() : filtros;
        return ofertaRepository.findAll().stream()
                .filter(oferta -> corresponde(oferta, filtroSeguro))
                .sorted(Comparator.comparingLong(Oferta::getId))
                .toList();
    }

    public double quantidadeDisponivel(Oferta oferta) {
        if (solicitacaoService == null) {
            return oferta.getQuantidade();
        }
        return solicitacaoService.quantidadeDisponivel(oferta);
    }

    private boolean corresponde(Oferta oferta, OfertaFiltros filtros) {
        boolean disponivel = !oferta.isExpirada() && quantidadeDisponivel(oferta) > 0;
        if (Boolean.TRUE.equals(filtros.disponivel()) && !disponivel) {
            return false;
        }
        if (Boolean.FALSE.equals(filtros.disponivel()) && disponivel) {
            return false;
        }
        return contem(oferta.getAlimento(), filtros.alimento())
                && contem(oferta.getCategoria(), filtros.categoria())
                && contem(oferta.getLocalRetirada(), filtros.localizacao())
                && correspondeData(oferta, filtros.data());
    }

    private boolean contem(String valor, String filtro) {
        return filtro == null || filtro.isBlank()
                || (valor != null && valor.toLowerCase(Locale.ROOT).contains(filtro.trim().toLowerCase(Locale.ROOT)));
    }

    private boolean correspondeData(Oferta oferta, LocalDate data) {
        return data == null
                || data.equals(oferta.getValidade())
                || data.equals(oferta.getRetiradaAte())
                || (oferta.getDataHoraRetirada() != null && data.equals(oferta.getDataHoraRetirada().toLocalDate()));
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
