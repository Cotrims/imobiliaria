package br.ufscar.dc.dsw.imobiliaria.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.NumberFormat;

@SuppressWarnings("serial")
@Entity
@Table(name = "PropostaCompra")
public class PropostaCompra extends AbstractEntity<Long> {

    @NotNull
    @DecimalMin("0.0")
    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,##0.00")
    @Column(nullable = false)
    private BigDecimal valorProposta;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String condicoesPagamento;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataProposta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProposta status = StatusProposta.ABERTO;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "imovel_id", nullable = false)
    private Imovel imovel;

    public PropostaCompra() {
    }

    public PropostaCompra(BigDecimal valorProposta, String condicoesPagamento,
            Cliente cliente, Imovel imovel) {
        this.valorProposta = valorProposta;
        this.condicoesPagamento = condicoesPagamento;
        this.cliente = cliente;
        this.imovel = imovel;
        this.dataProposta = LocalDateTime.now();
        this.status = StatusProposta.ABERTO;
    }

    public BigDecimal getValorProposta() {
        return valorProposta;
    }

    public String getCondicoesPagamento() {
        return condicoesPagamento;
    }

    public LocalDateTime getDataProposta() {
        return dataProposta;
    }

    public StatusProposta getStatus() {
        return status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setValorProposta(BigDecimal valorProposta) {
        this.valorProposta = valorProposta;
    }

    public void setCondicoesPagamento(String condicoesPagamento) {
        this.condicoesPagamento = condicoesPagamento;
    }

    public void setDataProposta(LocalDateTime dataProposta) {
        this.dataProposta = dataProposta;
    }

    public void setStatus(StatusProposta status) {
        this.status = status;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }
}