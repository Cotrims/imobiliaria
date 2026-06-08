package br.ufscar.dc.dsw.imobiliaria.domain;

import br.ufscar.dc.dsw.imobiliaria.validation.UniqueCPF;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

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
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@UniqueCPF
@Table(name = "Cliente", uniqueConstraints = {
        @UniqueConstraint(columnNames = "cpf")
})
public class Cliente extends AbstractEntity<Long> {

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @CPF
    @NotBlank
    @Column(nullable = false, unique = true)
    private String cpf;

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

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropostaCompra> propostas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_CLIENTE;

    public Cliente() {
    }

    public Cliente(String cpf, String nome,
            String telefone, String sexo, LocalDate dataNascimento) {
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
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

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}