package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;

@SuppressWarnings("unchecked")
public interface IUsuarioDAO extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAll();

    Usuario save(Usuario usuario);

    void deleteById(Long id);
}