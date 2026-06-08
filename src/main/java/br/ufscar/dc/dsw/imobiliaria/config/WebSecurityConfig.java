package br.ufscar.dc.dsw.imobiliaria.config;

import br.ufscar.dc.dsw.imobiliaria.security.UsuarioDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public UsuarioDetailsServiceImpl userDetailsService() {
        return new UsuarioDetailsServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UsuarioDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index", "/error", "/login", "/login/**", "/acesso-negado",
                                "/imoveis/catalogo", "/imoveis/catalogo/**")
                        .permitAll()
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**", "/image/**",
                                "/webjars/**", "/uploads/**")
                        .permitAll()

                        // AA1: CRUD de clientes e imobiliárias apenas para administrador.
                        .requestMatchers("/clientes/**", "/imobiliarias/**", "/usuarios/**")
                        .hasRole("ADMIN")

                        // AA1: imóveis cadastrados e gerenciados pela imobiliária logada.
                        .requestMatchers(
                                "/imoveis/cadastrar", "/imoveis/salvar", "/imoveis/editar",
                                "/imoveis/editar/**", "/imoveis/excluir/**", "/imoveis/meus")
                        .hasRole("IMOBILIARIA")

                        // Listagem administrativa interna de imóveis. O catálogo público fica liberado acima.
                        .requestMatchers("/imoveis/listar")
                        .hasRole("ADMIN")

                        // AA1: cliente cria, lista e altera apenas suas propostas em aberto.
                        .requestMatchers(
                                "/propostas/cadastrar", "/propostas/salvar", "/propostas/minhas",
                                "/propostas/editar", "/propostas/editar/**", "/propostas/excluir/**")
                        .hasRole("CLIENTE")

                        // AA1: imobiliária avalia propostas dos próprios imóveis.
                        .requestMatchers("/propostas/imobiliaria", "/propostas/avaliar/**", "/propostas/decidir")
                        .hasRole("IMOBILIARIA")

                        .requestMatchers("/propostas/listar")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll())
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acesso-negado"));

        return http.build();
    }
}
