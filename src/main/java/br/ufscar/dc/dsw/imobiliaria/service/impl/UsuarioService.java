package br.ufscar.dc.dsw.imobiliaria.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufscar.dc.dsw.imobiliaria.dao.IUsuarioDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IUsuarioService;

@Service
@Transactional(readOnly = false)
public class UsuarioService implements IUsuarioService {

    @Autowired
    IUsuarioDAO dao;

    public Usuario save(Usuario usuario) {
        return dao.save(usuario);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return dao.findById(id.longValue());
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return dao.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return dao.findAll();
    }
}