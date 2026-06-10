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
import java.nio.file.StandardCopyOption;
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

    private Optional<Imobiliaria> imobiliariaLogada() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return Optional.empty();
        }

        Optional<Usuario> user = usuarioService.findByEmail(auth.getName());
        return user.flatMap(u -> imobiliariaService.findById(u.getId()));
    }

    private boolean pertenceAImobiliariaLogada(Imovel imovel, Imobiliaria imobiliaria) {
        return imovel != null
                && imovel.getImobiliaria() != null
                && imovel.getImobiliaria().getId() != null
                && imovel.getImobiliaria().getId().equals(imobiliaria.getId());
    }

    private void carregarCombos(ModelMap model) {
        model.addAttribute("cidades", cidadeService.findAll());
        imobiliariaLogada().ifPresent(imob -> model.addAttribute("imobiliariaLogada", imob));
    }

    @GetMapping("/cadastrar")
    public String cadastrar(Imovel imovel, ModelMap model, RedirectAttributes attr) {
        Optional<Imobiliaria> imob = imobiliariaLogada();
        if (imob.isEmpty()) {
            attr.addFlashAttribute("fail", "imovel.fail.semImobiliaria");
            return "redirect:/";
        }

        imovel.setImobiliaria(imob.get());
        carregarCombos(model);
        return "imovel/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap model) {
        model.addAttribute("imoveis", service.findAll());
        return "imovel/lista";
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(value = "cidade", required = false) String cidade, ModelMap model) {
        model.addAttribute("imoveis", service.findByCidadeNome(cidade));
        model.addAttribute("cidade", cidade);
        return "imovel/catalogo";
    }

    @GetMapping("/meus")
    public String listarMeus(ModelMap model) {
        Optional<Imobiliaria> imob = imobiliariaLogada();

        if (imob.isEmpty()) {
            model.addAttribute("imoveis", List.of());
            return "imovel/meus";
        }

        model.addAttribute("imoveis", service.findByImobiliariaId(imob.get().getId()));
        return "imovel/meus";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid Imovel imovel,
            BindingResult result,
            @RequestParam(value = "novasFotos", required = false) List<MultipartFile> fotos,
            ModelMap model,
            RedirectAttributes attr) {

        Optional<Imobiliaria> imob = imobiliariaLogada();
        if (imob.isEmpty()) {
            attr.addFlashAttribute("fail", "imovel.fail.semImobiliaria");
            return "redirect:/";
        }

        imovel.setImobiliaria(imob.get());

        int fotoCount = contarFotosEnviadas(fotos);
        if (fotoCount > 10) {
            result.reject("imovel.fail.maxFotos");
            model.addAttribute("fail", "imovel.fail.maxFotos");
        }

        if (result.hasErrors()) {
            carregarCombos(model);
            return "imovel/cadastro";
        }

        Imovel salvo = service.save(imovel);
        salvarFotos(fotos, salvo);

        attr.addFlashAttribute("sucess", "imovel.create.sucess");
        return "redirect:/imoveis/meus";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model, RedirectAttributes attr) {
        Optional<Imobiliaria> imob = imobiliariaLogada();
        Optional<Imovel> opt = service.findById(id);

        if (imob.isEmpty() || opt.isEmpty() || !pertenceAImobiliariaLogada(opt.get(), imob.get())) {
            attr.addFlashAttribute("fail", "imovel.fail.naoEncontrado");
            return "redirect:/imoveis/meus";
        }

        model.addAttribute("imovel", opt.get());
        carregarCombos(model);
        return "imovel/cadastro";
    }

    @PostMapping("/editar")
    public String editar(
            @Valid Imovel imovel,
            BindingResult result,
            @RequestParam(value = "novasFotos", required = false) List<MultipartFile> fotos,
            @RequestParam(value = "fotosParaExcluir", required = false) List<Long> fotosParaExcluir,
            ModelMap model,
            RedirectAttributes attr) {

        Optional<Imobiliaria> imob = imobiliariaLogada();
        Optional<Imovel> existente = imovel.getId() == null ? Optional.empty() : service.findById(imovel.getId());

        if (imob.isEmpty() || existente.isEmpty() || !pertenceAImobiliariaLogada(existente.get(), imob.get())) {
            attr.addFlashAttribute("fail", "imovel.fail.naoEncontrado");
            return "redirect:/imoveis/meus";
        }

        imovel.setImobiliaria(imob.get());

        int removidas = fotosParaExcluir == null ? 0 : fotosParaExcluir.size();
        long existentes = Math.max(0, fotoDAO.findByImovelId(imovel.getId()).size() - removidas);
        int novas = contarFotosEnviadas(fotos);

        if (existentes + novas > 10) {
            result.reject("imovel.fail.maxFotos");
            model.addAttribute("fail", "imovel.fail.maxFotos");
        }

        if (result.hasErrors()) {
            imovel.setFotos(existente.get().getFotos());
            carregarCombos(model);
            return "imovel/cadastro";
        }

        excluirFotosMarcadas(fotosParaExcluir, imovel.getId());

        // Reanexa as fotos remanescentes ao objeto vindo do formulário (que tem a
        // coleção vazia). Sem isso, o merge com orphanRemoval=true removeria do banco
        // todas as fotos que não foram marcadas para exclusão.
        imovel.setFotos(fotoDAO.findByImovelId(imovel.getId()));

        Imovel salvo = service.save(imovel);
        salvarFotos(fotos, salvo);

        attr.addFlashAttribute("sucess", "imovel.edit.sucess");
        return "redirect:/imoveis/meus";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
        Optional<Imobiliaria> imob = imobiliariaLogada();
        Optional<Imovel> opt = service.findById(id);

        if (imob.isEmpty() || opt.isEmpty() || !pertenceAImobiliariaLogada(opt.get(), imob.get())) {
            attr.addFlashAttribute("fail", "imovel.fail.naoEncontrado");
            return "redirect:/imoveis/meus";
        }

        fotoDAO.findByImovelId(id).forEach(foto -> excluirArquivo(foto.getNomeArquivo()));
        service.deleteById(id);

        attr.addFlashAttribute("sucess", "imovel.delete.sucess");
        return "redirect:/imoveis/meus";
    }

    private int contarFotosEnviadas(List<MultipartFile> fotos) {
        return fotos == null ? 0 : (int) fotos.stream().filter(f -> f != null && !f.isEmpty()).count();
    }

    private void excluirFotosMarcadas(List<Long> fotosParaExcluir, Long imovelId) {
        if (fotosParaExcluir == null) {
            return;
        }

        fotosParaExcluir.forEach(fotoId -> fotoDAO.findById(fotoId).ifPresent(foto -> {
            if (foto.getImovel() != null && imovelId.equals(foto.getImovel().getId())) {
                excluirArquivo(foto.getNomeArquivo());
                fotoDAO.deleteById(fotoId);
            }
        }));
    }

    private void salvarFotos(List<MultipartFile> fotos, Imovel imovel) {
        if (fotos == null) {
            return;
        }

        for (MultipartFile foto : fotos) {
            if (foto == null || foto.isEmpty()) {
                continue;
            }
            try {
                Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(dir);

                String ext = "";
                String original = foto.getOriginalFilename();
                if (original != null && original.contains(".")) {
                    ext = original.substring(original.lastIndexOf("."));
                }
                String nome = UUID.randomUUID().toString() + ext;
                Files.copy(foto.getInputStream(), dir.resolve(nome), StandardCopyOption.REPLACE_EXISTING);

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
