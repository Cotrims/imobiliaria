package br.ufscar.dsw.imobiliaria.repository;

import br.ufscar.dsw.imobiliaria.domain.Imobiliaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IImobiliariaRepository extends JpaRepository<Imobiliaria, Long> {

    @Query("SELECT i FROM Imobiliaria i WHERE i.email = :email")
    Optional<Imobiliaria> findByEmail(String email);

    @Query("SELECT i FROM Imobiliaria i WHERE i.cnpj = :cnpj")
    Optional<Imobiliaria> findByCnpj(String cnpj);

    @Query("SELECT i.id FROM Imobiliaria i WHERE i.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT i.id FROM Imobiliaria i WHERE i.cnpj = :cnpj")
    boolean existsByCnpj(String cnpj);
}