package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.Endereco;
import br.garbage.ColetaDeResiduos.repository.EnderecoRepository;
import br.garbage.ColetaDeResiduos.repository.TipoEdificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EnderecoService {
    private final EnderecoRepository repository;
    private final TipoEdificacaoRepository tipoRepo;

    public EnderecoService(EnderecoRepository repository, TipoEdificacaoRepository tipoRepo) {
        this.repository = repository;
        this.tipoRepo = tipoRepo;
    }

    @Transactional(readOnly = true)
    public List<Endereco> listar() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Endereco buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Endereço não encontrado: " + id));
    }

    @Transactional
    public Endereco cadastrar(Endereco dados, Long idTipoEdificacao) {
        preencher(dados, idTipoEdificacao);
        dados.setIdEndereco(null);
        return repository.save(dados);
    }

    @Transactional
    public Endereco atualizar(Long id, Endereco dados, Long idTipoEdificacao) {
        Endereco atual = buscar(id);
        preencher(dados, idTipoEdificacao);
        atual.setLogradouro(dados.getLogradouro());
        atual.setNumero(dados.getNumero());
        atual.setCep(dados.getCep());
        atual.setCidade(dados.getCidade());
        atual.setEstadoUf(dados.getEstadoUf().toUpperCase());
        atual.setTipoEdificacao(dados.getTipoEdificacao());
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }

    private void preencher(Endereco d, Long idTipoEdificacao) {
        if (d.getCidade() == null || d.getCidade().isBlank() || d.getEstadoUf() == null || d.getEstadoUf().isBlank()) {
            throw new IllegalArgumentException("Cidade e Estado (UF) são obrigatórios.");
        }
        Long tId = idTipoEdificacao != null ? idTipoEdificacao :
                (d.getTipoEdificacao() != null ? d.getTipoEdificacao().getIdTipo() : null);
        if (tId == null) throw new IllegalArgumentException("Tipo de Edificação é obrigatório.");
        d.setTipoEdificacao(tipoRepo.findById(tId)
                .orElseThrow(() -> new NoSuchElementException("Tipo de Edificação não encontrado.")));
    }
}
