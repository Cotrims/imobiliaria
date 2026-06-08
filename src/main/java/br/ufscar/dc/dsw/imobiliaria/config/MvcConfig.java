package br.ufscar.dc.dsw.imobiliaria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import br.ufscar.dc.dsw.imobiliaria.conversor.BigDecimalConversor;
import br.ufscar.dc.dsw.imobiliaria.conversor.CidadeConversor;
import br.ufscar.dc.dsw.imobiliaria.conversor.ImobiliariaConversor;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@ComponentScan(basePackages = "br.ufscar.dc.dsw.imobiliaria.config")
public class MvcConfig implements WebMvcConfigurer {
    @Value("${app.upload.dir:${user.home}/imoveis-uploads}")
    private String uploadDir;

    @Autowired
    private ImobiliariaConversor imobiliariaConversor;

    @Autowired
    private CidadeConversor cidadeConversor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }

        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login").setViewName("login");
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.forLanguageTag("pt-BR"));
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new BigDecimalConversor());
        registry.addConverter(imobiliariaConversor);
        registry.addConverter(cidadeConversor);
    }
}
