package br.ufscar.dsw.imobiliaria;

import br.ufscar.dsw.imobiliaria.domain.*;
import br.ufscar.dsw.imobiliaria.repository.ClienteRepository;
import br.ufscar.dsw.imobiliaria.repository.ImobiliariaRepository;
import br.ufscar.dsw.imobiliaria.repository.ImovelRepository;
import br.ufscar.dsw.imobiliaria.repository.PropostaCompraRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class ImobiliariaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImobiliariaApplication.class, args);
	}

	@Bean
	CommandLineRunner executarCrudExemplo(
			ClienteRepository clienteRepository,
			ImobiliariaRepository imobiliariaRepository,
			ImovelRepository imovelRepository,
			PropostaCompraRepository propostaCompraRepository) {
		return args -> {
			System.out.println("===== INICIANDO EXEMPLOS DE CRUD =====");

			// R1: Cliente com as informações do documento de requisitos

			/*
			 * CREATE - Criando cliente
			 */
			Cliente cliente = new Cliente(
					"joao@email.com",
					"123456",
					"111.444.777-35",
					"João Silva",
					"(16) 99999-9999",
					"Masculino",
					LocalDate.of(1998, 5, 20));

			cliente = clienteRepository.save(cliente);
			System.out.println("Cliente criado: " + cliente.getNome());

			// R2: Imobiliária e imóvel com as informações do documento de requisitos

			/*
			 * CREATE - Criando imobiliária
			 */
			Imobiliaria imobiliaria = new Imobiliaria(
					"contato@morarbem.com",
					"123456",
					"11.222.333/0001-81",
					"Morar Bem Imóveis",
					"Imobiliária especializada em casas e apartamentos residenciais.");

			imobiliaria = imobiliariaRepository.save(imobiliaria);
			System.out.println("Imobiliária criada: " + imobiliaria.getNome());

			// R3: Cadastro de imóvel de acordo com as informações do documento de
			// requisitos, incluindo o limite de 10 fotos por imóvel

			/*
			 * CREATE - Criando imóvel
			 */
			Imovel imovel = new Imovel(
					"Rua das Flores, 100",
					"São Carlos",
					"Casa com 3 quartos, garagem e quintal.",
					new BigDecimal("450000.00"),
					imobiliaria);

			// R3: Adicionando fotos ao imóvel, respeitando o limite de 10 fotos

			imovel.adicionarFoto(new FotoImovel("/imagens/casa-1.jpg"));
			imovel.adicionarFoto(new FotoImovel("/imagens/casa-2.jpg"));

			imovel = imovelRepository.save(imovel);
			System.out.println("Imóvel criado na cidade: " + imovel.getCidade());

			// R4: Listagem de todos os imóveis disponíveis para venda

			/*
			 * READ - Listando imóveis
			 */
			System.out.println("\nImoveis cadastrados:");
			List<Imovel> imoveis = imovelRepository.findAll();

			for (Imovel i : imoveis) {
				System.out.println(i.getId() + " - " + i.getEndereco() + " - R$ " + i.getValor());
			}

			// R5: Cadastro de proposta de compra para um imóvel específico, associando-a a
			// um cliente e ao imóvel

			/*
			 * CREATE - Criando proposta de compra
			 */
			PropostaCompra proposta = new PropostaCompra(
					new BigDecimal("430000.00"),
					"Entrada de R$ 100.000,00 e restante financiado.",
					cliente,
					imovel);

			proposta = propostaCompraRepository.save(proposta);
			System.out.println("Proposta criada com status: " + proposta.getStatus());

			// R6: Listagem de todos os imóveis de uma imobiliária específica

			/*
			 * READ - Listando imóveis da imobiliária
			 */
			System.out.println("\nImóveis da imobiliária:");
			List<Imovel> imoveisImobiliaria = imovelRepository.findByImobiliaria(imobiliaria);

			for (Imovel i : imoveisImobiliaria) {
				System.out.println(i.getId() + " - " + i.getEndereco() + " - R$ " + i.getValor());
			}

			// R7: Listagem de todas as propostas de compra para um imóvel específico

			/*
			 * READ - Listando propostas de um usuário por status
			 */

			System.out.println("\nPropostas para de um usuário por status: " + cliente.getNome() + " - "
					+ StatusProposta.ACEITO);

			List<PropostaCompra> propostasUsuario = propostaCompraRepository.findByClienteAndStatus(cliente,
					StatusProposta.ACEITO);

			for (PropostaCompra p : propostasUsuario) {
				System.out.println(p.getId() + " - " + p.getValorProposta() + " - " + p.getStatus());
			}

			/*
			 * UPDATE - Atualizando valor do imóvel
			 */
			imovel.setValor(new BigDecimal("440000.00"));
			imovelRepository.save(imovel);

			System.out.println("\nValor do imóvel atualizado para: R$ " + imovel.getValor());

			/*
			 * UPDATE - Atualizando status da proposta
			 */
			proposta.setStatus(StatusProposta.ACEITO);
			propostaCompraRepository.save(proposta);

			System.out.println("Status da proposta atualizado para: " + proposta.getStatus());

			/*
			 * DELETE - Removendo proposta
			 */
			propostaCompraRepository.delete(proposta);
			System.out.println("\nProposta removida.");

			/*
			 * DELETE - Removendo imóvel
			 */
			imovelRepository.delete(imovel);
			System.out.println("Imóvel removido.");

		};
	}
}