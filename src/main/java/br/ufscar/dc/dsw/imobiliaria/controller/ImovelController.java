package br.ufscar.dc.dsw.imobiliaria.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImovelService;

@Controller
@RequestMapping("/imoveis")
public class ImovelController {

    @Autowired
    private IImovelService service;
    @Autowired
    private br.ufscar.dc.dsw.imobiliaria.service.spec.ICidadeService cidadeService;
    @Autowired
    private br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService imobiliariaService;
    @Autowired
    private br.ufscar.dc.dsw.imobiliaria.service.spec.IUsuarioService usuarioService;

    @GetMapping("/cadastrar")
    public String cadastrar(Imovel imovel, ModelMap model) {
        // provide list of cidades for select input
        model.addAttribute("cidades", cidadeService.findAll());
        model.addAttribute("imobiliarias", imobiliariaService.findAll());

        return "imovel/cadastro";
    }

    @GetMapping("/listar")
    public String listar(
            @RequestParam(value = "cidade", required = false) String cidade,
            ModelMap model) {
        model.addAttribute("imoveis", service.findByCidadeNome(cidade));
        model.addAttribute("cidade", cidade);

        return "imovel/lista";
    }

    @GetMapping("/meus")
    public String listarMeus(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> user = usuarioService.findByUsername(username);
        if (user.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Imobiliaria> imob = imobiliariaService
                .findByUsuarioId(user.get().getId());

        if (imob.isEmpty()) {
            model.addAttribute("imoveis", List.of());
            return "imovel/lista";
        }

        model.addAttribute("imoveis", service.findByImobiliariaId(imob.get().getId()));
        return "imovel/lista";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid Imovel imovel, BindingResult result, ModelMap model, RedirectAttributes attr) {

        if (result.hasErrors()) {
            model.addAttribute("cidades", cidadeService.findAll());
            model.addAttribute("imobiliarias", imobiliariaService.findAll());

            return "imovel/cadastro";
        }

        if (imovel.getCidade() == null) {
            model.addAttribute("cidades", cidadeService.findAll());
            model.addAttribute("imobiliarias", imobiliariaService.findAll());
            model.addAttribute("fail", "Selecione uma cidade.");

            return "imovel/cadastro";
        }

        if (imovel.getImobiliaria() == null) {
            model.addAttribute("cidades", cidadeService.findAll());
            model.addAttribute("imobiliarias", imobiliariaService.findAll());
            model.addAttribute("fail", "Selecione uma imobiliária.");

            return "imovel/cadastro";
        }

        System.out.println("[DEBUG] Salvando imovel: endereco='" + imovel.getEndereco() + "', cidade="
                + (imovel.getCidade() != null ? imovel.getCidade().getId() : null) + ", imobiliaria="
                + (imovel.getImobiliaria() != null ? imovel.getImobiliaria().getId() : null));

        service.save(imovel);

        attr.addFlashAttribute("sucess", "Imovel inserido com sucesso.");

        return "redirect:/imoveis/listar";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("imovel", service.findById(id).get());
        model.addAttribute("cidades", cidadeService.findAll());
        model.addAttribute("imobiliarias", imobiliariaService.findAll());

        return "imovel/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid Imovel imovel, BindingResult result, ModelMap model, RedirectAttributes attr) {

        if (result.hasErrors()) {
            model.addAttribute("cidades", cidadeService.findAll());
            model.addAttribute("imobiliarias", imobiliariaService.findAll());

            return "imovel/cadastro";
        }

        if (imovel.getCidade() == null) {
            model.addAttribute("cidades", cidadeService.findAll());
            model.addAttribute("imobiliarias", imobiliariaService.findAll());
            model.addAttribute("fail", "Selecione uma cidade.");

            return "imovel/cadastro";
        }

        if (imovel.getImobiliaria() == null) {
            model.addAttribute("cidades", cidadeService.findAll());
            model.addAttribute("imobiliarias", imobiliariaService.findAll());
            model.addAttribute("fail", "Selecione uma imobiliária.");

            return "imovel/cadastro";
        }

        service.save(imovel);

        attr.addFlashAttribute("sucess", "Imovel editado com sucesso.");

        return "redirect:/imoveis/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        // if (service.imobiliariaTemLivros(id)) {
        // model.addAttribute("fail", "Imobiliaria não excluída. Possui livro(s)
        // vinculado(s).");
        // } else {
        service.deleteById(id);

        model.addAttribute("sucess", "Imovel excluído com sucesso.");
        // }

        return listar(null, model);
    }
}
