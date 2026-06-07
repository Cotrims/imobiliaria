package br.ufscar.dc.dsw.imobiliaria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@Table(name = "Usuario")
public class Usuario extends AbstractEntity<Long> {

    @NotBlank
    @Column(nullable = false, length = 45, unique = true)
    private String username;

    @NotBlank
    @Size(min = 6, max = 64)
    @Column(nullable = false, length = 64)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_ADMIN;

    @Column(nullable = false)
    private boolean enabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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