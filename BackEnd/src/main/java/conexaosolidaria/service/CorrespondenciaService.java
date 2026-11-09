package conexaosolidaria.service;

import conexaosolidaria.model.Demanda;
import conexaosolidaria.model.Instituicao;
import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.StatusInstituicao;
import conexaosolidaria.repository.InstituicaoRepository;
import conexaosolidaria.service.Correspondencia.NivelAtendimento;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Regra unica para decidir se uma oferta pode atender a uma demanda (RF-15 a RF-17, RN-02).
 * Uma oferta corresponde a uma demanda quando:
 * 1. o alimento e o mesmo (um nome contem o outro, ignorando maiusculas e acentos);
 * 2. a unidade de medida e a mesma;
 * 3. a oferta esta disponivel (nao expirada e com quantidade restante);
 * 4. o prazo da demanda ainda nao venceu e a retirada marcada na oferta nao e posterior a ele;
 * 5. a instituicao da demanda esta habilitada para receber alimentos.
 * A quantidade nao impede a correspondencia: define se o atendimento e total ou parcial.
 */
public class CorrespondenciaService {
    private final DisponibilidadeOfertaService disponibilidade;
    private final InstituicaoRepository instituicaoRepository;

    public CorrespondenciaService(DisponibilidadeOfertaService disponibilidade,
            InstituicaoRepository instituicaoRepository) {
        this.disponibilidade = disponibilidade;
        this.instituicaoRepository = instituicaoRepository;
    }

    public Optional<Correspondencia> avaliar(Oferta oferta, Demanda demanda) {
        if (!mesmoAlimento(oferta.getAlimento(), demanda.getAlimento())
                || !BuscaTexto.igual(oferta.getUnidadeMedida(), demanda.getUnidadeMedida())) {
            return Optional.empty();
        }
        if (!disponibilidade.estaDisponivel(oferta) || !prazoCompativel(oferta, demanda)
                || !instituicaoHabilitada(demanda)) {
            return Optional.empty();
        }

        double disponivel = disponibilidade.quantidadeDisponivel(oferta);
        NivelAtendimento nivel = disponivel >= demanda.getQuantidade()
                ? NivelAtendimento.TOTAL
                : NivelAtendimento.PARCIAL;
        return Optional.of(new Correspondencia(oferta, demanda, disponivel,
                Math.min(disponivel, demanda.getQuantidade()), nivel));
    }

    private boolean mesmoAlimento(String alimentoOferta, String alimentoDemanda) {
        return BuscaTexto.contem(alimentoOferta, alimentoDemanda)
                || BuscaTexto.contem(alimentoDemanda, alimentoOferta);
    }

    private boolean prazoCompativel(Oferta oferta, Demanda demanda) {
        if (demanda.getPrazo().isBefore(LocalDate.now())) {
            return false;
        }
        return oferta.getDataHoraRetirada() == null
                || !oferta.getDataHoraRetirada().toLocalDate().isAfter(demanda.getPrazo());
    }

    private boolean instituicaoHabilitada(Demanda demanda) {
        return instituicaoRepository.findByUsuarioId(demanda.getIdUsuarioInstituicao())
                .map(Instituicao::getStatus)
                .filter(status -> status == StatusInstituicao.HABILITADA)
                .isPresent();
    }
}
