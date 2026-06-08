package br.ufscar.dc.dsw.imobiliaria.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.ufscar.dc.dsw.imobiliaria.dao.IUsuarioDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;

public class UsuarioDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuarioDAO dao;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<Usuario> usuario = dao.getUserByUsername(username);

        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new UsuarioDetails(usuario.get());
    }
}