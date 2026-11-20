package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;

@Entity
@Table(schema = "APP", name = "ENDERECO",
       indexes = {@Index(name = "IDX_ENDERECO_CIDADE_UF", columnList = "CIDADE, ESTADO_UF")})
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ENDERECO", nullable = false, updatable = false)
    private Long idEndereco;

    @Column(name = "LOGRADOURO", nullable = false, length = 120)
    private String logradouro;

    @Column(name = "NUMERO", nullable = false, length = 20)
    private String numero;

    @Column(name = "CEP", nullable = false, length = 12)
    private String cep;

    @Column(name = "CIDADE", nullable = false, length = 60)
    private String cidade;

    @Column(name = "ESTADO_UF", nullable = false, length = 2)
    private String estadoUf;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_TIPO_EDIFICACAO", nullable = false, foreignKey = @ForeignKey(name = "FK_ENDERECO_TIPO_EDIF"))
    private TipoEdificacao tipoEdificacao;

    public Endereco() {}

    public Endereco(String logradouro, String numero, String cep, String cidade, String estadoUf, TipoEdificacao tipoEdificacao) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
        this.cidade = cidade;
        this.estadoUf = estadoUf;
        this.tipoEdificacao = tipoEdificacao;
    }

    public String getCidadeUfFormatado() { return cidade + " - " + estadoUf; }

    public Long getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Long idEndereco) { this.idEndereco = idEndereco; }
    public Long getId() { return idEndereco; }
    public void setId(Long id) { this.idEndereco = id; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstadoUf() { return estadoUf; }
    public void setEstadoUf(String estadoUf) { this.estadoUf = estadoUf; }

    public TipoEdificacao getTipoEdificacao() { return tipoEdificacao; }
    public void setTipoEdificacao(TipoEdificacao tipoEdificacao) { this.tipoEdificacao = tipoEdificacao; }
}
