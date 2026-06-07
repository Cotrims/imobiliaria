package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;

@SuppressWarnings("unchecked")
public interface IClienteDAO extends CrudRepository<Cliente, Long> {
    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByNome(String nome);

    List<Cliente> findAll();

    Optional<Cliente> findByUsuarioId(Long usuarioId);

    Optional<Cliente> findByCpf(String cpf);

    Cliente save(Cliente cliente);

    void deleteById(Long id);
}
