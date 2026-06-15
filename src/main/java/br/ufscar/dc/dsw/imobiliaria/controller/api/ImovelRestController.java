package br.ufscar.dc.dsw.imobiliaria.controller.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

import br.ufscar.dc.dsw.imobiliaria.domain.Cidade;
import br.ufscar.dc.dsw.imobiliaria.domain.Imobiliaria;
import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;
import br.ufscar.dc.dsw.imobiliaria.service.spec.ICidadeService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImovelService;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/imoveis")
public class ImovelRestController {

    @Autowired
    private IImovelService service;

    @Autowired
    private ICidadeService cidadeService;

    @Autowired
    private IImobiliariaService imobiliariaService;

    private boolean isJSONValid(String jsonInString) {
        try {
            return new ObjectMapper().readTree(jsonInString) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private void parse(Imovel imovel, JSONObject json) {
        Object id = json.get("id");

        if (id != null) {
            if (id instanceof Integer) {
                imovel.setId(((Integer) id).longValue());
            } else {
                imovel.setId((Long) id);
            }
        }

        if (json.get("endereco") != null) {
            imovel.setEndereco((String) json.get("endereco"));
        }

        if (json.get("descricao") != null) {
            imovel.setDescricao((String) json.get("descricao"));
        }

        if (json.get("valor") != null) {
            imovel.setValor(new BigDecimal(json.get("valor").toString()));
        }

        Object cidadeId = json.get("cidadeId");
        if (cidadeId != null) {
            Optional<Cidade> cidade = cidadeService.findById(Long.valueOf(cidadeId.toString()));
            cidade.ifPresent(imovel::setCidade);
        }

        Object imobiliariaId = json.get("imobiliariaId");
        if (imobiliariaId != null) {
            Optional<Imobiliaria> imobiliaria = imobiliariaService.findById(Long.valueOf(imobiliariaId.toString()));
            imobiliaria.ifPresent(imovel::setImobiliaria);
        }
    }

    @GetMapping
    public ResponseEntity<List<Imovel>> findAll() {
        List<Imovel> lista = service.findAll();

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Imovel> findById(@PathVariable("id") Long id) {
        Optional<Imovel> imovel = service.findById(id);

        if (imovel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(imovel.get());
    }

    @GetMapping(path = "/cidades/{nome}")
    public ResponseEntity<List<Imovel>> findByCidade(@PathVariable("nome") String nome) {
        List<Imovel> lista = service.findByCidadeNome(nome);

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping(path = "/imobiliarias/{id}")
    public ResponseEntity<List<Imovel>> findByAgency(@PathVariable("id") Long id) {
        List<Imovel> lista = service.findByImobiliariaId(id);

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }
}
