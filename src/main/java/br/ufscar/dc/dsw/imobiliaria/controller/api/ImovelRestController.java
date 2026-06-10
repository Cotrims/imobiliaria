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

        imovel.setEndereco((String) json.get("endereco"));
        imovel.setDescricao((String) json.get("descricao"));

        Object valor = json.get("valor");
        if (valor != null) {
            imovel.setValor(new BigDecimal(valor.toString()));
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

    // GET /api/imoveis -- lista de imóveis (à venda)
    @GetMapping
    public ResponseEntity<List<Imovel>> lista() {
        List<Imovel> lista = service.findAll();

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }

    // GET /api/imoveis/{id} -- imóvel (à venda) de id = {id}
    @GetMapping(path = "/{id}")
    public ResponseEntity<Imovel> lista(@PathVariable("id") Long id) {
        Optional<Imovel> imovel = service.findById(id);

        if (imovel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(imovel.get());
    }

    // GET /api/imoveis/cidades/{nome} -- imóveis (à venda) da cidade de nome =
    // {nome}
    @GetMapping(path = "/cidades/{nome}")
    public ResponseEntity<List<Imovel>> listaPorCidade(@PathVariable("nome") String nome) {
        List<Imovel> lista = service.findByCidadeNome(nome);

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }

    // GET /api/imoveis/imobiliarias/{id} -- imóveis (à venda) da imobiliária de id
    // = {id}
    @GetMapping(path = "/imobiliarias/{id}")
    public ResponseEntity<List<Imovel>> listaPorImobiliaria(@PathVariable("id") Long id) {
        List<Imovel> lista = service.findByImobiliariaId(id);

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Imovel> cria(@Valid @RequestBody JSONObject json, BindingResult result) {
        try {
            if (isJSONValid(json.toJSONString())) {
                Imovel imovel = new Imovel();

                parse(imovel, json);

                service.save(imovel);

                return ResponseEntity.ok(imovel);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(null);
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Imovel> atualiza(@PathVariable("id") Long id, @RequestBody JSONObject json) {
        try {
            if (isJSONValid(json.toString())) {
                Optional<Imovel> optional = service.findById(id);

                if (optional.isEmpty())
                    return ResponseEntity.notFound().build();

                Imovel imovel = optional.get();

                parse(imovel, json);

                service.save(imovel);

                return ResponseEntity.ok(imovel);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(null);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") Long id) {

        Optional<Imovel> imovel = service.findById(id);

        if (imovel.isEmpty())
            return ResponseEntity.notFound().build();

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
