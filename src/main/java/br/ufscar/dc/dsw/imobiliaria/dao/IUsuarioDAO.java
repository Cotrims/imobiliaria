package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;

@SuppressWarnings("unchecked")
public interface IUsuarioDAO extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findById(Long id);

    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    Optional<Usuario> getUserByUsername(@Param("username") String username);

    List<Usuario> findAll();

    Usuario save(Usuario usuario);

    void deleteById(Long id);
}