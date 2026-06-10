package br.ufscar.dc.dsw.imobiliaria.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = UniqueCPFValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCPF {
    String message() default "{cliente.cpf.unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
