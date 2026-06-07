package br.ufscar.dc.dsw.imobiliaria.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufscar.dc.dsw.imobiliaria.dao.IClienteDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IClienteService;

@Service
@Transactional(readOnly = false)
public class ClienteService implements IClienteService {

    @Autowired
    IClienteDAO dao;

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public Cliente save(Cliente cliente) {
        dao.save(cliente);

        return cliente;
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> findByNome(String nome) {
        return dao.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> findByUsuarioId(Long usuarioId) {
        return dao.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> findById(Long id) {
        return dao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return dao.findAll();
    }
}
