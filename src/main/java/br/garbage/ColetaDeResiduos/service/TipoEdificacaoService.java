package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.TipoEdificacao;
import br.garbage.ColetaDeResiduos.repository.TipoEdificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TipoEdificacaoService {
    private final TipoEdificacaoRepository repository;

    public TipoEdificacaoService(TipoEdificacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoEdificacao> listar() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public TipoEdificacao buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de Edificação não encontrado: " + id));
    }

    @Transactional
    public TipoEdificacao cadastrar(TipoEdificacao dados) {
        validar(dados);
        dados.setIdTipo(null);
        return repository.save(dados);
    }

    @Transactional
    public TipoEdificacao atualizar(Long id, TipoEdificacao dados) {
        validar(dados);
        TipoEdificacao atual = buscar(id);
        atual.setNomeSegmento(dados.getNomeSegmento());
        atual.setDescricaoNorma(dados.getDescricaoNorma());
        atual.setExigeLicencaEspecial(Boolean.TRUE.equals(dados.getExigeLicencaEspecial()));
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }

    private void validar(TipoEdificacao d) {
        if (d == null || d.getNomeSegmento() == null || d.getNomeSegmento().isBlank()) {
            throw new IllegalArgumentException("Nome do segmento da edificação é obrigatório.");
        }
        if (d.getDescricaoNorma() == null || d.getDescricaoNorma().isBlank()) {
            d.setDescricaoNorma("Norma Técnica Ambiental ABNT NBR 10004");
        }
        if (d.getExigeLicencaEspecial() == null) d.setExigeLicencaEspecial(false);
    }
}
