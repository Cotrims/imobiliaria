package br.ufscar.dsw.imobiliaria.repository;

import br.ufscar.dsw.imobiliaria.domain.Imovel;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IImovelRepository extends JpaRepository<Imovel, Long> {

    @Query("SELECT i FROM Imovel i WHERE UPPER(i.cidade) = UPPER(:cidade)")
    List<Imovel> findByCidadeIgnoreCase(String cidade);

    @Query("SELECT i FROM Imovel i WHERE i.imobiliaria.id = :imobiliariaId")
    List<Imovel> findByImobiliariaId(Long imobiliariaId);
}