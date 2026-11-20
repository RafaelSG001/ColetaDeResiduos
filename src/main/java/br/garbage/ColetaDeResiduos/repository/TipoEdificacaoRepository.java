package br.garbage.ColetaDeResiduos.repository;

import br.garbage.ColetaDeResiduos.model.TipoEdificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoEdificacaoRepository extends JpaRepository<TipoEdificacao, Long> {
    Optional<TipoEdificacao> findByNomeSegmentoIgnoreCase(String nomeSegmento);
}
