package br.ufscar.dc.dsw.imobiliaria.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.ufscar.dc.dsw.imobiliaria.dao.IUsuarioDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;

public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private IUsuarioDAO usuarioDAO;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioDAO.findByEmail(email);

        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Could not find user: " + email);
        }

        return new UsuarioDetails(usuario.get());
    }
}