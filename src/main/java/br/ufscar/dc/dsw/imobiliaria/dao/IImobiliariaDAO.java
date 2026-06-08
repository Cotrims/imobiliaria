package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;

@SuppressWarnings("unchecked")
public interface IImobiliariaDAO extends CrudRepository<Imobiliaria, Long> {
    Optional<Imobiliaria> findById(Long id);

    Optional<Imobiliaria> findByNome(String nome);

    Optional<Imobiliaria> findByCNPJ(String CNPJ);

    List<Imobiliaria> findAll();

    Imobiliaria save(Imobiliaria imobiliaria);

    void deleteById(Long id);

    Optional<Imobiliaria> findByUsuarioId(Long usuarioId);
}
