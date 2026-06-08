package br.ufscar.dc.dsw.imobiliaria.conversor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.ufscar.dc.dsw.imobiliaria.domain.Cidade;
import br.ufscar.dc.dsw.imobiliaria.service.spec.ICidadeService;

@Component
public class CidadeConversor implements Converter<String, Cidade> {

    @Autowired
    private ICidadeService service;

    @Override
    public Cidade convert(String source) {
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
