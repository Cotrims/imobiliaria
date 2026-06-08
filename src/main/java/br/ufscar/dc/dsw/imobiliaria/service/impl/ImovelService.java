package br.ufscar.dc.dsw.imobiliaria.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufscar.dc.dsw.imobiliaria.dao.IImovelDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImovelService;

@Service
@Transactional(readOnly = false)
public class ImovelService implements IImovelService {

    @Autowired
    IImovelDAO dao;

    public Imovel save(Imovel imovel) {
        dao.save(imovel);

        return imovel;
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Imovel> findByEndereco(String endereco) {
        return dao.findByEndereco(endereco);
    }

    @Transactional(readOnly = true)
    public Optional<Imovel> findById(Long id) {
        return dao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Imovel> findByCidadeNome(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            return dao.findAll();
        }

        return dao.findByCidadeNomeContainingIgnoreCase(cidade);
    }

    @Transactional(readOnly = true)
    public List<Imovel> findByImobiliariaId(Long imobiliariaId) {
        return dao.findByImobiliariaId(imobiliariaId);
    }

    @Transactional(readOnly = true)
    public List<Imovel> findAll() {
        return dao.findAll();
    }
}
