package br.ufscar.dsw.imobiliaria.repository;

import br.ufscar.dsw.imobiliaria.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c.id FROM Cliente c WHERE c.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT c.id FROM Cliente c WHERE c.cpf = :cpf")
    boolean existsByCpf(String cpf);
}