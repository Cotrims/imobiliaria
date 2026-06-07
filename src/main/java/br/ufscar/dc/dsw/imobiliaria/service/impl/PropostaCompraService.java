package br.ufscar.dc.dsw.imobiliaria.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufscar.dc.dsw.imobiliaria.dao.IPropostaCompraDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.PropostaCompra;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IPropostaCompraService;

@Service
@Transactional(readOnly = false)
public class PropostaCompraService implements IPropostaCompraService {

    @Autowired
    IPropostaCompraDAO dao;

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public PropostaCompra save(PropostaCompra proposta) {
        dao.save(proposta);

        return proposta;
    }

    @Transactional(readOnly = true)
    public Optional<PropostaCompra> findById(Long id) {
        return dao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PropostaCompra> findByClienteId(Long clienteId) {
        return dao.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<PropostaCompra> findByImovelId(Long imovelId) {
        return dao.findByImovelId(imovelId);
    }

    @Transactional(readOnly = true)
    public List<PropostaCompra> findByImobiliariaId(Long imobiliariaId) {
        return dao.findByImobiliariaId(imobiliariaId);
    }

    @Transactional(readOnly = true)
    public boolean existsByClienteIdAndImovelIdAndStatus(Long clienteId, Long imovelId,
            br.ufscar.dc.dsw.imobiliaria.domain.StatusProposta status) {
        return dao.existsByClienteIdAndImovelIdAndStatus(clienteId, imovelId, status);
    }

    @Transactional(readOnly = true)
    public List<PropostaCompra> findAll() {
        return dao.findAll();
    }
}
