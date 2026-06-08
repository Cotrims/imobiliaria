package br.ufscar.dc.dsw.imobiliaria.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufscar.dc.dsw.imobiliaria.dao.IImobiliariaDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;

@Component
public class UniqueCNPJValidator
        implements ConstraintValidator<UniqueCNPJ, br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria> {

    @Autowired
    private IImobiliariaDAO dao;

    @Override
    public boolean isValid(br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria value, ConstraintValidatorContext context) {
        if (dao == null) {
            return true;
        }

        if (value == null || value.getCNPJ() == null) {
            return true;
        }

        String CNPJ = value.getCNPJ();
        Optional<Imobiliaria> imobiliaria = dao.findByCNPJ(CNPJ);

        if (imobiliaria.isEmpty()) {
            return true;
        }

        if (value.getId() != null && value.getId().equals(imobiliaria.get().getId())) {
            return true;
        }

        return false;
    }
}