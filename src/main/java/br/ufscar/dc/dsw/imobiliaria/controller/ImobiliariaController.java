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
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;
import br.ufscar.dc.dsw.imobiliaria.domain.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;

@Controller
@RequestMapping("/imobiliarias")
public class ImobiliariaController {

    @Autowired
    private IImobiliariaService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/cadastrar")
    public String cadastrar(Imobiliaria imobiliaria) {
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

        if (imobiliaria.getUsuario() != null) {
            Usuario usuario = imobiliaria.getUsuario();
            String pass = usuario.getPassword();

            if (pass != null && !pass.startsWith("$2")) {
                usuario.setPassword(passwordEncoder.encode(pass));
            }

            usuario.setRole(Role.ROLE_IMOBILIARIA);
            usuario.setEnabled(true);

            imobiliaria.setUsuario(usuario);
        } else {
            Usuario usuario = new Usuario();

            usuario.setUsername(imobiliaria.getCNPJ());
            usuario.setPassword(passwordEncoder.encode(imobiliaria.getCNPJ()));
            usuario.setRole(Role.ROLE_IMOBILIARIA);
            usuario.setEnabled(true);

            imobiliaria.setUsuario(usuario);
        }

        service.save(imobiliaria);

        attr.addFlashAttribute("sucess", "Imobiliaria inserida com sucesso.");

        return "redirect:/imobiliarias/listar";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("imobiliaria", service.findById(id).get());

        return "imobiliaria/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid Imobiliaria imobiliaria, BindingResult result, RedirectAttributes attr) {

        if (result.hasErrors()) {
            return "imobiliaria/cadastro";
        }

        if (imobiliaria.getId() != null) {
            java.util.Optional<Imobiliaria> opt = service.findById(imobiliaria.getId());
            if (opt.isPresent()) {
                Imobiliaria db = opt.get();

                if (imobiliaria.getUsuario() != null) {
                    Usuario usuarioForm = imobiliaria.getUsuario();
                    Usuario usuarioDB = db.getUsuario();

                    if (usuarioForm.getUsername() != null && !usuarioForm.getUsername().isBlank()) {
                        usuarioDB.setUsername(usuarioForm.getUsername());
                    }

                    String pass = usuarioForm.getPassword();
                    if (pass != null && !pass.isBlank()) {
                        if (!pass.startsWith("$2")) {
                            usuarioDB.setPassword(passwordEncoder.encode(pass));
                        } else {
                            usuarioDB.setPassword(pass);
                        }
                    }

                    usuarioDB.setRole(Role.ROLE_IMOBILIARIA);
                    usuarioDB.setEnabled(true);

                    imobiliaria.setUsuario(usuarioDB);
                } else {
                    imobiliaria.setUsuario(db.getUsuario());
                }
            }
        }

        service.save(imobiliaria);

        attr.addFlashAttribute("sucess", "Imobiliaria editada com sucesso.");

        return "redirect:/imobiliarias/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        // if (service.imobiliariaTemLivros(id)) {
        // model.addAttribute("fail", "Imobiliaria não excluída. Possui livro(s)
        // vinculado(s).");
        // } else {
        service.deleteById(id);

        model.addAttribute("sucess", "Imobiliaria excluída com sucesso.");
        // }
        return listar(model);
    }
}
