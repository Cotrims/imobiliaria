package br.ufscar.dc.dsw.imobiliaria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@SuppressWarnings("serial")
@Entity
@Table(name = "Cidade")
public class Cidade extends AbstractEntity<Long> {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nome;

    public Cidade() {
    }

    public Cidade(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
