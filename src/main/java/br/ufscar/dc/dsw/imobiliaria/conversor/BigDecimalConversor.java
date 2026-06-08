package br.ufscar.dc.dsw.imobiliaria.conversor;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;

public class BigDecimalConversor implements Converter<String, BigDecimal> {

    @Override
    public BigDecimal convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        source = source.replace(",", ".");

        try {
            return new BigDecimal(Double.parseDouble(source));
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
