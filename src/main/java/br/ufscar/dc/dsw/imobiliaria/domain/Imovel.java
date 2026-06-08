package br.ufscar.dc.dsw.imobiliaria.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;

@SuppressWarnings("serial")
@Entity
@Table(name = "Imovel")
public class Imovel extends AbstractEntity<Long> {

    @NotBlank
    @Column(nullable = false)
    private String endereco;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cidade_id", nullable = false)
    @NotNull
    private Cidade cidade;

    @Column(length = 2000)
    private String descricao;

    @NotNull
    @DecimalMin("0.0")
    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,##0.00")
    @Column(nullable = false)
    private BigDecimal valor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "imobiliaria_id", nullable = false)
    @NotNull
    private Imobiliaria imobiliaria;

    @JsonIgnore
    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropostaCompra> propostas = new ArrayList<>();

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FotoImovel> fotos = new ArrayList<>();

    public Imovel() {
    }

    public Imovel(String endereco, Cidade cidade, String descricao,
            BigDecimal valor, Imobiliaria imobiliaria) {
        this.endereco = endereco;
        this.cidade = cidade;
        this.descricao = descricao;
        this.valor = valor;
        this.imobiliaria = imobiliaria;
    }

    public String getEndereco() {
        return endereco;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Imobiliaria getImobiliaria() {
        return imobiliaria;
    }

    public List<PropostaCompra> getPropostas() {
        return propostas;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setImobiliaria(Imobiliaria imobiliaria) {
        this.imobiliaria = imobiliaria;
    }

    public List<FotoImovel> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoImovel> fotos) {
        this.fotos = fotos;
    }
}