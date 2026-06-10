package br.ufscar.dc.dsw.imobiliaria.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufscar.dc.dsw.imobiliaria.dao.IImobiliariaDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;

@Service
@Transactional(readOnly = false)
public class ImobiliariaService implements IImobiliariaService {

    @Autowired
    IImobiliariaDAO dao;

    public Imobiliaria save(Imobiliaria imobiliaria) {
        dao.save(imobiliaria);

        return imobiliaria;
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Imobiliaria> findById(Long id) {
        return dao.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Imobiliaria> findByNome(String nome) {
        return dao.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public Optional<Imobiliaria> findByCNPJ(String CNPJ) {
        return dao.findByCNPJ(CNPJ);
    }

    @Transactional(readOnly = true)
    public List<Imobiliaria> findAll() {
        return dao.findAll();
    }
}
