package br.ufscar.dc.dsw.imobiliaria.service.spec;

import java.util.List;
import java.util.Optional;

import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;

public interface IImobiliariaService {
    Optional<Imobiliaria> findById(Long id);

    Optional<Imobiliaria> findByNome(String nome);

    Optional<Imobiliaria> findByCNPJ(String CNPJ);

    List<Imobiliaria> findAll();

    Imobiliaria save(Imobiliaria imobiliaria);

    void deleteById(Long id);

    Optional<Imobiliaria> findByUsuarioId(Long usuarioId);
}
