package br.ufscar.dc.dsw.imobiliaria.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufscar.dc.dsw.imobiliaria.dao.IClienteDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;

import java.util.Optional;

@Component
public class UniqueCPFValidator implements ConstraintValidator<UniqueCPF, Cliente> {

    @Autowired
    private IClienteDAO dao;

    @Override
    public boolean isValid(Cliente value, ConstraintValidatorContext context) {
        if (dao == null) {
            return true;
        }

        if (value == null || value.getCpf() == null) {
            return true;
        }

        Optional<Cliente> existing = dao.findByCpf(value.getCpf());
        if (existing.isEmpty()) {
            return true;
        }

        if (value.getId() != null && value.getId().equals(existing.get().getId())) {
            return true;
        }

        return false;
    }
}
