package br.ufscar.dc.dsw.imobiliaria;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.ufscar.dc.dsw.imobiliaria.dao.ICidadeDAO;
import br.ufscar.dc.dsw.imobiliaria.dao.IClienteDAO;
import br.ufscar.dc.dsw.imobiliaria.dao.IImobiliariaDAO;
import br.ufscar.dc.dsw.imobiliaria.dao.IImovelDAO;
import br.ufscar.dc.dsw.imobiliaria.dao.IPropostaCompraDAO;
import br.ufscar.dc.dsw.imobiliaria.dao.IUsuarioDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class ImobiliariaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImobiliariaApplication.class, args);
	}

	@Bean
	CommandLineRunner executarCrudExemplo(
			IClienteDAO clienteDAO,
			IImobiliariaDAO imobiliariaDAO,
			IImovelDAO imovelDAO,
			ICidadeDAO cidadeDAO,
			IPropostaCompraDAO propostaCompraDAO,
			IUsuarioDAO usuarioDAO,
			BCryptPasswordEncoder encoder,
			javax.sql.DataSource dataSource) {

		return (args) -> {
			propostaCompraDAO.deleteAll();
			imovelDAO.deleteAll();
			clienteDAO.deleteAll();
			imobiliariaDAO.deleteAll();
			cidadeDAO.deleteAll();
			usuarioDAO.deleteAll();

			System.out.println("===== INICIANDO INSERÇÕES NO BANCO DE DADOS =====");

			Usuario admin = new Usuario(
					"admin@hotmail.com",
					encoder.encode("123456"),
					"ROLE_ADMIN",
					true);

			usuarioDAO.save(admin);

			Cliente cliente = new Cliente(
					"julio@hotmail.com",
					encoder.encode("123456"),
					"123.456.789-09",
					"Julio Moya",
					"(16) 99999-9999",
					"Masculino",
					LocalDate.of(1998, 5, 20));

			cliente = clienteDAO.save(cliente);

			Cliente cliente2 = new Cliente(
					"marina@hotmail.com",
					encoder.encode("123456"),
					"234.567.891-73",
					"Marina Pena",
					"(16) 98888-1111",
					"Feminino",
					LocalDate.of(1995, 3, 12));

			cliente2 = clienteDAO.save(cliente2);

			Cliente cliente3 = new Cliente(
					"jader@hotmail.com",
					encoder.encode("123456"),
					"456.789.123-64",
					"Jader Vinícius",
					"(16) 96666-3333",
					"Feminino",
					LocalDate.of(2001, 11, 25));

			cliente2 = clienteDAO.save(cliente3);

			Imobiliaria imobiliaria = new Imobiliaria(
					"cardinali@hotmail.com",
					encoder.encode("123456"),
					"11.222.333/0001-81",
					"Cardinali",
					"Imobiliária especializada em casas e apartamentos residenciais.");

			imobiliaria = imobiliariaDAO.save(imobiliaria);

			Imobiliaria imobiliaria2 = new Imobiliaria(
					"vitashouse@hotmail.com",
					encoder.encode("123456"),
					"22.111.444/0001-37",
					"Cada da Vita",
					"Na casa da Vita vai rolar!");

			imobiliaria2 = imobiliariaDAO.save(imobiliaria2);

			Cidade cidade = new Cidade("São Carlos");
			cidade = cidadeDAO.save(cidade);

			Cidade cidade2 = new Cidade("Ribeirão Preto");
			cidade2 = cidadeDAO.save(cidade2);

			Cidade cidade3 = new Cidade("Araraquara");
			cidade3 = cidadeDAO.save(cidade3);

			Imovel imovel = new Imovel(
					"Rua das Flores, 100",
					cidade,
					"Casa com 3 quartos, garagem e quintal.",
					new BigDecimal("450000.00"),
					imobiliaria);

			imovel = imovelDAO.save(imovel);

			Imovel imovel2 = new Imovel(
					"Avenida Paulista, 200",
					cidade2,
					"Apartamento com 2 quartos, próximo ao metrô.",
					new BigDecimal("350000.00"),
					imobiliaria);

			imovel2 = imovelDAO.save(imovel2);

			Imovel imovel3 = new Imovel(
					"Rua dos Pinheiros, 300",
					cidade3,
					"Casa com 4 quartos, piscina e área gourmet.",
					new BigDecimal("550000.00"),
					imobiliaria2);

			imovel3 = imovelDAO.save(imovel3);

			Imovel imovel4 = new Imovel(
					"Rua XV de Novembro, 850",
					cidade,
					"Apartamento compacto com 1 quarto, sala integrada e vaga coberta.",
					new BigDecimal("220000.00"),
					imobiliaria);

			imovel4 = imovelDAO.save(imovel4);

			Imovel imovel5 = new Imovel(
					"Rua Episcopal, 45",
					cidade,
					"Sobrado com 3 dormitórios, escritório, churrasqueira e duas vagas.",
					new BigDecimal("620000.00"),
					imobiliaria2);

			imovel5 = imovelDAO.save(imovel5);

			Imovel imovel6 = new Imovel(
					"Avenida Portugal, 1200",
					cidade2,
					"Casa térrea com 2 quartos, cozinha planejada e quintal amplo.",
					new BigDecimal("310000.00"),
					imobiliaria);

			imovel6 = imovelDAO.save(imovel6);

			Imovel imovel7 = new Imovel(
					"Rua Itália, 77",
					cidade3,
					"Apartamento com 3 quartos, suíte, sacada e área de lazer completa.",
					new BigDecimal("480000.00"),
					imobiliaria2);

			imovel7 = imovelDAO.save(imovel7);

			Imovel imovel8 = new Imovel(
					"Rua Sete de Setembro, 510",
					cidade,
					"Kitnet mobiliada próxima ao centro, ideal para estudantes.",
					new BigDecimal("160000.00"),
					imobiliaria);

			imovel8 = imovelDAO.save(imovel8);

			Imovel imovel9 = new Imovel(
					"Rua Dona Alexandrina, 920",
					cidade,
					"Casa reformada com 2 quartos, sala ampla e área de serviço.",
					new BigDecimal("390000.00"),
					imobiliaria);

			imovel9 = imovelDAO.save(imovel9);

			Imovel imovel10 = new Imovel(
					"Avenida Presidente Vargas, 300",
					cidade2,
					"Apartamento novo com varanda, elevador e garagem.",
					new BigDecimal("410000.00"),
					imobiliaria2);

			imovel10 = imovelDAO.save(imovel10);

			PropostaCompra proposta = new PropostaCompra(
					new BigDecimal("430000.00"),
					"Entrada de R$ 100.000,00 e restante financiado.",
					cliente,
					imovel);

			propostaCompraDAO.save(proposta);

			PropostaCompra proposta2 = new PropostaCompra(
					new BigDecimal("335000.00"),
					"Pagamento à vista com desconto.",
					cliente2,
					imovel2);

			propostaCompraDAO.save(proposta2);

			PropostaCompra proposta3 = new PropostaCompra(
					new BigDecimal("520000.00"),
					"Entrada de R$ 150.000,00 e financiamento em 240 meses.",
					cliente3,
					imovel3);

			propostaCompraDAO.save(proposta3);

			PropostaCompra proposta4 = new PropostaCompra(
					new BigDecimal("210000.00"),
					"Proposta com entrada de R$ 50.000,00 e restante financiado.",
					cliente2,
					imovel4);

			propostaCompraDAO.save(proposta4);

			PropostaCompra proposta5 = new PropostaCompra(
					new BigDecimal("600000.00"),
					"Pagamento com entrada alta e financiamento direto com o banco.",
					cliente2,
					imovel5);

			propostaCompraDAO.save(proposta5);

			PropostaCompra proposta6 = new PropostaCompra(
					new BigDecimal("300000.00"),
					"Entrada de R$ 80.000,00 e saldo financiado.",
					cliente2,
					imovel6);

			propostaCompraDAO.save(proposta6);

			PropostaCompra proposta7 = new PropostaCompra(
					new BigDecimal("455000.00"),
					"Proposta condicionada à aprovação de financiamento.",
					cliente,
					imovel7);

			propostaCompraDAO.save(proposta7);

			PropostaCompra proposta8 = new PropostaCompra(
					new BigDecimal("150000.00"),
					"Pagamento à vista.",
					cliente,
					imovel8);

			propostaCompraDAO.save(proposta8);

			PropostaCompra proposta9 = new PropostaCompra(
					new BigDecimal("375000.00"),
					"Entrada de R$ 90.000,00 e financiamento do restante.",
					cliente,
					imovel9);

			propostaCompraDAO.save(proposta9);

			PropostaCompra proposta10 = new PropostaCompra(
					new BigDecimal("395000.00"),
					"Proposta com pagamento parcelado mediante aprovação bancária.",
					cliente2,
					imovel10);

			propostaCompraDAO.save(proposta10);

			System.out.println("===== INSERÇÕES FINALIZADAS COM SUCESSO =====");
		};
	}
}