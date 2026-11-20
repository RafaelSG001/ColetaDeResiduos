package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.Perfil;
import br.garbage.ColetaDeResiduos.model.Usuario;
import br.garbage.ColetaDeResiduos.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    @PersistenceContext
    private EntityManager entityManager;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Usuario buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    @Transactional
    public void salvar(UUID id, String username, String novaSenha, Perfil perfil,
                       boolean ativo, UUID administradorId) {
        if (username == null || username.isBlank() || username.trim().length() > 80)
            throw new IllegalArgumentException("Informe um username válido de até 80 caracteres.");
        if (perfil == null) throw new IllegalArgumentException("Selecione um perfil.");
        username = username.trim();
        boolean novo = id == null;
        boolean mudarSenha = novaSenha != null && !novaSenha.isEmpty();
        if (novo && !mudarSenha) throw new IllegalArgumentException("Informe a senha do usuário.");
        if ((novo || mudarSenha) && novaSenha.length() < 8)
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");

        Usuario usuario = novo ? new Usuario() : buscar(id);
        if (!novo && usuario.getId().equals(administradorId)) {
            if (!usuario.getUsername().equals(username))
                throw new IllegalArgumentException("Não é permitido alterar o próprio username durante a sessão.");
            if (perfil != Perfil.ADMIN || !ativo)
                throw new IllegalArgumentException("Não é permitido remover o próprio acesso administrativo.");
        }
        if (repository.findByUsername(username)
                .filter(outro -> novo || !outro.getId().equals(usuario.getId())).isPresent())
            throw new IllegalArgumentException("Nome de usuário já cadastrado.");

        if (!novo && usuario.getPerfil() == Perfil.ADMIN && usuario.isAtivo()
                && (perfil != Perfil.ADMIN || !ativo)) {
            List<Usuario> admins = repository.bloquearAdministradoresAtivos(Perfil.ADMIN);
            entityManager.refresh(usuario);
            if (usuario.getPerfil() == Perfil.ADMIN && usuario.isAtivo() && admins.size() <= 1)
                throw new IllegalArgumentException("O sistema deve manter pelo menos um ADMIN ativo.");
        }
        usuario.setUsername(username);
        usuario.setPerfil(perfil);
        usuario.setAtivo(ativo);
        if (novo || mudarSenha) usuario.setSenha(encoder.encode(novaSenha));
        repository.saveAndFlush(usuario);
    }

    @Transactional
    public void excluir(UUID id, UUID administradorId) {
        Usuario usuario = buscar(id);
        if (usuario.getId().equals(administradorId))
            throw new IllegalArgumentException("Não é permitido excluir a própria conta.");
        if (usuario.getPerfil() == Perfil.ADMIN && usuario.isAtivo()) {
            List<Usuario> admins = repository.bloquearAdministradoresAtivos(Perfil.ADMIN);
            entityManager.refresh(usuario);
            if (usuario.getPerfil() == Perfil.ADMIN && usuario.isAtivo() && admins.size() <= 1)
                throw new IllegalArgumentException("Não é permitido excluir o último ADMIN ativo.");
        }
        repository.delete(usuario);
        repository.flush();
    }
}
