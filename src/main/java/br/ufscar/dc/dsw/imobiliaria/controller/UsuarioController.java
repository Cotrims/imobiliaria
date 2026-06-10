package br.ufscar.dc.dsw.imobiliaria.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IUsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService service;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/cadastrar")
    public String cadastrar(Usuario usuario) {
        return "usuario/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap model) {
        model.addAttribute("usuarios", service.findAll());

        return "usuario/lista";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attr) {

        if (result.hasErrors()) {
            return "usuario/cadastro";
        }

        usuario.setEnabled(true);
        usuario.setPassword(encoder.encode(usuario.getPassword()));

        service.save(usuario);

        attr.addFlashAttribute("sucess", "user.create.sucess");

        return "redirect:/usuarios/listar";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("usuario", service.findById(id).get());

        return "usuario/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid Usuario usuario, BindingResult result, String novoPassword, RedirectAttributes attr) {

        if (result.hasErrors()) {
            return "usuario/cadastro";
        }

        if (novoPassword != null && !novoPassword.isBlank()) {
            usuario.setPassword(encoder.encode(novoPassword));
        }

        usuario.setEnabled(true);

        service.save(usuario);

        attr.addFlashAttribute("sucess", "user.edit.sucess");

        return "redirect:/usuarios/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        service.deleteById(id);

        model.addAttribute("sucess", "user.delete.sucess");

        return listar(model);
    }
}