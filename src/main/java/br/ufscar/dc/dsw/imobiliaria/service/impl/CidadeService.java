package br.ufscar.dc.dsw.imobiliaria.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufscar.dc.dsw.imobiliaria.dao.ICidadeDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Cidade;
import br.ufscar.dc.dsw.imobiliaria.service.spec.ICidadeService;

@Service
@Transactional(readOnly = false)
public class CidadeService implements ICidadeService {

    @Autowired
    private ICidadeDAO dao;

    @Override
    public Cidade save(Cidade cidade) {
        dao.save(cidade);
        return cidade;
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cidade> findByNome(String nome) {
        return dao.findByNomeIgnoreCase(nome);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cidade> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cidade> findAll() {
        return dao.findAll();
    }
}
