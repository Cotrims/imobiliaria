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

import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
import br.ufscar.dc.dsw.imobiliaria.domain.Role;

import org.springframework.security.crypto.password.PasswordEncoder;

import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;

@Controller
@RequestMapping("/imobiliarias")
public class ImobiliariaController {

    @Autowired
    private IImobiliariaService service;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/cadastrar")
    public String cadastrar(Imobiliaria imobiliaria) {
        if (imobiliaria.getId() == null) {
            imobiliaria.setEnabled(true);
        }
        return "imobiliaria/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap model) {
        model.addAttribute("imobiliarias", service.findAll());

        return "imobiliaria/lista";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid Imobiliaria imobiliaria, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "imobiliaria/cadastro";
        }

        imobiliaria.setRole(Role.ROLE_IMOBILIARIA);
        imobiliaria.setPassword(encoder.encode(imobiliaria.getPassword()));

        service.save(imobiliaria);

        attr.addFlashAttribute("sucess", "agency.create.sucess");

        return "redirect:/imobiliarias/listar";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("imobiliaria", service.findById(id).get());

        return "imobiliaria/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid Imobiliaria imobiliaria, BindingResult result, String novoPassword,
            RedirectAttributes attr) {

        if (result.hasErrors()) {
            return "imobiliaria/cadastro";
        }

        if (novoPassword != null && !novoPassword.isBlank()) {
            imobiliaria.setPassword(encoder.encode(novoPassword));
        }

        imobiliaria.setRole(Role.ROLE_IMOBILIARIA);

        service.save(imobiliaria);

        attr.addFlashAttribute("sucess", "agency.edit.sucess");

        return "redirect:/imobiliarias/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        service.deleteById(id);

        model.addAttribute("sucess", "agency.delete.sucess");

        return listar(model);
    }
}
