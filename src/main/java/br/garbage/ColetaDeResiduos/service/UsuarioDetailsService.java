package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.Usuario;
import br.garbage.ColetaDeResiduos.repository.UsuarioRepository;
import br.garbage.ColetaDeResiduos.security.PrincipalAmbiental;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new PrincipalAmbiental(u.getId(), u.getUsername(), u.getSenha(), u.getPerfil(), u.isAtivo());
    }
}
