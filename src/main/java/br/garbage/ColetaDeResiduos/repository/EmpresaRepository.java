package br.garbage.ColetaDeResiduos.repository;

import br.garbage.ColetaDeResiduos.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByNomeEmpresaIgnoreCase(String nomeEmpresa);
}
