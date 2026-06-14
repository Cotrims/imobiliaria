package br.ufscar.dc.dsw.imobiliaria.controller.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufscar.dc.dsw.imobiliaria.domain.Cliente;
import br.ufscar.dc.dsw.imobiliaria.domain.Role;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IClienteService;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    @Autowired
    private IClienteService service;

    @Autowired
    private PasswordEncoder encoder;

    private boolean isJSONValid(String jsonInString) {
        try {
            return new ObjectMapper().readTree(jsonInString) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private void parse(Cliente cliente, JSONObject json) {
        Object id = json.get("id");

        if (id != null) {
            if (id instanceof Integer) {
                cliente.setId(((Integer) id).longValue());
            } else {
                cliente.setId((Long) id);
            }
        }

        if (json.get("email") != null) {
            cliente.setEmail((String) json.get("email"));
        }

        if (json.get("password") != null) {
            cliente.setPassword(encoder.encode((String) json.get("password")));
        }

        if (json.get("nome") != null) {
            cliente.setNome((String) json.get("nome"));
        }

        if (json.get("CPF") != null) {
            cliente.setCPF((String) json.get("CPF"));
        }

        if (json.get("sexo") != null) {
            cliente.setSexo((String) json.get("sexo"));
        }

        if (json.get("telefone") != null) {
            cliente.setTelefone((String) json.get("telefone"));
        }

        Object dataNascimento = json.get("dataNascimento");

        if (dataNascimento != null) {
            cliente.setDataNascimento(LocalDate.parse(dataNascimento.toString()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> findAll() {
        List<Cliente> lista = service.findAll();

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable("id") long id) {
        Optional<Cliente> cliente = service.findById(id);

        if (cliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cliente.get());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Cliente> crate(@Valid @RequestBody JSONObject json, BindingResult result) {
        try {
            if (isJSONValid(json.toJSONString())) {
                System.out.println("EH VALIDO E ENTROU");

                Cliente cliente = new Cliente();

                parse(cliente, json);

                cliente.setRole(Role.ROLE_CLIENTE);
                cliente.setEnabled(true);

                service.save(cliente);

                return ResponseEntity.ok(cliente);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(null);
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Cliente> update(@PathVariable("id") long id, @RequestBody JSONObject json) {
        try {
            if (isJSONValid(json.toString())) {
                Cliente cliente = service.findById(id).get();

                if (cliente == null)
                    return ResponseEntity.notFound().build();

                parse(cliente, json);

                service.save(cliente);

                return ResponseEntity.ok(cliente);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(null);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {

        Optional<Cliente> cliente = service.findById(id);

        if (cliente.isEmpty())
            return ResponseEntity.notFound().build();

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
