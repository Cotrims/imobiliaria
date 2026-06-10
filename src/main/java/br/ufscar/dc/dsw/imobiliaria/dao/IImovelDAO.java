package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;

@SuppressWarnings("unchecked")
public interface IImovelDAO extends JpaRepository<Imovel, Long> {
    Optional<Imovel> findById(Long id);

    Optional<Imovel> findByEndereco(String endereco);

    List<Imovel> findByCidadeNomeContainingIgnoreCase(String nome);

    List<Imovel> findAll();

    List<Imovel> findByImobiliariaId(Long imobiliariaId);

    Imovel save(Imovel imovel);

    void deleteById(Long id);
}
