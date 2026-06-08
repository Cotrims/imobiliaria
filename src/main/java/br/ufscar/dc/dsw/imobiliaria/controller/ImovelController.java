package br.ufscar.dc.dsw.imobiliaria.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ufscar.dc.dsw.imobiliaria.dao.IFotoImovelDAO;
import br.ufscar.dc.dsw.imobiliaria.domain.FotoImovel;
import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;
import br.ufscar.dc.dsw.imobiliaria.service.spec.ICidadeService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImovelService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IUsuarioService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/imoveis")
public class ImovelController {

    @Autowired
    private IImovelService service;

    @Autowired
    private ICidadeService cidadeService;

    @Autowired
    private IImobiliariaService imobiliariaService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IFotoImovelDAO fotoDAO;

    @Value("${app.upload.dir:${user.home}/imoveis-uploads}")
    private String uploadDir;

    // ─── helpers ─────────────────────────────────────────────────────────────

    /** Returns the Imobiliaria for the currently authenticated user, or empty. */
    private Optional<Imobiliaria> imobiliariaLogada() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return Optional.empty();
        Optional<Usuario> user = usuarioService.findByUsername(auth.getName());
        return user.flatMap(u -> imobiliariaService.findByUsuarioId(u.getId()));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping("/cadastrar")
    public String cadastrar(Imovel imovel, ModelMap model) {
        model.addAttribute("cidades", cidadeService.findAll());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(auth)) {
            model.addAttribute("imobiliarias", imobiliariaService.findAll());
        } else {
            imobiliariaLogada().ifPresent(imob -> {
                System.out.println(imob.getUsuario().getUsername());
                model.addAttribute("imobiliariaLogada", imob);
                imovel.setImobiliaria(imob);
            });
        }

        return "imovel/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap model) {
        model.addAttribute("imoveis", service.findAll());

        return "imovel/lista";
    }

    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(value = "cidade", required = false) String cidade,
            ModelMap model) {

        model.addAttribute("imoveis", service.findByCidadeNome(cidade));
        model.addAttribute("cidade", cidade);

        return "imovel/catalogo";
    }

    @GetMapping("/meus")
    public String listarMeus(ModelMap model) {
        Optional<Imobiliaria> imob = imobiliariaLogada();

        if (imob.isEmpty()) {
            model.addAttribute("imoveis", List.of());

            return "imovel/lista";
        }

        model.addAttribute("imoveis", service.findByImobiliariaId(imob.get().getId()));

        return "imovel/meus";
    }

    // ─── salvar ───────────────────────────────────────────────────────────────

    @PostMapping("/salvar")
    public String salvar(
            @Valid Imovel imovel,
            BindingResult result,
            @RequestParam(value = "fotos", required = false) List<MultipartFile> fotos,
            ModelMap model,
            RedirectAttributes attr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth)) {
            imobiliariaLogada().ifPresent(imovel::setImobiliaria);
        }

        if (imovel.getImobiliaria() == null || imovel.getImobiliaria().getId() == null) {
            result.rejectValue("imobiliaria", "NotNull.imovel.imobiliaria");
        }

        if (result.hasErrors()) {
            model.addAttribute("cidades", cidadeService.findAll());

            if (isAdmin(auth))
                model.addAttribute("imobiliarias", imobiliariaService.findAll());

            return "imovel/cadastro";
        }

        int fotoCount = (fotos == null) ? 0 : (int) fotos.stream().filter(f -> !f.isEmpty()).count();

        if (fotoCount > 10) {
            model.addAttribute("fail", "Máximo de 10 fotos permitido.");
            model.addAttribute("cidades", cidadeService.findAll());

            if (isAdmin(auth))
                model.addAttribute("imobiliarias", imobiliariaService.findAll());

            return "imovel/cadastro";
        }

        Imovel salvo = service.save(imovel);

        salvarFotos(fotos, salvo);

        attr.addFlashAttribute("sucess", "Imóvel inserido com sucesso.");

        return isAdmin(auth) ? "redirect:/imoveis/listar" : "redirect:/imoveis/meus";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        Optional<Imovel> opt = service.findById(id);

        if (opt.isEmpty())
            return "redirect:/imoveis/listar";

        model.addAttribute("imovel", opt.get());
        model.addAttribute("cidades", cidadeService.findAll());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(auth)) {
            model.addAttribute("imobiliarias", imobiliariaService.findAll());
        } else {
            imobiliariaLogada().ifPresent(imob -> model.addAttribute("imobiliariaLogada", imob));
        }

        return "imovel/cadastro";
    }

    @PostMapping("/editar")
    public String editar(
            @Valid Imovel imovel,
            BindingResult result,
            @RequestParam(value = "fotos", required = false) List<MultipartFile> fotos,
            @RequestParam(value = "fotosParaExcluir", required = false) List<Long> fotosParaExcluir,
            ModelMap model,
            RedirectAttributes attr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth)) {
            imobiliariaLogada().ifPresent(imovel::setImobiliaria);
        }

        if (imovel.getImobiliaria() == null || imovel.getImobiliaria().getId() == null) {
            result.rejectValue("imobiliaria", "NotNull.imovel.imobiliaria");
        }

        if (result.hasErrors()) {
            model.addAttribute("cidades", cidadeService.findAll());

            if (isAdmin(auth))
                model.addAttribute("imobiliarias", imobiliariaService.findAll());

            return "imovel/cadastro";
        }

        // Remove fotos marcadas para exclusão
        if (fotosParaExcluir != null) {
            fotosParaExcluir.forEach(fotoId -> fotoDAO.findById(fotoId).ifPresent(foto -> {
                excluirArquivo(foto.getNomeArquivo());
                fotoDAO.deleteById(fotoId);
            }));
        }

        long existentes = fotoDAO.findByImovelId(imovel.getId()).size()
                - (fotosParaExcluir != null ? fotosParaExcluir.size() : 0);

        int novas = (fotos == null) ? 0 : (int) fotos.stream().filter(f -> !f.isEmpty()).count();

        if (existentes + novas > 10) {
            model.addAttribute("fail", "Máximo de 10 fotos permitido.");

            model.addAttribute("cidades", cidadeService.findAll());

            if (isAdmin(auth))
                model.addAttribute("imobiliarias", imobiliariaService.findAll());

            return "imovel/cadastro";
        }

        Imovel salvo = service.save(imovel);

        salvarFotos(fotos, salvo);

        attr.addFlashAttribute("sucess", "Imóvel editado com sucesso.");

        return isAdmin(auth) ? "redirect:/imoveis/listar" : "redirect:/imoveis/meus";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        Optional<Imovel> opt = service.findById(id);

        opt.ifPresent(imovel -> {
            fotoDAO.findByImovelId(id).forEach(foto -> excluirArquivo(foto.getNomeArquivo()));
        });

        service.deleteById(id);

        model.addAttribute("sucess", "Imóvel excluído com sucesso.");

        return listar(model);
    }

    private void salvarFotos(List<MultipartFile> fotos, Imovel imovel) {
        if (fotos == null)
            return;

        for (MultipartFile foto : fotos) {
            if (foto == null || foto.isEmpty())
                continue;
            try {
                Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(dir);

                String ext = "";
                String original = foto.getOriginalFilename();
                if (original != null && original.contains(".")) {
                    ext = original.substring(original.lastIndexOf("."));
                }
                String nome = UUID.randomUUID().toString() + ext;
                Files.copy(foto.getInputStream(), dir.resolve(nome));

                FotoImovel fotoImovel = new FotoImovel(imovel, nome);
                fotoDAO.save(fotoImovel);
            } catch (IOException e) {
                System.err.println("Erro ao salvar foto: " + e.getMessage());
            }
        }
    }

    private void excluirArquivo(String nomeArquivo) {
        try {
            Path file = Paths.get(uploadDir).resolve(nomeArquivo).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("Erro ao excluir arquivo: " + e.getMessage());
        }
    }
}
