package br.garbage.ColetaDeResiduos.security;

import br.garbage.ColetaDeResiduos.model.Perfil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class PrincipalAmbiental implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final String username;
    private final String senha;
    private final Perfil perfil;
    private final boolean ativo;

    public PrincipalAmbiental(UUID id, String username, String senha, Perfil perfil, boolean ativo) {
        this.id = id;
        this.username = username;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public UUID getId() { return id; }
    public Perfil getPerfil() { return perfil; }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return senha; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public boolean isEnabled() { return ativo; }
}
