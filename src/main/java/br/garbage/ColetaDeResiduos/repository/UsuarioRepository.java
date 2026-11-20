package br.garbage.ColetaDeResiduos.repository;

import br.garbage.ColetaDeResiduos.model.Perfil;
import br.garbage.ColetaDeResiduos.model.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.perfil = :perfil and u.ativo = true order by u.id")
    List<Usuario> bloquearAdministradoresAtivos(@Param("perfil") Perfil perfil);
}
