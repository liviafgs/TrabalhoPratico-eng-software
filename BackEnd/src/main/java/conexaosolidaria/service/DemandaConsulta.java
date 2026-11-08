package conexaosolidaria.service;

import conexaosolidaria.model.Demanda;

/** Demanda acompanhada dos dados da instituicao que a cadastrou (quando ela ja esta cadastrada). */
public record DemandaConsulta(Demanda demanda, String instituicaoNome, String instituicaoEndereco) {
}
