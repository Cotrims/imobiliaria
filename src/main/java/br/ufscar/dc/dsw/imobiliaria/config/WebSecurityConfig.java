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
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                                .authenticationProvider(authenticationProvider)

                                .authorizeHttpRequests(auth -> auth
                                                // páginas públicas
                                                .requestMatchers("/", "/index", "/error", "/login", "/login/**",
                                                                "/acesso-negado")
                                                .permitAll()

                                                // arquivos estáticos
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/image/**",
                                                                "/webjars/**", "/uploads/**")
                                                .permitAll()

                                                // API da AA-2 sem autenticação
                                                .requestMatchers("/api/**").permitAll()

                                                // listar os imóveis e detalhes sem autenticação
                                                .requestMatchers("/imoveis/catalogo").permitAll()

                                                // imobiliaria
                                                .requestMatchers("/imoveis/cadastrar", "/imoveis/editar/**",
                                                                "/imoveis/salvar/**",
                                                                "/imoveis/excluir/**",
                                                                "/imoveis/meus")
                                                .hasRole("IMOBILIARIA")

                                                // administrador
                                                .requestMatchers("/clientes/**").hasRole("ADMIN")
                                                .requestMatchers("/imobiliarias/**").hasRole("ADMIN")
                                                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                                                .requestMatchers("/imoveis/**").hasRole("ADMIN")

                                                // cliente
                                                .requestMatchers("/propostas/minhas", "/propostas/nova/**",
                                                                "/propostas/salvar")
                                                .hasRole("CLIENTE")

                                                // imobiliária
                                                .requestMatchers("/propostas/analisar/**", "/imoveis/meus")
                                                .hasRole("IMOBILIARIA")

                                                // qualquer outra rota exige login
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