package br.ufscar.dc.dsw.imobiliaria.service.spec;

import java.util.List;
import java.util.Optional;

import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;

public interface IImovelService {

    Optional<Imovel> findById(Long id);

    Optional<Imovel> findByEndereco(String endereco);

    List<Imovel> findByCidadeNome(String nome);

    List<Imovel> findByImobiliariaId(Long imobiliariaId);

    List<Imovel> findAll();

    Imovel save(Imovel imovel);

    void deleteById(Long id);

}