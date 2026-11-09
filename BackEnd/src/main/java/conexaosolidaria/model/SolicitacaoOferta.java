package conexaosolidaria.model;

import java.time.LocalDateTime;

public class SolicitacaoOferta {
    private final long id;
    private final long idOferta;
    private final long idUsuarioInstituicao;
    private final double quantidade;
    private final StatusSolicitacaoOferta status;
    private final LocalDateTime criadoEm;
    private final LocalDateTime retiradaAgendadaPara;

    public SolicitacaoOferta(long id, long idOferta, long idUsuarioInstituicao, double quantidade,
            StatusSolicitacaoOferta status, LocalDateTime criadoEm) {
        this(id, idOferta, idUsuarioInstituicao, quantidade, status, criadoEm, null);
    }

    public SolicitacaoOferta(long id, long idOferta, long idUsuarioInstituicao, double quantidade,
            StatusSolicitacaoOferta status, LocalDateTime criadoEm, LocalDateTime retiradaAgendadaPara) {
        this.id = id;
        this.idOferta = idOferta;
        this.idUsuarioInstituicao = idUsuarioInstituicao;
        this.quantidade = quantidade;
        this.status = status;
        this.criadoEm = criadoEm;
        this.retiradaAgendadaPara = retiradaAgendadaPara;
    }

    public long getId() {
        return id;
    }

    public long getIdOferta() {
        return idOferta;
    }

    public long getIdUsuarioInstituicao() {
        return idUsuarioInstituicao;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public StatusSolicitacaoOferta getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getRetiradaAgendadaPara() {
        return retiradaAgendadaPara;
    }

    public SolicitacaoOferta comStatus(StatusSolicitacaoOferta novoStatus) {
        return new SolicitacaoOferta(id, idOferta, idUsuarioInstituicao, quantidade, novoStatus, criadoEm,
                retiradaAgendadaPara);
    }

    public SolicitacaoOferta agendadaPara(LocalDateTime dataHora) {
        return new SolicitacaoOferta(id, idOferta, idUsuarioInstituicao, quantidade,
                StatusSolicitacaoOferta.AGENDADA, criadoEm, dataHora);
    }
}
