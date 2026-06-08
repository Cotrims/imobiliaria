package br.ufscar.dc.dsw.imobiliaria.service.spec;

import java.util.List;
import java.util.Optional;

import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;

public interface IUsuarioService {

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findAll();

    Usuario save(Usuario usuario);

    void deleteById(Long id);
}