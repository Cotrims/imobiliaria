package br.ufscar.dc.dsw.imobiliaria.conversor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;

@Component
public class ImobiliariaConversor implements Converter<String, Imobiliaria> {

    @Autowired
    private IImobiliariaService service;

    @Override
    public Imobiliaria convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        try {
            Long id = Long.parseLong(source);
            return service.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
