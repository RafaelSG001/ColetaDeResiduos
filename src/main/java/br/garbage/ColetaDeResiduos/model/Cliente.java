package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(schema = "APP", name = "CLIENTE",
       uniqueConstraints = {@UniqueConstraint(name = "UQ_CLIENTE_CNPJ", columnNames = "CNPJ")},
       indexes = {@Index(name = "IDX_CLIENTE_TIPO_EDIF", columnList = "ID_TIPO_EDIFICACAO")})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE", nullable = false, updatable = false)
    private Long idCliente;

    @Column(name = "RAZAO_SOCIAL", nullable = false, length = 120)
    private String razaoSocial;

    @Column(name = "CNPJ", nullable = false, length = 20)
    private String cnpj;

    @Column(name = "CIDADE", nullable = false, length = 60)
    private String cidade;

    @Column(name = "ESTADO_UF", nullable = false, length = 2)
    private String estadoUf;

    @Column(name = "VOLUME_CONTRATADO_MENSAL_TON", nullable = false, precision = 10, scale = 2)
    private BigDecimal volumeContratadoMensalTon;

    @Column(name = "CONTRATO_ATIVO", nullable = false)
    private Boolean contratoAtivo = true;

    @Column(name = "INDICE_SATISFACAO", nullable = false, precision = 4, scale = 1)
    private BigDecimal indiceSatisfacao;

    @Column(name = "TEMPO_CONTRATO_MESES", nullable = false)
    private Integer tempoContratoMeses;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_TIPO_EDIFICACAO", nullable = false, foreignKey = @ForeignKey(name = "FK_CLIENTE_TIPO_EDIF"))
    private TipoEdificacao tipoEdificacao;

    public Cliente() {}

    public BigDecimal calcularVolumeTotalContrato() {
        if (volumeContratadoMensalTon == null || tempoContratoMeses == null) return BigDecimal.ZERO;
        return volumeContratadoMensalTon.multiply(BigDecimal.valueOf(tempoContratoMeses));
    }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public Long getId() { return idCliente; }
    public void setId(Long id) { this.idCliente = id; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstadoUf() { return estadoUf; }
    public void setEstadoUf(String estadoUf) { this.estadoUf = estadoUf; }

    public BigDecimal getVolumeContratadoMensalTon() { return volumeContratadoMensalTon; }
    public void setVolumeContratadoMensalTon(BigDecimal volumeContratadoMensalTon) { this.volumeContratadoMensalTon = volumeContratadoMensalTon; }

    public Boolean getContratoAtivo() { return contratoAtivo; }
    public void setContratoAtivo(Boolean contratoAtivo) { this.contratoAtivo = contratoAtivo; }

    public BigDecimal getIndiceSatisfacao() { return indiceSatisfacao; }
    public void setIndiceSatisfacao(BigDecimal indiceSatisfacao) { this.indiceSatisfacao = indiceSatisfacao; }

    public Integer getTempoContratoMeses() { return tempoContratoMeses; }
    public void setTempoContratoMeses(Integer tempoContratoMeses) { this.tempoContratoMeses = tempoContratoMeses; }

    public TipoEdificacao getTipoEdificacao() { return tipoEdificacao; }
    public void setTipoEdificacao(TipoEdificacao tipoEdificacao) { this.tipoEdificacao = tipoEdificacao; }
}
