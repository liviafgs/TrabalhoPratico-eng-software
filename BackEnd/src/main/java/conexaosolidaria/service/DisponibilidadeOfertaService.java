package conexaosolidaria.service;

import conexaosolidaria.model.Oferta;
import conexaosolidaria.model.SolicitacaoOferta;
import conexaosolidaria.model.StatusSolicitacaoOferta;
import conexaosolidaria.repository.SolicitacaoOfertaRepository;

/**
 * Unico ponto do sistema que decide se uma oferta ainda esta disponivel
 * e quanto resta dela (RF-11, RN-03, RN-04, RNF-04).
 */
public class DisponibilidadeOfertaService {
    private final SolicitacaoOfertaRepository solicitacaoRepository;

    public DisponibilidadeOfertaService(SolicitacaoOfertaRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public double quantidadeDisponivel(Oferta oferta) {
        double solicitada = solicitacaoRepository.findAll().stream()
                .filter(item -> item.getIdOferta() == oferta.getId())
                .filter(item -> item.getStatus() == StatusSolicitacaoOferta.SOLICITADA
                        || item.getStatus() == StatusSolicitacaoOferta.RETIRADA)
                .mapToDouble(SolicitacaoOferta::getQuantidade)
                .sum();
        return Math.max(0, oferta.getQuantidade() - solicitada);
    }

    public boolean estaDisponivel(Oferta oferta) {
        return !oferta.isExpirada() && quantidadeDisponivel(oferta) > 0;
    }
}
