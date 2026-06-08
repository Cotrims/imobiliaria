package br.ufscar.dc.dsw.imobiliaria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;
import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
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

    private Optional<Usuario> usuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return Optional.empty();
        }
        return usuarioService.findByUsername(auth.getName());
    }

    private Optional<Cliente> clienteLogado() {
        return usuarioLogado().flatMap(user -> clienteService.findByUsuarioId(user.getId()));
    }

    private Optional<Imobiliaria> imobiliariaLogada() {
        return usuarioLogado().flatMap(user -> imobiliariaService.findByUsuarioId(user.getId()));
    }

    private boolean pertenceAoCliente(PropostaCompra proposta, Cliente cliente) {
        return proposta != null
                && proposta.getCliente() != null
                && proposta.getCliente().getId() != null
                && proposta.getCliente().getId().equals(cliente.getId());
    }

    private boolean pertenceAImobiliaria(PropostaCompra proposta, Imobiliaria imobiliaria) {
        return proposta != null
                && proposta.getImovel() != null
                && proposta.getImovel().getImobiliaria() != null
                && proposta.getImovel().getImobiliaria().getId() != null
                && proposta.getImovel().getImobiliaria().getId().equals(imobiliaria.getId());
    }

    private void carregarFormulario(ModelMap model) {
        model.addAttribute("imoveis", imovelService.findAll());
    }

    @GetMapping("/cadastrar")
    public String cadastrar(@ModelAttribute("propostaCompra") PropostaCompra proposta,
            @RequestParam(value = "imovelId", required = false) Long imovelId,
            ModelMap model,
            RedirectAttributes attr) {

        if (clienteLogado().isEmpty()) {
            attr.addFlashAttribute("fail", "Usuário autenticado não está vinculado a um cliente.");
            return "redirect:/imoveis/catalogo";
        }

        if (imovelId != null) {
            imovelService.findById(imovelId).ifPresent(proposta::setImovel);
        }
        if (proposta.getImovel() == null) {
            proposta.setImovel(new Imovel());
        }

        carregarFormulario(model);
        return "propostas/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap model) {
        model.addAttribute("propostas", service.findAll());
        return "propostas/lista";
    }

    @GetMapping("/minhas")
    public String listarMeus(ModelMap model) {
        Optional<Cliente> cliente = clienteLogado();
        model.addAttribute("propostas", cliente.map(c -> service.findByClienteId(c.getId())).orElse(List.of()));
        return "propostas/minhas";
    }

    @GetMapping("/imobiliaria")
    public String listarParaImobiliaria(ModelMap model) {
        Optional<Imobiliaria> imob = imobiliariaLogada();
        model.addAttribute("propostas", imob.map(i -> service.findByImobiliariaId(i.getId())).orElse(List.of()));
        return "propostas/lista";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("propostaCompra") PropostaCompra proposta,
            BindingResult result,
            ModelMap model,
            RedirectAttributes attr) {

        Optional<Cliente> clienteOpt = clienteLogado();
        if (clienteOpt.isEmpty()) {
            attr.addFlashAttribute("fail", "Usuário autenticado não está vinculado a um cliente.");
            return "redirect:/imoveis/catalogo";
        }

        validarCamposEditaveis(proposta, result);

        if (proposta.getImovel() == null || proposta.getImovel().getId() == null) {
            result.rejectValue("imovel", "NotNull.propostaCompra.imovel", "Selecione um imóvel.");
        }

        Optional<Imovel> imovelOpt = Optional.empty();
        if (proposta.getImovel() != null && proposta.getImovel().getId() != null) {
            imovelOpt = imovelService.findById(proposta.getImovel().getId());
        }

        if (imovelOpt.isEmpty()) {
            result.rejectValue("imovel", "Invalid.propostaCompra.imovel", "Imóvel inválido.");
        }

        if (result.hasErrors()) {
            if (proposta.getImovel() == null) {
                proposta.setImovel(new Imovel());
            }
            carregarFormulario(model);
            return "propostas/cadastro";
        }

        Long clienteId = clienteOpt.get().getId();
        Long imovelId = imovelOpt.get().getId();

        if (service.existsByClienteIdAndImovelIdAndStatus(clienteId, imovelId, StatusProposta.ABERTO)) {
            attr.addFlashAttribute("fail", "Você já possui uma proposta em aberto para este imóvel.");
            return "redirect:/imoveis/catalogo";
        }

        proposta.setCliente(clienteOpt.get());
        proposta.setImovel(imovelOpt.get());
        proposta.setDataProposta(LocalDateTime.now());
        proposta.setStatus(StatusProposta.ABERTO);

        service.save(proposta);

        attr.addFlashAttribute("sucess", "Proposta de compra inserida com sucesso.");
        return "redirect:/propostas/minhas";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
        Optional<Cliente> clienteOpt = clienteLogado();
        Optional<PropostaCompra> propostaOpt = service.findById(id);

        if (clienteOpt.isEmpty()
                || propostaOpt.isEmpty()
                || !pertenceAoCliente(propostaOpt.get(), clienteOpt.get())
                || propostaOpt.get().getStatus() != StatusProposta.ABERTO) {
            attr.addFlashAttribute("fail", "Proposta não encontrada ou não está mais aberta para edição.");
            return "redirect:/propostas/minhas";
        }

        model.addAttribute("propostaCompra", propostaOpt.get());
        carregarFormulario(model);
        return "propostas/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@ModelAttribute("propostaCompra") PropostaCompra proposta,
            BindingResult result,
            ModelMap model,
            RedirectAttributes attr) {

        Optional<Cliente> clienteOpt = clienteLogado();
        Optional<PropostaCompra> existingOpt = proposta.getId() == null ? Optional.empty() : service.findById(proposta.getId());

        if (clienteOpt.isEmpty()
                || existingOpt.isEmpty()
                || !pertenceAoCliente(existingOpt.get(), clienteOpt.get())
                || existingOpt.get().getStatus() != StatusProposta.ABERTO) {
            attr.addFlashAttribute("fail", "Proposta não encontrada ou não está mais aberta para edição.");
            return "redirect:/propostas/minhas";
        }

        validarCamposEditaveis(proposta, result);

        if (result.hasErrors()) {
            if (proposta.getImovel() == null) {
                proposta.setImovel(existingOpt.get().getImovel());
            }
            proposta.setCliente(existingOpt.get().getCliente());
            proposta.setImovel(existingOpt.get().getImovel());
            proposta.setDataProposta(existingOpt.get().getDataProposta());
            proposta.setStatus(existingOpt.get().getStatus());
            carregarFormulario(model);
            return "propostas/cadastro";
        }

        PropostaCompra existing = existingOpt.get();
        existing.setValorProposta(proposta.getValorProposta());
        existing.setCondicoesPagamento(proposta.getCondicoesPagamento());

        service.save(existing);

        attr.addFlashAttribute("sucess", "Proposta de compra editada com sucesso.");
        return "redirect:/propostas/minhas";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
        Optional<Cliente> clienteOpt = clienteLogado();
        Optional<PropostaCompra> existingOpt = service.findById(id);

        if (clienteOpt.isEmpty()
                || existingOpt.isEmpty()
                || !pertenceAoCliente(existingOpt.get(), clienteOpt.get())
                || existingOpt.get().getStatus() != StatusProposta.ABERTO) {
            attr.addFlashAttribute("fail", "Proposta não encontrada ou não está mais aberta para exclusão.");
            return "redirect:/propostas/minhas";
        }

        service.deleteById(id);
        attr.addFlashAttribute("sucess", "Proposta de compra excluída com sucesso.");
        return "redirect:/propostas/minhas";
    }

    @GetMapping("/avaliar/{id}")
    public String avaliar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
        Optional<Imobiliaria> imob = imobiliariaLogada();
        Optional<PropostaCompra> proposta = service.findById(id);

        if (imob.isEmpty() || proposta.isEmpty() || !pertenceAImobiliaria(proposta.get(), imob.get())) {
            attr.addFlashAttribute("fail", "Proposta não encontrada para a imobiliária logada.");
            return "redirect:/propostas/imobiliaria";
        }

        model.addAttribute("proposta", proposta.get());
        return "propostas/avaliar";
    }

    @PostMapping("/decidir")
    public String decidir(Long id, String acao, String contraValor, String contraCondicoes,
            String meetingLink, String meetingHorario, RedirectAttributes attr) {
        Optional<Imobiliaria> imob = imobiliariaLogada();
        Optional<PropostaCompra> propostaOpt = id == null ? Optional.empty() : service.findById(id);

        if (imob.isEmpty() || propostaOpt.isEmpty() || !pertenceAImobiliaria(propostaOpt.get(), imob.get())) {
            attr.addFlashAttribute("fail", "Proposta não encontrada para a imobiliária logada.");
            return "redirect:/propostas/imobiliaria";
        }

        PropostaCompra proposta = propostaOpt.get();

        if ("NAO_ACEITO".equals(acao)) {
            proposta.setStatus(StatusProposta.NAO_ACEITO);
            service.save(proposta);
            enviarEmailNaoAceito(proposta, contraValor, contraCondicoes);
            attr.addFlashAttribute("sucess", "Proposta marcada como NÃO ACEITO e cliente notificado.");
        } else if ("ACEITO".equals(acao)) {
            if (meetingLink == null || meetingLink.isBlank()) {
                attr.addFlashAttribute("fail", "Informe o link da videoconferência ao aceitar a proposta.");
                return "redirect:/propostas/avaliar/" + id;
            }

            proposta.setStatus(StatusProposta.ACEITO);
            service.save(proposta);
            enviarEmailAceito(proposta, meetingLink, meetingHorario);
            attr.addFlashAttribute("sucess", "Proposta marcada como ACEITO e cliente notificado.");
        } else {
            attr.addFlashAttribute("fail", "Selecione uma decisão válida.");
            return "redirect:/propostas/avaliar/" + id;
        }

        return "redirect:/propostas/imobiliaria";
    }

    private void validarCamposEditaveis(PropostaCompra proposta, BindingResult result) {
        BigDecimal valor = proposta.getValorProposta();
        if (valor == null) {
            result.rejectValue("valorProposta", "NotNull.propostaCompra.valorProposta", "Informe o valor da proposta.");
        } else if (valor.compareTo(BigDecimal.ZERO) < 0) {
            result.rejectValue("valorProposta", "DecimalMin.propostaCompra.valorProposta", "O valor deve ser maior ou igual a zero.");
        }

        if (proposta.getCondicoesPagamento() == null || proposta.getCondicoesPagamento().isBlank()) {
            result.rejectValue("condicoesPagamento", "NotBlank.propostaCompra.condicoesPagamento",
                    "Informe as condições de pagamento.");
        }
    }

    private void enviarEmailNaoAceito(PropostaCompra proposta, String contraValor, String contraCondicoes) {
        String to = proposta.getCliente().getUsuario().getUsername();
        String subject = "Proposta não aceita — " + proposta.getImovel().getEndereco();
        StringBuilder body = new StringBuilder();

        body.append("Olá, ").append(proposta.getCliente().getNome()).append(",\n\n");
        body.append("Sua proposta para o imóvel em ").append(proposta.getImovel().getEndereco());
        body.append(" (").append(proposta.getImovel().getCidade().getNome()).append(") não foi aceita.\n\n");
        body.append("Valor proposto: R$ ").append(proposta.getValorProposta()).append("\n");
        body.append("Condições: ").append(proposta.getCondicoesPagamento()).append("\n\n");

        if (contraValor != null && !contraValor.isBlank()) {
            body.append("--- Contraproposta da imobiliária ---\n");
            body.append("Valor: R$ ").append(contraValor).append("\n");
            if (contraCondicoes != null && !contraCondicoes.isBlank()) {
                body.append("Condições: ").append(contraCondicoes).append("\n");
            }
        }

        body.append("\nAtenciosamente,\n").append(proposta.getImovel().getImobiliaria().getNome());
        emailService.sendEmail(to, subject, body.toString());
    }

    private void enviarEmailAceito(PropostaCompra proposta, String meetingLink, String meetingHorario) {
        String to = proposta.getCliente().getUsuario().getUsername();
        String subject = "Proposta aceita — " + proposta.getImovel().getEndereco();
        StringBuilder body = new StringBuilder();

        body.append("Olá, ").append(proposta.getCliente().getNome()).append(",\n\n");
        body.append("Sua proposta para o imóvel em ").append(proposta.getImovel().getEndereco());
        body.append(" (").append(proposta.getImovel().getCidade().getNome()).append(") foi ACEITA!\n\n");
        body.append("Valor acordado: R$ ").append(proposta.getValorProposta()).append("\n");
        body.append("Condições: ").append(proposta.getCondicoesPagamento()).append("\n\n");
        body.append("--- Reunião de fechamento ---\n");

        if (meetingHorario != null && !meetingHorario.isBlank()) {
            body.append("Horário: ").append(meetingHorario).append("\n");
        }

        body.append("Link: ").append(meetingLink).append("\n\n");
        body.append("Atenciosamente,\n").append(proposta.getImovel().getImobiliaria().getNome());
        emailService.sendEmail(to, subject, body.toString());
    }
}
