package conexaosolidaria.service;

/** Uma correspondencia entre oferta e demanda, com os nomes dos participantes para exibicao. */
public record Oportunidade(Correspondencia correspondencia, String estabelecimentoNome, String instituicaoNome) {
}
