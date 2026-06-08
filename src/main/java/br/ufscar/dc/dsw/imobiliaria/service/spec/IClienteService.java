package br.ufscar.dc.dsw.imobiliaria.service.spec;

import java.util.List;
import java.util.Optional;

import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;

public interface IClienteService {
    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByUsuarioId(Long usuarioId);

    Optional<Cliente> findByNome(String nome);

    List<Cliente> findAll();

    Cliente save(Cliente cliente);

    void deleteById(Long id);
}
