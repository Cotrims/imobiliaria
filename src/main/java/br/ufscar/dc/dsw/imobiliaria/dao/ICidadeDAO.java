package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.ufscar.dc.dsw.imobiliaria.domain.Cidade;

@SuppressWarnings("unchecked")
public interface ICidadeDAO extends CrudRepository<Cidade, Long> {
    Optional<Cidade> findById(Long id);

    Optional<Cidade> findByNomeIgnoreCase(String nome);

    List<Cidade> findAll();

    Cidade save(Cidade cidade);

    void deleteById(Long id);
}
