package br.ufscar.dc.dsw.imobiliaria;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

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
			PasswordEncoder encoder,
			javax.sql.DataSource dataSource) {
		return (args) -> {
			propostaCompraDAO.deleteAll();
			imovelDAO.deleteAll();
			clienteDAO.deleteAll();
			imobiliariaDAO.deleteAll();
			cidadeDAO.deleteAll();
			usuarioDAO.deleteAll();

			System.out.println("===== INICIANDO INSERÇÕES NO BANCO DE DADOS =====");

			Usuario ua = new Usuario();
			ua.setUsername("admin");
			ua.setPassword(encoder.encode("123"));
			ua.setRole(Role.ROLE_ADMIN);
			ua.setEnabled(true);
			usuarioDAO.save(ua);

			Cliente cliente = new Cliente(
					"111.444.777-35",
					"João Silva",
					"(16) 99999-9999",
					"Masculino",
					LocalDate.of(1998, 5, 20));

			Usuario uc = new Usuario();
			uc.setUsername("joao@email.com");
			uc.setPassword(encoder.encode("123"));
			uc.setRole(Role.ROLE_CLIENTE);
			uc.setEnabled(true);
			cliente.setUsuario(uc);

			cliente = clienteDAO.save(cliente);

			Imobiliaria imobiliaria = new Imobiliaria(
					"11.222.333/0001-81",
					"Morar Bem Imóveis",
					"Imobiliária especializada em casas e apartamentos residenciais.");

			Usuario ui = new Usuario();

			ui.setUsername("contato@morarbem.com");
			ui.setPassword(encoder.encode("123"));
			ui.setRole(Role.ROLE_IMOBILIARIA);
			ui.setEnabled(true);

			imobiliaria.setUsuario(ui);

			imobiliaria = imobiliariaDAO.save(imobiliaria);

			Imobiliaria imobiliaria2 = new Imobiliaria(
					"22.111.444/0001-81",
					"Cardinali",
					"A melhorzinha de São Carlos.");

			Usuario ui2 = new Usuario();

			ui2.setUsername("cardinali@email.com");
			ui2.setPassword(encoder.encode("123"));
			ui2.setRole(Role.ROLE_IMOBILIARIA);
			ui2.setEnabled(true);

			imobiliaria2.setUsuario(ui2);

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

			PropostaCompra proposta = new PropostaCompra(
					new BigDecimal("430000.00"),
					"Entrada de R$ 100.000,00 e restante financiado.",
					cliente,
					imovel);

			proposta = propostaCompraDAO.save(proposta);
		};
	}
}