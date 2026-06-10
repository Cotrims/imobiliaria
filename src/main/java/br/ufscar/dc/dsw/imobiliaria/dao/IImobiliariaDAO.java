package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;

@SuppressWarnings("unchecked")
public interface IImobiliariaDAO extends JpaRepository<Imobiliaria, Long> {
    Optional<Imobiliaria> findById(Long id);

    Optional<Imobiliaria> findByNome(String nome);

    @Query("select i from Imobiliaria i where i.CNPJ = :cnpj")
    Optional<Imobiliaria> findByCNPJ(@Param("cnpj") String CNPJ);

    List<Imobiliaria> findAll();

    Imobiliaria save(Imobiliaria imobiliaria);

    void deleteById(Long id);
}
