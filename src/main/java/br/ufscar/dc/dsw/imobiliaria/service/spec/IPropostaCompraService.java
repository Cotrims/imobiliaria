package br.ufscar.dc.dsw.imobiliaria.service.spec;

import java.util.List;
import java.util.Optional;

import br.ufscar.dc.dsw.imobiliaria.domain.PropostaCompra;

public interface IPropostaCompraService {
    Optional<PropostaCompra> findById(Long id);

    List<PropostaCompra> findByImovelId(Long imovelId);

    List<PropostaCompra> findByClienteId(Long clienteId);

    List<PropostaCompra> findByImobiliariaId(Long imobiliariaId);

    boolean existsByClienteIdAndImovelIdAndStatus(Long clienteId, Long imovelId,
            br.ufscar.dc.dsw.imobiliaria.domain.StatusProposta status);

    List<PropostaCompra> findAll();

    PropostaCompra save(PropostaCompra propostaCompra);

    void deleteById(Long id);

}
