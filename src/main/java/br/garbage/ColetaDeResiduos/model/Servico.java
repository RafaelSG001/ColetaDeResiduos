package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(schema = "APP", name = "SERVICO",
       uniqueConstraints = {@UniqueConstraint(name = "UQ_SERVICO_TIPO_RESIDUO", columnNames = "TIPO_RESIDUO")})
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SERVICO", nullable = false, updatable = false)
    private Long idServico;

    @Column(name = "TIPO_RESIDUO", nullable = false, length = 50)
    private String tipoResiduo;

    @Column(name = "CLASSE_PERICULOSIDADE", nullable = false, length = 60)
    private String classePericulosidade;

    @Column(name = "DESTINO_PADRAO", nullable = false, length = 50)
    private String destinoPadrao;

    @Column(name = "RECICLAVEL", nullable = false)
    private Boolean reciclavel = true;

    @Column(name = "TARIFA_POR_TONELADA", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaPorTonelada;

    public Servico() {}

    public Servico(String tipoResiduo, String classePericulosidade, String destinoPadrao, Boolean reciclavel, BigDecimal tarifaPorTonelada) {
        this.tipoResiduo = tipoResiduo;
        this.classePericulosidade = classePericulosidade;
        this.destinoPadrao = destinoPadrao;
        this.reciclavel = reciclavel;
        this.tarifaPorTonelada = tarifaPorTonelada;
    }

    public Long getIdServico() { return idServico; }
    public void setIdServico(Long idServico) { this.idServico = idServico; }
    public Long getId() { return idServico; }
    public void setId(Long id) { this.idServico = id; }

    public String getTipoResiduo() { return tipoResiduo; }
    public void setTipoResiduo(String tipoResiduo) { this.tipoResiduo = tipoResiduo; }

    public String getClassePericulosidade() { return classePericulosidade; }
    public void setClassePericulosidade(String classePericulosidade) { this.classePericulosidade = classePericulosidade; }

    public String getDestinoPadrao() { return destinoPadrao; }
    public void setDestinoPadrao(String destinoPadrao) { this.destinoPadrao = destinoPadrao; }

    public Boolean getReciclavel() { return reciclavel; }
    public void setReciclavel(Boolean reciclavel) { this.reciclavel = reciclavel; }

    public BigDecimal getTarifaPorTonelada() { return tarifaPorTonelada; }
    public void setTarifaPorTonelada(BigDecimal tarifaPorTonelada) { this.tarifaPorTonelada = tarifaPorTonelada; }
}
