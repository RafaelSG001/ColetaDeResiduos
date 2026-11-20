package br.garbage.ColetaDeResiduos.repository;

import br.garbage.ColetaDeResiduos.model.IndicadorMensal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface IndicadorMensalRepository extends JpaRepository<IndicadorMensal, Long> {
    Page<IndicadorMensal> findByEmpresa_IdEmpresa(Long idEmpresa, Pageable pageable);

    @Query("SELECT COALESCE(SUM(i.emissaoCo2Evitaton), 0) FROM IndicadorMensal i")
    BigDecimal somarCo2EvitadoTotal();
}
