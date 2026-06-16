package br.ufscar.dc.dsw.imobiliaria.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.ufscar.dc.dsw.imobiliaria.validation.UniqueEmail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@UniqueEmail
@Table(name = "Usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario extends AbstractEntity<Long> {

    @NotBlank
    @Email
    @Size(max = 45)
    @Column(nullable = false, length = 45, unique = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 64)
    @Column(nullable = false, length = 64)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    public Usuario() {

    }

    public Usuario(String email, String password, String role, boolean enabled) {
        setEmail(email);
        setRole(Role.valueOf(role));
        setPassword(password);
        setEnabled(enabled);
    }

    public String get() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}