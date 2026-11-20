package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.Cliente;
import br.garbage.ColetaDeResiduos.repository.ClienteRepository;
import br.garbage.ColetaDeResiduos.repository.TipoEdificacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteService {
    private final ClienteRepository repository;
    private final TipoEdificacaoRepository tipoRepo;

    public ClienteService(ClienteRepository repository, TipoEdificacaoRepository tipoRepo) {
        this.repository = repository;
        this.tipoRepo = tipoRepo;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return repository.findAll(PageRequest.of(0, 300, Sort.by("idCliente").ascending())).getContent();
    }

    @Transactional(readOnly = true)
    public Page<Cliente> listarPaginado(String busca, Long idTipo, int pagina, int tamanho) {
        return repository.filtrar(busca, idTipo, PageRequest.of(Math.max(0, pagina), tamanho, Sort.by("idCliente").ascending()));
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado: " + id));
    }

    @Transactional
    public Cliente cadastrar(Cliente dados, Long idTipoEdificacao) {
        preencher(dados, idTipoEdificacao);
        dados.setIdCliente(null);
        return repository.save(dados);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dados, Long idTipoEdificacao) {
        Cliente atual = buscar(id);
        preencher(dados, idTipoEdificacao);
        atual.setRazaoSocial(dados.getRazaoSocial());
        atual.setCnpj(dados.getCnpj());
        atual.setCidade(dados.getCidade());
        atual.setEstadoUf(dados.getEstadoUf().toUpperCase());
        atual.setVolumeContratadoMensalTon(dados.getVolumeContratadoMensalTon());
        atual.setContratoAtivo(Boolean.TRUE.equals(dados.getContratoAtivo()));
        atual.setIndiceSatisfacao(dados.getIndiceSatisfacao());
        atual.setTempoContratoMeses(dados.getTempoContratoMeses());
        atual.setTipoEdificacao(dados.getTipoEdificacao());
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }

    private void preencher(Cliente c, Long idTipoEdificacao) {
        if (c.getRazaoSocial() == null || c.getRazaoSocial().isBlank()) {
            throw new IllegalArgumentException("Razão Social do cliente é obrigatória.");
        }
        Long tId = idTipoEdificacao != null ? idTipoEdificacao :
                (c.getTipoEdificacao() != null ? c.getTipoEdificacao().getIdTipo() : null);
        if (tId == null) throw new IllegalArgumentException("Segmento / Tipo de Edificação é obrigatório.");
        c.setTipoEdificacao(tipoRepo.findById(tId)
                .orElseThrow(() -> new NoSuchElementException("Tipo de Edificação não encontrado.")));
        if (c.getVolumeContratadoMensalTon() == null) c.setVolumeContratadoMensalTon(new BigDecimal("10.00"));
        if (c.getIndiceSatisfacao() == null) c.setIndiceSatisfacao(new BigDecimal("8.5"));
        if (c.getTempoContratoMeses() == null) c.setTempoContratoMeses(12);
        if (c.getContratoAtivo() == null) c.setContratoAtivo(true);
    }
}
