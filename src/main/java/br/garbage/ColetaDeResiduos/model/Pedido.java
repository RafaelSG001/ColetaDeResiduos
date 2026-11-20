package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(schema = "APP", name = "PEDIDO",
       uniqueConstraints = {@UniqueConstraint(name = "UQ_PEDIDO_PROTOCOLO", columnNames = "CODIGO_PROTOCOLO")},
       indexes = {
           @Index(name = "IDX_PEDIDO_CLIENTE", columnList = "ID_CLIENTE"),
           @Index(name = "IDX_PEDIDO_EMPRESA", columnList = "ID_EMPRESA"),
           @Index(name = "IDX_PEDIDO_STATUS", columnList = "STATUS")
       })
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PEDIDO", nullable = false, updatable = false)
    private Long idPedido;

    @Column(name = "CODIGO_PROTOCOLO", nullable = false, length = 30)
    private String codigoProtocolo;

    @Column(name = "DATA_SOLICITACAO", nullable = false)
    private LocalDate dataSolicitacao;

    @Column(name = "DATA_COLETA_EXECUCAO", nullable = false)
    private LocalDate dataColetaExecucao;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 25)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(name = "CUSTO_OPERACIONAL_TOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoOperacionalTotal = BigDecimal.ZERO;

    @Column(name = "RECEITA_RECICLAGEM_TOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal receitaReciclagemTotal = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_CLIENTE", nullable = false, foreignKey = @ForeignKey(name = "FK_PEDIDO_CLIENTE"))
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_EMPRESA", nullable = false, foreignKey = @ForeignKey(name = "FK_PEDIDO_EMPRESA"))
    private Empresa empresa;

    public Pedido() {}

    public BigDecimal calcularSaldoLiquido() {
        if (receitaReciclagemTotal == null || custoOperacionalTotal == null) return BigDecimal.ZERO;
        return receitaReciclagemTotal.subtract(custoOperacionalTotal);
    }

    public void avancarStatus(StatusPedido novo) {
        if (this.status != null && !this.status.podeTransitarPara(novo)) {
            throw new IllegalStateException("Transição de status inválida: não é permitido alterar de "
                    + this.status.getRotulo() + " para " + novo.getRotulo());
        }
        this.status = novo;
        if (novo == StatusPedido.CANCELADO) {
            this.receitaReciclagemTotal = BigDecimal.ZERO;
        }
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }
    public Long getId() { return idPedido; }
    public void setId(Long id) { this.idPedido = id; }

    public String getCodigoProtocolo() { return codigoProtocolo; }
    public void setCodigoProtocolo(String codigoProtocolo) { this.codigoProtocolo = codigoProtocolo; }

    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDate dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public LocalDate getDataColetaExecucao() { return dataColetaExecucao; }
    public void setDataColetaExecucao(LocalDate dataColetaExecucao) { this.dataColetaExecucao = dataColetaExecucao; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public BigDecimal getCustoOperacionalTotal() { return custoOperacionalTotal; }
    public void setCustoOperacionalTotal(BigDecimal custoOperacionalTotal) { this.custoOperacionalTotal = custoOperacionalTotal; }

    public BigDecimal getReceitaReciclagemTotal() { return receitaReciclagemTotal; }
    public void setReceitaReciclagemTotal(BigDecimal receitaReciclagemTotal) { this.receitaReciclagemTotal = receitaReciclagemTotal; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}
