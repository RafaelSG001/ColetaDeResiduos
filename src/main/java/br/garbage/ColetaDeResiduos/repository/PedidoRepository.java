package br.garbage.ColetaDeResiduos.repository;

import br.garbage.ColetaDeResiduos.model.Pedido;
import br.garbage.ColetaDeResiduos.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    long countByStatus(StatusPedido status);

    @Query("SELECT COALESCE(SUM(p.receitaReciclagemTotal), 0) FROM Pedido p")
    BigDecimal somarReceitaReciclagemTotal();

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:idCliente IS NULL OR p.cliente.idCliente = :idCliente) AND " +
           "(:idEmpresa IS NULL OR p.empresa.idEmpresa = :idEmpresa) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Pedido> filtrar(@Param("idCliente") Long idCliente,
                         @Param("idEmpresa") Long idEmpresa,
                         @Param("status") StatusPedido status,
                         Pageable pageable);
}
