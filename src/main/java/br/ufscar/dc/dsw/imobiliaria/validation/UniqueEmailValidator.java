package br.ufscar.dc.dsw.imobiliaria.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufscar.dc.dsw.imobiliaria.dao.IUsuarioDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;

import java.util.Optional;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, Usuario> {

    @Autowired
    private IUsuarioDAO dao;

    @Override
    public boolean isValid(Usuario value, ConstraintValidatorContext context) {
        if (dao == null) {
            return true;
        }

        if (value == null || value.getEmail() == null) {
            return true;
        }

        Optional<Usuario> existing = dao.findByEmail(value.getEmail());

        if (existing.isEmpty()) {
            return true;
        }

        if (value.getId() != null && value.getId().equals(existing.get().getId())) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("email")
                .addConstraintViolation();

        return false;
    }
}
