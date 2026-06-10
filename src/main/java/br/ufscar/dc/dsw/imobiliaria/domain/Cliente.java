package br.ufscar.dc.dsw.imobiliaria.domain;

import br.ufscar.dc.dsw.imobiliaria.validation.UniqueCPF;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@UniqueCPF
@Table(name = "Cliente")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Cliente extends Usuario {

    @CPF
    @NotBlank
    @Column(nullable = false, unique = true)
    private String CPF;

    @NotBlank
    @Size(max = 100, min = 3)
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String telefone;

    @NotBlank
    private String sexo;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropostaCompra> propostas = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String email, String senha, String CPF, String nome,
            String telefone, String sexo, LocalDate dataNascimento) {
        super(email, senha, "ROLE_CLIENTE", true);

        this.CPF = CPF;
        this.nome = nome;
        this.telefone = telefone;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
    }

    public String getCPF() {
        return CPF;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getSexo() {
        return sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public List<PropostaCompra> getPropostas() {
        return propostas;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}