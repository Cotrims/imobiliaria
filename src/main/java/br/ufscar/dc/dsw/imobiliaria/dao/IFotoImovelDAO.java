package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import br.ufscar.dc.dsw.imobiliaria.domain.FotoImovel;

public interface IFotoImovelDAO extends CrudRepository<FotoImovel, Long> {
    List<FotoImovel> findByImovelId(Long imovelId);
    void deleteByImovelId(Long imovelId);
}
