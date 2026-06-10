package br.ufscar.dc.dsw.imobiliaria.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.ufscar.dc.dsw.imobiliaria.validation.UniqueCNPJ;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@SuppressWarnings("serial")
@Entity
@UniqueCNPJ
@Table(name = "Imobiliaria")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Imobiliaria extends Usuario {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String CNPJ;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @JsonIgnore
    @OneToMany(mappedBy = "imobiliaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imovel> imoveis = new ArrayList<>();

    public Imobiliaria() {
    }

    public Imobiliaria(String email, String senha, String CNPJ, String nome, String descricao) {
        super(email, senha, "ROLE_IMOBILIARIA", true);

        this.CNPJ = CNPJ;
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Imovel> getImoveis() {
        return imoveis;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}