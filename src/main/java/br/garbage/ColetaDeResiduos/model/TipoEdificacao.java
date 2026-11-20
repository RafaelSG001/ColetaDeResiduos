package br.garbage.ColetaDeResiduos.model;

import jakarta.persistence.*;

@Entity
@Table(schema = "APP", name = "TIPO_EDIFICACAO",
       uniqueConstraints = {@UniqueConstraint(name = "UQ_TIPO_EDIF_NOME", columnNames = "NOME_SEGMENTO")})
public class TipoEdificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO", nullable = false, updatable = false)
    private Long idTipo;

    @Column(name = "NOME_SEGMENTO", nullable = false, length = 60)
    private String nomeSegmento;

    @Column(name = "DESCRICAO_NORMA", nullable = false, length = 150)
    private String descricaoNorma;

    @Column(name = "EXIGE_LICENCA_ESPECIAL", nullable = false)
    private Boolean exigeLicencaEspecial = false;

    public TipoEdificacao() {}

    public TipoEdificacao(String nomeSegmento, String descricaoNorma, Boolean exigeLicencaEspecial) {
        this.nomeSegmento = nomeSegmento;
        this.descricaoNorma = descricaoNorma;
        this.exigeLicencaEspecial = exigeLicencaEspecial;
    }

    public Long getIdTipo() { return idTipo; }
    public void setIdTipo(Long idTipo) { this.idTipo = idTipo; }
    public Long getId() { return idTipo; }
    public void setId(Long id) { this.idTipo = id; }

    public String getNomeSegmento() { return nomeSegmento; }
    public void setNomeSegmento(String nomeSegmento) { this.nomeSegmento = nomeSegmento; }

    public String getDescricaoNorma() { return descricaoNorma; }
    public void setDescricaoNorma(String descricaoNorma) { this.descricaoNorma = descricaoNorma; }

    public Boolean getExigeLicencaEspecial() { return exigeLicencaEspecial; }
    public void setExigeLicencaEspecial(Boolean exigeLicencaEspecial) { this.exigeLicencaEspecial = exigeLicencaEspecial; }
}
