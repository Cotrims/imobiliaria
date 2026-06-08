package br.ufscar.dc.dsw.imobiliaria.conversor;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;

public class BigDecimalConversor implements Converter<String, BigDecimal> {

    @Override
    public BigDecimal convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        String value = source.trim()
                .replace("R$", "")
                .replace(" ", "");

        if (value.contains(",")) {
            value = value.replace(".", "").replace(",", ".");
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
