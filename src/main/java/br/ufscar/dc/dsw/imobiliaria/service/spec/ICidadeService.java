package br.ufscar.dc.dsw.imobiliaria.service.spec;

import java.util.List;
import java.util.Optional;

import br.ufscar.dc.dsw.imobiliaria.domain.Cidade;

public interface ICidadeService {

    Optional<Cidade> findById(Long id);

    Optional<Cidade> findByNome(String nome);

    List<Cidade> findAll();

    Cidade save(Cidade cidade);

    void deleteById(Long id);
}
