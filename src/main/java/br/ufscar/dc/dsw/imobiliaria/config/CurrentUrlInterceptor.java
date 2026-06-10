package br.ufscar.dc.dsw.imobiliaria.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Disponibiliza para as views as URLs da página atual com cada idioma
 * (urlPt/urlEn/urlJp), para que a troca de idioma permaneça na mesma página
 * em vez de redirecionar para a home.
 */
public class CurrentUrlInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) {

        if (modelAndView == null) {
            return;
        }

        String viewName = modelAndView.getViewName();
        if (viewName != null && viewName.startsWith("redirect:")) {
            return;
        }

        StringBuilder query = new StringBuilder();
        request.getParameterMap().forEach((key, values) -> {
            if ("lang".equals(key)) {
                return;
            }
            for (String value : values) {
                query.append(query.length() == 0 ? "?" : "&")
                        .append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        });

        String base = request.getRequestURI() + query;
        String separator = query.length() == 0 ? "?" : "&";

        modelAndView.addObject("urlPt", base + separator + "lang=pt");
        modelAndView.addObject("urlEn", base + separator + "lang=en");
        modelAndView.addObject("urlJp", base + separator + "lang=jp");
    }
}
