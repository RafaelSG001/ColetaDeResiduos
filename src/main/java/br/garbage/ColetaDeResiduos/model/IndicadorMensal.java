package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(schema = "APP", name = "INDICADOR_MENSAL",
       indexes = {@Index(name = "IDX_IND_EMPRESA_MES", columnList = "ID_EMPRESA, MES_REFERENCIA")})
public class IndicadorMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INDICADOR", nullable = false, updatable = false)
    private Long idIndicador;

    @Column(name = "MES_REFERENCIA", nullable = false, length = 7)
    private String mesReferencia;

    @Column(name = "TOTAL_RESIDUOS_TON", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalResiduosTon;

    @Column(name = "TAXA_RECICLAGEM_PERCENTUAL", nullable = false, precision = 6, scale = 2)
    private BigDecimal taxaReciclagemPercentual;

    @Column(name = "CUSTO_TOTAL_OPERACAO", nullable = false, precision = 14, scale = 2)
    private BigDecimal custoTotalOperacao;

    @Column(name = "RECEITA_TOTAL", nullable = false, precision = 14, scale = 2)
    private BigDecimal receitaTotal;

    @Column(name = "EMISSAO_CO2_EVITATON", nullable = false, precision = 12, scale = 2)
    private BigDecimal emissaoCo2Evitaton;

    @Column(name = "CLIENTES_ATENDIDOS", nullable = false)
    private Integer clientesAtendidos;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_EMPRESA", nullable = false, foreignKey = @ForeignKey(name = "FK_INDICADOR_EMPRESA"))
    private Empresa empresa;

    public IndicadorMensal() {}

    public BigDecimal calcularLucroOperacional() {
        if (receitaTotal == null || custoTotalOperacao == null) return BigDecimal.ZERO;
        return receitaTotal.subtract(custoTotalOperacao);
    }

    public Long getIdIndicador() { return idIndicador; }
    public void setIdIndicador(Long idIndicador) { this.idIndicador = idIndicador; }
    public Long getId() { return idIndicador; }
    public void setId(Long id) { this.idIndicador = id; }

    public String getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(String mesReferencia) { this.mesReferencia = mesReferencia; }

    public BigDecimal getTotalResiduosTon() { return totalResiduosTon; }
    public void setTotalResiduosTon(BigDecimal totalResiduosTon) { this.totalResiduosTon = totalResiduosTon; }

    public BigDecimal getTaxaReciclagemPercentual() { return taxaReciclagemPercentual; }
    public void setTaxaReciclagemPercentual(BigDecimal taxaReciclagemPercentual) { this.taxaReciclagemPercentual = taxaReciclagemPercentual; }

    public BigDecimal getCustoTotalOperacao() { return custoTotalOperacao; }
    public void setCustoTotalOperacao(BigDecimal custoTotalOperacao) { this.custoTotalOperacao = custoTotalOperacao; }

    public BigDecimal getReceitaTotal() { return receitaTotal; }
    public void setReceitaTotal(BigDecimal receitaTotal) { this.receitaTotal = receitaTotal; }

    public BigDecimal getEmissaoCo2Evitaton() { return emissaoCo2Evitaton; }
    public void setEmissaoCo2Evitaton(BigDecimal emissaoCo2Evitaton) { this.emissaoCo2Evitaton = emissaoCo2Evitaton; }

    public Integer getClientesAtendidos() { return clientesAtendidos; }
    public void setClientesAtendidos(Integer clientesAtendidos) { this.clientesAtendidos = clientesAtendidos; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}
