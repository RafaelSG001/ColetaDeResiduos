package br.garbage.ColetaDeResiduos.repository;

import br.garbage.ColetaDeResiduos.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    long countByContratoAtivoTrue();

    @Query("SELECT c FROM Cliente c WHERE " +
           "(:busca IS NULL OR :busca = '' OR UPPER(c.razaoSocial) LIKE UPPER(CONCAT('%', :busca, '%')) OR UPPER(c.cidade) LIKE UPPER(CONCAT('%', :busca, '%'))) AND " +
           "(:idTipo IS NULL OR c.tipoEdificacao.idTipo = :idTipo)")
    Page<Cliente> filtrar(@Param("busca") String busca, @Param("idTipo") Long idTipo, Pageable pageable);
}
