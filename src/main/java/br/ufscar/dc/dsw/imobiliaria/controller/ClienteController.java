package br.ufscar.dc.dsw.imobiliaria.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;
import br.ufscar.dc.dsw.imobiliaria.domain.Role;

import org.springframework.security.crypto.password.PasswordEncoder;

import br.ufscar.dc.dsw.imobiliaria.service.spec.IClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private IClienteService service;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/cadastrar")
    public String cadastrar(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setEnabled(true);
        }
        return "cliente/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap model) {
        model.addAttribute("clientes", service.findAll());

        return "cliente/lista";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result,
            RedirectAttributes attr) {

        if (result.hasErrors()) {
            return "cliente/cadastro";
        }

        cliente.setRole(Role.ROLE_CLIENTE);
        cliente.setPassword(encoder.encode(cliente.getPassword()));

        service.save(cliente);

        attr.addFlashAttribute("sucess", "client.create.sucess");

        return "redirect:/clientes/listar";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("cliente", service.findById(id).get());

        return "cliente/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid Cliente cliente, BindingResult result, String novoPassword, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "cliente/cadastro";
        }

        if (novoPassword != null && !novoPassword.isBlank()) {
            cliente.setPassword(encoder.encode(novoPassword));
        }

        cliente.setRole(Role.ROLE_CLIENTE);

        service.save(cliente);

        attr.addFlashAttribute("sucess", "client.edit.sucess");

        return "redirect:/clientes/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        service.deleteById(id);

        model.addAttribute("sucess", "client.delete.sucess");

        return listar(model);
    }
}
