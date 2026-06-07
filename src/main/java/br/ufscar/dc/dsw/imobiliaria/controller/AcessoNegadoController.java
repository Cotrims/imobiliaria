package br.ufscar.dc.dsw.imobiliaria.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcessoNegadoController {

    @GetMapping("/acesso-negado")
    public String acessoNegado(HttpServletRequest request) {
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.FORBIDDEN.value());
        return "forward:/error";
    }
}