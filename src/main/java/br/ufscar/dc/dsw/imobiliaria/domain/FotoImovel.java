package br.ufscar.dc.dsw.imobiliaria.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "FotoImovel")
public class FotoImovel extends AbstractEntity<Long> {

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "imovel_id", nullable = false)
    private Imovel imovel;

    @Column(nullable = false, length = 512)
    private String nomeArquivo;

    public FotoImovel() {
    }

    public FotoImovel(Imovel imovel, String nomeArquivo) {
        this.imovel = imovel;
        this.nomeArquivo = nomeArquivo;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}
