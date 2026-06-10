package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;

@SuppressWarnings("unchecked")
public interface IClienteDAO extends CrudRepository<Cliente, Long> {
    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByNome(String nome);

    List<Cliente> findAll();

    @Query("select c from Cliente c where c.CPF = :cpf")
    Optional<Cliente> findByCPF(@Param("cpf") String CPF);

    Cliente save(Cliente cliente);

    void deleteById(Long id);
}
