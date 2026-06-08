package br.ufscar.dc.dsw.imobiliaria.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import br.ufscar.dc.dsw.imobiliaria.domain.PropostaCompra;

@SuppressWarnings("unchecked")
public interface IPropostaCompraDAO extends CrudRepository<PropostaCompra, Long> {
    Optional<PropostaCompra> findById(Long id);

    List<PropostaCompra> findByImovelId(Long imovelId);

    List<PropostaCompra> findByClienteId(Long clienteId);

    boolean existsByClienteIdAndImovelIdAndStatus(Long clienteId, Long imovelId,
            br.ufscar.dc.dsw.imobiliaria.domain.StatusProposta status);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM PropostaCompra p WHERE p.imovel.imobiliaria.id = :imobiliariaId")
    List<PropostaCompra> findByImobiliariaId(
            @org.springframework.data.repository.query.Param("imobiliariaId") Long imobiliariaId);

    List<PropostaCompra> findAll();

    PropostaCompra save(PropostaCompra propostaCompra);

    void deleteById(Long id);
}
