package br.garbage.ColetaDeResiduos.security;

import br.garbage.ColetaDeResiduos.model.Usuario;
import br.garbage.ColetaDeResiduos.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class EstadoContaFilter extends OncePerRequestFilter {
    private final UsuarioRepository repository;

    public EstadoContaFilter(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof PrincipalAmbiental principal) {
            Optional<Usuario> atual = repository.findById(principal.getId());
            boolean invalida = atual.isEmpty() || !atual.get().isAtivo()
                    || !atual.get().getUsername().equals(principal.getUsername())
                    || atual.get().getPerfil() != principal.getPerfil();
            if (invalida) {
                SecurityContextHolder.clearContext();
                HttpSession session = request.getSession(false);
                if (session != null) session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login?expired");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
