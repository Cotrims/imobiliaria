package br.ufscar.dsw.imobiliaria.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "imoveis")
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String endereco;

    @NotBlank
    @Column(nullable = false)
    private String cidade;

    @Column(length = 2000)
    private String descricao;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal valor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "imobiliaria_id", nullable = false)
    private Imobiliaria imobiliaria;

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoImovel> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropostaCompra> propostas = new ArrayList<>();

    public Imovel() {
    }

    public Imovel(String endereco, String cidade, String descricao,
            BigDecimal valor, Imobiliaria imobiliaria) {
        this.endereco = endereco;
        this.cidade = cidade;
        this.descricao = descricao;
        this.valor = valor;
        this.imobiliaria = imobiliaria;
    }

    public void adicionarFoto(FotoImovel foto) {
        if (this.fotos.size() >= 10) {
            throw new IllegalStateException("Um imóvel pode ter no máximo 10 fotos.");
        }

        foto.setImovel(this);
        this.fotos.add(foto);
    }

    public Long getId() {
        return id;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
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

    public List<FotoImovel> getFotos() {
        return fotos;
    }

    public List<PropostaCompra> getPropostas() {
        return propostas;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setCidade(String cidade) {
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
}