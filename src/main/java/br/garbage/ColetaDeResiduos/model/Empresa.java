package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Year;

@Entity
@Table(schema = "APP", name = "EMPRESA",
       uniqueConstraints = {
           @UniqueConstraint(name = "UQ_EMPRESA_NOME", columnNames = "NOME_EMPRESA"),
           @UniqueConstraint(name = "UQ_EMPRESA_CNPJ", columnNames = "CNPJ_OPERADORA")
       })
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPRESA", nullable = false, updatable = false)
    private Long idEmpresa;

    @Column(name = "NOME_EMPRESA", nullable = false, length = 100)
    private String nomeEmpresa;

    @Column(name = "CNPJ_OPERADORA", nullable = false, length = 20)
    private String cnpjOperadora;

    @Column(name = "CIDADE_SEDE", nullable = false, length = 60)
    private String cidadeSede;

    @Column(name = "ESTADO_SEDE", nullable = false, length = 2)
    private String estadoSede;

    @Column(name = "LATITUDE", nullable = false, precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", nullable = false, precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "ANO_FUNDACAO", nullable = false)
    private Integer anoFundacao;

    @Column(name = "FUNCIONARIOS", nullable = false)
    private Integer funcionarios;

    @Column(name = "CLIENTES_ATIVOS", nullable = false)
    private Integer clientesAtivos = 0;

    @Column(name = "CERTIFICACAO_ISO14001", nullable = false)
    private Boolean certificacaoIso14001 = false;

    public Empresa() {}

    public int calcularTempoAtuacao() {
        return anoFundacao == null ? 0 : Year.now().getValue() - anoFundacao;
    }

    public Long getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }
    public Long getId() { return idEmpresa; }
    public void setId(Long id) { this.idEmpresa = id; }

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getCnpjOperadora() { return cnpjOperadora; }
    public void setCnpjOperadora(String cnpjOperadora) { this.cnpjOperadora = cnpjOperadora; }

    public String getCidadeSede() { return cidadeSede; }
    public void setCidadeSede(String cidadeSede) { this.cidadeSede = cidadeSede; }

    public String getEstadoSede() { return estadoSede; }
    public void setEstadoSede(String estadoSede) { this.estadoSede = estadoSede; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Integer getAnoFundacao() { return anoFundacao; }
    public void setAnoFundacao(Integer anoFundacao) { this.anoFundacao = anoFundacao; }

    public Integer getFuncionarios() { return funcionarios; }
    public void setFuncionarios(Integer funcionarios) { this.funcionarios = funcionarios; }

    public Integer getClientesAtivos() { return clientesAtivos; }
    public void setClientesAtivos(Integer clientesAtivos) { this.clientesAtivos = clientesAtivos; }

    public Boolean getCertificacaoIso14001() { return certificacaoIso14001; }
    public void setCertificacaoIso14001(Boolean certificacaoIso14001) { this.certificacaoIso14001 = certificacaoIso14001; }
}
