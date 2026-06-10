package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import br.ufscar.dc.dsw.imobiliaria.domain.FotoImovel;

public interface IFotoImovelDAO extends JpaRepository<FotoImovel, Long> {
    List<FotoImovel> findByImovelId(Long imovelId);

    void deleteByImovelId(Long imovelId);
}
