package conexaosolidaria.model;

import java.time.LocalDateTime;

public class Notificacao implements java.io.Serializable {
    private final long id;
    private final long idUsuarioDestino;
    private final TipoNotificacao tipo;
    private final String mensagem;
    private final Long idSolicitacao;
    private final Long idOferta;
    private final Long idDemanda;
    private final LocalDateTime criadaEm;
    private final LocalDateTime lidaEm;

    public Notificacao(long id, long idUsuarioDestino, TipoNotificacao tipo, String mensagem, Long idSolicitacao,
            Long idOferta, Long idDemanda, LocalDateTime criadaEm, LocalDateTime lidaEm) {
        this.id = id;
        this.idUsuarioDestino = idUsuarioDestino;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.idSolicitacao = idSolicitacao;
        this.idOferta = idOferta;
        this.idDemanda = idDemanda;
        this.criadaEm = criadaEm;
        this.lidaEm = lidaEm;
    }

    public long getId() {
        return id;
    }

    public long getIdUsuarioDestino() {
        return idUsuarioDestino;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Long getIdSolicitacao() {
        return idSolicitacao;
    }

    public Long getIdOferta() {
        return idOferta;
    }

    public Long getIdDemanda() {
        return idDemanda;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public LocalDateTime getLidaEm() {
        return lidaEm;
    }

    public boolean isLida() {
        return lidaEm != null;
    }

    public Notificacao comoLida(LocalDateTime quando) {
        return new Notificacao(id, idUsuarioDestino, tipo, mensagem, idSolicitacao, idOferta, idDemanda, criadaEm,
                quando);
    }
}
