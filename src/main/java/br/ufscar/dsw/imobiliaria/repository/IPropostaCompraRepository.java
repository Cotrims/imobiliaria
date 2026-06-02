package br.ufscar.dsw.imobiliaria.repository;

import br.ufscar.dsw.imobiliaria.domain.PropostaCompra;
import br.ufscar.dsw.imobiliaria.domain.StatusProposta;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IPropostaCompraRepository extends JpaRepository<PropostaCompra, Long> {

    @Query("SELECT p FROM PropostaCompra p WHERE p.cliente.id = :clienteId")
    List<PropostaCompra> findByClienteId(Long clienteId);

    @Query("SELECT p FROM PropostaCompra p WHERE p.cliente.id = :clienteId AND p.status = :status")
    List<PropostaCompra> findByClienteIdAndStatus(Long clienteId, StatusProposta status);

    @Query("SELECT p FROM PropostaCompra p INNER JOIN p.imovel i WHERE i.imobiliaria.id = :imobiliariaId")
    List<PropostaCompra> findByImovelImobiliariaId(Long imobiliariaId);

    @Query("SELECT p.id FROM PropostaCompra p WHERE p.cliente.id = :clienteId AND p.imovel.id = :imovelId AND p.status = :status")
    boolean existsByClienteIdAndImovelIdAndStatus(
            Long clienteId,
            Long imovelId,
            StatusProposta status);
}