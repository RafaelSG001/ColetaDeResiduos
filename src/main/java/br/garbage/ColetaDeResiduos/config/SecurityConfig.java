package br.garbage.ColetaDeResiduos.config;

import br.garbage.ColetaDeResiduos.repository.UsuarioRepository;
import br.garbage.ColetaDeResiduos.security.EstadoContaFilter;
import br.garbage.ColetaDeResiduos.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsuarioDetailsService details,
                                                            PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(details);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider provider,
                                                   UsuarioRepository usuarios) throws Exception {
        http.authenticationProvider(provider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/acesso-negado", "/css/**", "/js/**",
                        "/images/**", "/favicon.ico", "/api/**").permitAll()
                .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMIN")
                // Exclusões: exclusivas do perfil ADMIN
                .requestMatchers(HttpMethod.POST,
                        "/empresas/*/excluir", "/clientes/*/excluir",
                        "/servicos/*/excluir", "/pedidos/*/excluir",
                        "/itens-pedido/*/excluir", "/enderecos/*/excluir",
                        "/tipos-edificacao/*/excluir", "/indicadores/*/excluir").hasRole("ADMIN")
                // Cadastros e edições administrativas/operacionais: ADMIN ou OPERADOR
                .requestMatchers(HttpMethod.POST,
                        "/empresas/salvar", "/clientes/salvar",
                        "/servicos/salvar", "/enderecos/salvar",
                        "/tipos-edificacao/salvar", "/indicadores/salvar").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers(HttpMethod.GET,
                        "/empresas/novo", "/empresas/*/editar",
                        "/clientes/novo", "/clientes/*/editar",
                        "/servicos/novo", "/servicos/*/editar",
                        "/enderecos/novo", "/enderecos/*/editar",
                        "/tipos-edificacao/novo", "/tipos-edificacao/*/editar",
                        "/indicadores/novo", "/indicadores/*/editar").hasAnyRole("ADMIN", "OPERADOR")
                // Abertura e gerenciamento de Pedidos e Itens de Coleta: ADMIN, OPERADOR ou CLIENTE
                .requestMatchers(HttpMethod.POST,
                        "/pedidos/salvar", "/itens-pedido/salvar").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                .requestMatchers(HttpMethod.GET,
                        "/pedidos/novo", "/pedidos/*/editar",
                        "/itens-pedido/novo", "/itens-pedido/*/editar").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                .requestMatchers(HttpMethod.POST,
                        "/empresas/**", "/clientes/**", "/servicos/**",
                        "/pedidos/**", "/itens-pedido/**", "/enderecos/**",
                        "/tipos-edificacao/**", "/indicadores/**").denyAll()
                .requestMatchers(HttpMethod.PUT, "/**").denyAll()
                .requestMatchers(HttpMethod.PATCH, "/**").denyAll()
                .requestMatchers(HttpMethod.DELETE, "/**").denyAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true).permitAll())
            .exceptionHandling(ex -> ex.accessDeniedPage("/acesso-negado"))
            .addFilterBefore(new EstadoContaFilter(usuarios), AuthorizationFilter.class);
        return http.build();
    }
}
