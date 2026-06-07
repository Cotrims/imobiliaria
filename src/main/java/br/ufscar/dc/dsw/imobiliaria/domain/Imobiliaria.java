package br.ufscar.dc.dsw.imobiliaria.domain;

import java.util.ArrayList;
import java.util.List;

import br.ufscar.dc.dsw.imobiliaria.validation.UniqueCNPJ;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@SuppressWarnings("serial")
@Entity
@UniqueCNPJ
@Table(name = "Imobiliaria", uniqueConstraints = {
        @UniqueConstraint(columnNames = "CNPJ")
})
public class Imobiliaria extends AbstractEntity<Long> {

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String CNPJ;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @OneToMany(mappedBy = "imobiliaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imovel> imoveis = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_IMOBILIARIA;

    public Imobiliaria() {
    }

    public Imobiliaria(String CNPJ, String nome, String descricao) {
        this.CNPJ = CNPJ;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}