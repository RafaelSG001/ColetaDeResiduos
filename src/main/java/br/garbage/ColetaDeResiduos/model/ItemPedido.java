package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(schema = "APP", name = "ITEM_PEDIDO",
       uniqueConstraints = {@UniqueConstraint(name = "UQ_ITEM_MTR", columnNames = "NUMERO_MTR_CERTIFICADO")},
       indexes = {
           @Index(name = "IDX_ITEM_PEDIDO", columnList = "ID_PEDIDO"),
           @Index(name = "IDX_ITEM_SERVICO", columnList = "ID_SERVICO"),
           @Index(name = "IDX_ITEM_ENDERECO", columnList = "ID_ENDERECO")
       })
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ITEM", nullable = false, updatable = false)
    private Long idItem;

    @Column(name = "PESO_TONELADAS", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoToneladas;

    @Column(name = "DESTINO_FINAL", nullable = false, length = 50)
    private String destinoFinal;

    @Column(name = "CUSTO_ITEM", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoItem;

    @Column(name = "RECEITA_RECICLAGEM_ITEM", nullable = false, precision = 12, scale = 2)
    private BigDecimal receitaReciclagemItem;

    @Column(name = "NUMERO_MTR_CERTIFICADO", nullable = false, length = 35)
    private String numeroMtrCertificado;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_PEDIDO", nullable = false, foreignKey = @ForeignKey(name = "FK_ITEM_PEDIDO"))
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_SERVICO", nullable = false, foreignKey = @ForeignKey(name = "FK_ITEM_SERVICO"))
    private Servico servico;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_ENDERECO", nullable = false, foreignKey = @ForeignKey(name = "FK_ITEM_ENDERECO"))
    private Endereco endereco;

    public ItemPedido() {}

    public BigDecimal calcularSubtotalLiquido() {
        if (receitaReciclagemItem == null || custoItem == null) return BigDecimal.ZERO;
        return receitaReciclagemItem.subtract(custoItem);
    }

    public Long getIdItem() { return idItem; }
    public void setIdItem(Long idItem) { this.idItem = idItem; }
    public Long getId() { return idItem; }
    public void setId(Long id) { this.idItem = id; }

    public BigDecimal getPesoToneladas() { return pesoToneladas; }
    public void setPesoToneladas(BigDecimal pesoToneladas) { this.pesoToneladas = pesoToneladas; }

    public String getDestinoFinal() { return destinoFinal; }
    public void setDestinoFinal(String destinoFinal) { this.destinoFinal = destinoFinal; }

    public BigDecimal getCustoItem() { return custoItem; }
    public void setCustoItem(BigDecimal custoItem) { this.custoItem = custoItem; }

    public BigDecimal getReceitaReciclagemItem() { return receitaReciclagemItem; }
    public void setReceitaReciclagemItem(BigDecimal receitaReciclagemItem) { this.receitaReciclagemItem = receitaReciclagemItem; }

    public String getNumeroMtrCertificado() { return numeroMtrCertificado; }
    public void setNumeroMtrCertificado(String numeroMtrCertificado) { this.numeroMtrCertificado = numeroMtrCertificado; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
}
