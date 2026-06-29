package br.ufscar.dc.dsw.imobiliaria.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufscar.dc.dsw.imobiliaria.domain.Imovel;
import br.ufscar.dc.dsw.imobiliaria.service.spec.ICidadeService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImobiliariaService;
import br.ufscar.dc.dsw.imobiliaria.service.spec.IImovelService;

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
