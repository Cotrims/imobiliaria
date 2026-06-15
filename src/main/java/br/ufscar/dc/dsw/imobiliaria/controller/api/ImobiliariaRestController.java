package br.ufscar.dc.dsw.imobiliaria.controller.api;

import java.io.IOException;
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

import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
import br.ufscar.dc.dsw.imobiliaria.domain.Role;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/imobiliarias")
public class ImobiliariaRestController {

    @Autowired
    private IImobiliariaService service;

    @Autowired
    private PasswordEncoder encoder;

    private boolean isJSONValid(String jsonInString) {
        try {
            return new ObjectMapper().readTree(jsonInString) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private void parse(Imobiliaria imobiliaria, JSONObject json) {
        Object id = json.get("id");

        if (id != null) {
            if (id instanceof Integer) {
                imobiliaria.setId(((Integer) id).longValue());
            } else {
                imobiliaria.setId((Long) id);
            }
        }

        if (json.get("email") != null) {
            imobiliaria.setEmail((String) json.get("email"));
        }

        if (json.get("password") != null) {
            imobiliaria.setPassword(encoder.encode((String) json.get("password")));
        }

        if (json.get("CNPJ") != null) {
            imobiliaria.setCNPJ((String) json.get("CNPJ"));
        }

        if (json.get("nome") != null) {
            imobiliaria.setNome((String) json.get("nome"));
        }

        if (json.get("descricao") != null) {
            imobiliaria.setDescricao((String) json.get("descricao"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Imobiliaria>> findAll() {
        List<Imobiliaria> lista = service.findAll();

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Imobiliaria> findById(@PathVariable("id") long id) {
        Optional<Imobiliaria> imobiliaria = service.findById(id);

        if (imobiliaria.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(imobiliaria.get());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Imobiliaria> create(@Valid @RequestBody JSONObject json, BindingResult result) {
        try {
            if (isJSONValid(json.toJSONString())) {
                Imobiliaria imobiliaria = new Imobiliaria();

                System.out.println("JSON" + json);
                parse(imobiliaria, json);

                imobiliaria.setRole(Role.ROLE_IMOBILIARIA);
                imobiliaria.setEnabled(true);

                service.save(imobiliaria);

                return ResponseEntity.ok(imobiliaria);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(null);
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Imobiliaria> update(@PathVariable("id") long id, @RequestBody JSONObject json) {
        try {
            if (isJSONValid(json.toString())) {
                Optional<Imobiliaria> optional = service.findById(id);

                if (optional.isEmpty())
                    return ResponseEntity.notFound().build();

                Imobiliaria imobiliaria = optional.get();

                parse(imobiliaria, json);

                service.save(imobiliaria);

                return ResponseEntity.ok(imobiliaria);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(null);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {

        Optional<Imobiliaria> imobiliaria = service.findById(id);

        if (imobiliaria.isEmpty())
            return ResponseEntity.notFound().build();

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
