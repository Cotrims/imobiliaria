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
import br.ufscar.dc.dsw.imobiliaria.domain.Usuario;
import br.ufscar.dc.dsw.imobiliaria.domain.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private IClienteService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/cadastrar")
    public String cadastrar(Cliente cliente) {
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

        if (cliente.getUsuario() != null) {
            Usuario usuario = cliente.getUsuario();
            String pass = usuario.getPassword();

            if (pass != null && !pass.isBlank() && !pass.startsWith("$2")) {
                usuario.setPassword(passwordEncoder.encode(pass));
            }

            usuario.setRole(Role.ROLE_CLIENTE);
            usuario.setEnabled(true);

            cliente.setUsuario(usuario);
        } else {
            // Caso o formulário não tenha enviado dados de usuário, cria um usuário padrão
            // usando o CPF como nome de usuário e senha inicial (codificada).
            Usuario usuario = new Usuario();
            String defaultUsername = cliente.getCpf();
            String defaultPassword = cliente.getCpf();

            usuario.setUsername(defaultUsername);
            usuario.setPassword(passwordEncoder.encode(defaultPassword));
            usuario.setRole(Role.ROLE_CLIENTE);
            usuario.setEnabled(true);

            cliente.setUsuario(usuario);
        }

        service.save(cliente);

        attr.addFlashAttribute("sucess", "Cliente inserido com sucesso.");

        return "redirect:/clientes/listar";
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("cliente", service.findById(id).get());

        return "cliente/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "cliente/cadastro";
        }

        // Preserva dados do usuário existente quando o formulário não enviar senha
        if (cliente.getId() != null) {
            java.util.Optional<Cliente> opt = service.findById(cliente.getId());
            if (opt.isPresent()) {
                Cliente clienteDB = opt.get();

                if (cliente.getUsuario() != null) {
                    Usuario usuarioForm = cliente.getUsuario();
                    Usuario usuarioDB = clienteDB.getUsuario();

                    // Atualiza username apenas se fornecido
                    if (usuarioForm.getUsername() != null && !usuarioForm.getUsername().isBlank()) {
                        usuarioDB.setUsername(usuarioForm.getUsername());
                    }

                    // Atualiza senha apenas se fornecida
                    String pass = usuarioForm.getPassword();
                    if (pass != null && !pass.isBlank()) {
                        if (!pass.startsWith("$2")) {
                            usuarioDB.setPassword(passwordEncoder.encode(pass));
                        } else {
                            usuarioDB.setPassword(pass);
                        }
                    }

                    usuarioDB.setRole(Role.ROLE_CLIENTE);
                    usuarioDB.setEnabled(true);

                    cliente.setUsuario(usuarioDB);
                } else {
                    // Mantém o usuário atual se o formulário não enviou dados
                    cliente.setUsuario(clienteDB.getUsuario());
                }
            }
        }

        service.save(cliente);

        attr.addFlashAttribute("sucess", "Cliente editado com sucesso.");

        return "redirect:/clientes/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, ModelMap model) {
        // if (service.imobiliariaTemLivros(id)) {
        // model.addAttribute("fail", "Imobiliaria não excluída. Possui livro(s)
        // vinculado(s).");
        // } else {
        service.deleteById(id);

        model.addAttribute("sucess", "Cliente excluído com sucesso.");
        // }
        return listar(model);
    }
}
