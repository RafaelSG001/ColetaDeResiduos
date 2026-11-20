package br.garbage.ColetaDeResiduos.repository;

import br.garbage.ColetaDeResiduos.model.ItemPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    @Query("SELECT COALESCE(SUM(i.pesoToneladas), 0) FROM ItemPedido i")
    BigDecimal somarPesoTotalColetado();

    @Query("SELECT i FROM ItemPedido i WHERE " +
           "(:idServico IS NULL OR i.servico.idServico = :idServico) AND " +
           "(:destino IS NULL OR :destino = '' OR UPPER(i.destinoFinal) = UPPER(:destino))")
    Page<ItemPedido> filtrar(@Param("idServico") Long idServico,
                             @Param("destino") String destino,
                             Pageable pageable);
}
