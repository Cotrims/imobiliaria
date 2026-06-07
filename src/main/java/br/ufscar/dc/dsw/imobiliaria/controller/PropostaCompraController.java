package br.ufscar.dc.dsw.imobiliaria.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;
import br.ufscar.dc.dsw.imobiliaria.domain.PropostaCompra;
import br.ufscar.dc.dsw.imobiliaria.domain.StatusProposta;
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;
import br.ufscar.dc.dsw.imobiliaria.service.impl.EmailService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IClienteService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImovelService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IPropostaCompraService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IUsuarioService;

@Controller
@RequestMapping("/propostas")
public class PropostaCompraController {

    @Autowired
    private IPropostaCompraService service;

    @Autowired
    private IImovelService imovelService;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private IImobiliariaService imobiliariaService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/cadastrar")
    public String cadastrar(PropostaCompra proposta, ModelMap model, Long imovelId) {
        model.addAttribute("imoveis", imovelService.findAll());
        model.addAttribute("clientes", clienteService.findAll());

        if (imovelId != null) {
            Optional<Imovel> imovelOpt = imovelService.findById(imovelId);
            imovelOpt.ifPresent(proposta::setImovel);
        }

        return "propostas/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap model) {
        model.addAttribute("propostas", service.findAll());

        return "propostas/lista";
    }

    @GetMapping("/meus")
    public String listarMeus(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> user = usuarioService.findByUsername(username);
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        Optional<br.ufscar.dc.dsw.imobiliaria.domain.Cliente> cliente = clienteService
                .findByUsuarioId(user.get().getId());
        if (cliente.isEmpty()) {
            model.addAttribute("propostas", List.of());
            return "propostas/lista";
        }
        model.addAttribute("propostas", service.findByClienteId(cliente.get().getId()));
        return "propostas/lista";
    }

    @GetMapping("/imobiliaria")
    public String listarParaImobiliaria(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> user = usuarioService.findByUsername(username);
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        Optional<br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria> imob = imobiliariaService
                .findByUsuarioId(user.get().getId());
        if (imob.isEmpty()) {
            model.addAttribute("propostas", List.of());
            return "propostas/lista";
        }
        model.addAttribute("propostas", service.findByImobiliariaId(imob.get().getId()));
        return "propostas/lista";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid PropostaCompra proposta, BindingResult result, RedirectAttributes attr) {

        if (result.hasErrors()) {
            return "propostas/cadastro";
        }

        // determine authenticated client
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> user = usuarioService.findByUsername(username);
        if (user.isEmpty()) {
            return "redirect:/login";
        }

        Optional<br.ufscar.dc.dsw.imobiliaria.domain.Cliente> clienteOpt = clienteService
                .findByUsuarioId(user.get().getId());
        if (clienteOpt.isEmpty()) {
            attr.addFlashAttribute("fail", "Usuário autenticado não é um cliente.");
            return "redirect:/imoveis/listar";
        }

        // fetch imovel
        if (proposta.getImovel() == null || proposta.getImovel().getId() == null) {
            attr.addFlashAttribute("fail", "Selecione um imóvel.");
            return "redirect:/imoveis/listar";
        }

        Optional<Imovel> imovelOpt = imovelService.findById(proposta.getImovel().getId());
        if (imovelOpt.isEmpty()) {
            attr.addFlashAttribute("fail", "Imóvel inválido.");
            return "redirect:/imoveis/listar";
        }

        Long clienteId = clienteOpt.get().getId();
        Long imovelId = imovelOpt.get().getId();

        // enforce only one open proposal per client per imovel
        if (service.existsByClienteIdAndImovelIdAndStatus(clienteId, imovelId, StatusProposta.ABERTO)) {
            attr.addFlashAttribute("fail", "Você já possui uma proposta em aberto para este imóvel.");
            return "redirect:/imoveis/listar";
        }

        proposta.setCliente(clienteOpt.get());
        proposta.setImovel(imovelOpt.get());
        proposta.setDataProposta(LocalDateTime.now());
        proposta.setStatus(StatusProposta.ABERTO);

        service.save(proposta);

        attr.addFlashAttribute("sucess", "Proposta de compra inserida com sucesso.");

        return "redirect:/propostas/meus";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        Optional<PropostaCompra> propostaOpt = service.findById(id);
        if (propostaOpt.isEmpty()) {
            model.addAttribute("fail", "Proposta não encontrada.");
            return "redirect:/propostas/listar";
        }

        PropostaCompra proposta = propostaOpt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> user = usuarioService.findByUsername(username);
        boolean allowed = false;
        if (user.isPresent()) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                allowed = true;
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                Optional<br.ufscar.dc.dsw.imobiliaria.domain.Cliente> clienteOpt = clienteService
                        .findByUsuarioId(user.get().getId());
                if (clienteOpt.isPresent() && clienteOpt.get().getId().equals(proposta.getCliente().getId())
                        && proposta.getStatus() == StatusProposta.ABERTO) {
                    allowed = true;
                }
            }
        }

        if (!allowed) {
            model.addAttribute("fail", "Operação não autorizada.");
            return "redirect:/propostas/listar";
        }

        model.addAttribute("proposta", proposta);
        model.addAttribute("imoveis", imovelService.findAll());
        model.addAttribute("clientes", clienteService.findAll());
        return "propostas/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid PropostaCompra proposta, BindingResult result, RedirectAttributes attr) {

        if (result.hasErrors()) {
            return "propostas/cadastro";
        }

        Optional<PropostaCompra> existingOpt = service.findById(proposta.getId());
        if (existingOpt.isEmpty()) {
            attr.addFlashAttribute("fail", "Proposta não encontrada.");
            return "redirect:/propostas/listar";
        }

        PropostaCompra existing = existingOpt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> user = usuarioService.findByUsername(username);
        boolean allowed = false;
        if (user.isPresent()) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                allowed = true;
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                Optional<br.ufscar.dc.dsw.imobiliaria.domain.Cliente> clienteOpt = clienteService
                        .findByUsuarioId(user.get().getId());
                if (clienteOpt.isPresent() && clienteOpt.get().getId().equals(existing.getCliente().getId())
                        && existing.getStatus() == StatusProposta.ABERTO) {
                    allowed = true;
                }
            }
        }

        if (!allowed) {
            attr.addFlashAttribute("fail", "Operação não autorizada.");
            return "redirect:/propostas/listar";
        }

        // preserve cliente, imovel, data, status
        existing.setValorProposta(proposta.getValorProposta());
        existing.setCondicoesPagamento(proposta.getCondicoesPagamento());

        service.save(existing);

        attr.addFlashAttribute("sucess", "Proposta de compra editada com sucesso.");

        return "redirect:/propostas/meus";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        // if (service.imobiliariaTemLivros(id)) {
        // model.addAttribute("fail", "Imobiliaria não excluída. Possui livro(s)
        // vinculado(s).");
        // } else {
        Optional<PropostaCompra> existingOpt = service.findById(id);
        if (existingOpt.isEmpty()) {
            model.addAttribute("fail", "Proposta não encontrada.");
            return listar(model);
        }

        PropostaCompra existing = existingOpt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> user = usuarioService.findByUsername(username);
        boolean allowed = false;
        if (user.isPresent()) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                allowed = true;
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                Optional<br.ufscar.dc.dsw.imobiliaria.domain.Cliente> clienteOpt = clienteService
                        .findByUsuarioId(user.get().getId());
                if (clienteOpt.isPresent() && clienteOpt.get().getId().equals(existing.getCliente().getId())
                        && existing.getStatus() == StatusProposta.ABERTO) {
                    allowed = true;
                }
            }
        }

        if (!allowed) {
            model.addAttribute("fail", "Operação não autorizada.");
            return listar(model);
        }

        service.deleteById(id);

        model.addAttribute("sucess", "Proposta de compra excluída com sucesso.");
        // }
        return listar(model);
    }

    @GetMapping("/avaliar/{id}")
    public String avaliar(@PathVariable("id") Long id, ModelMap model) {
        Optional<PropostaCompra> proposta = service.findById(id);
        if (proposta.isEmpty()) {
            model.addAttribute("fail", "Proposta não encontrada.");
            return "redirect:/propostas/imobiliaria";
        }
        model.addAttribute("proposta", proposta.get());
        return "propostas/avaliar";
    }

    @PostMapping("/decidir")
    public String decidir(Long id, String acao, String contraValor, String contraCondicoes, String meetingLink,
            RedirectAttributes attr) {
        Optional<PropostaCompra> propostaOpt = service.findById(id);
        if (propostaOpt.isEmpty()) {
            attr.addFlashAttribute("fail", "Proposta não encontrada.");
            return "redirect:/propostas/imobiliaria";
        }
        PropostaCompra proposta = propostaOpt.get();

        if ("NAO_ACEITO".equals(acao)) {
            proposta.setStatus(StatusProposta.NAO_ACEITO);
            service.save(proposta);
            // send email to cliente with optional counterproposal
            String to = proposta.getCliente().getUsuario().getUsername();
            String subject = "Sua proposta foi recusada";
            StringBuilder body = new StringBuilder();
            body.append("Sua proposta para o imóvel " + proposta.getImovel().getEndereco() + " foi recusada.\n");
            if (contraValor != null && !contraValor.isBlank()) {
                body.append("Contraproposta: valor=" + contraValor + ", condicoes=" + contraCondicoes + "\n");
            }
            emailService.sendEmail(to, subject, body.toString());
            attr.addFlashAttribute("sucess", "Proposta marcada como NÃO ACEITO e cliente notificado.");
        } else if ("ACEITO".equals(acao)) {
            proposta.setStatus(StatusProposta.ACEITO);
            service.save(proposta);
            String to = proposta.getCliente().getUsuario().getUsername();
            String subject = "Sua proposta foi aceita";
            StringBuilder body = new StringBuilder();
            body.append("Sua proposta para o imóvel " + proposta.getImovel().getEndereco() + " foi aceita.\n");
            if (meetingLink != null && !meetingLink.isBlank()) {
                body.append("Link da videoconferência: " + meetingLink + "\n");
            }
            emailService.sendEmail(to, subject, body.toString());
            attr.addFlashAttribute("sucess", "Proposta marcada como ACEITO e cliente notificado.");
        }

        return "redirect:/propostas/imobiliaria";
    }
}
