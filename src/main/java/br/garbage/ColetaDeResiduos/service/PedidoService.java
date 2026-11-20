package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.Pedido;
import br.garbage.ColetaDeResiduos.model.StatusPedido;
import br.garbage.ColetaDeResiduos.repository.ClienteRepository;
import br.garbage.ColetaDeResiduos.repository.EmpresaRepository;
import br.garbage.ColetaDeResiduos.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {
    private final PedidoRepository repository;
    private final ClienteRepository clienteRepo;
    private final EmpresaRepository empresaRepo;

    public PedidoService(PedidoRepository repository, ClienteRepository clienteRepo, EmpresaRepository empresaRepo) {
        this.repository = repository;
        this.clienteRepo = clienteRepo;
        this.empresaRepo = empresaRepo;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarRecentes() {
        return repository.findAll(PageRequest.of(0, 200, Sort.by("idPedido").descending())).getContent();
    }

    @Transactional(readOnly = true)
    public Page<Pedido> filtrarPaginado(Long idCliente, Long idEmpresa, StatusPedido status, int pagina, int tamanho) {
        return repository.filtrar(idCliente, idEmpresa, status,
                PageRequest.of(Math.max(0, pagina), tamanho, Sort.by("idPedido").ascending()));
    }

    @Transactional(readOnly = true)
    public Pedido buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado: " + id));
    }

    @Transactional
    public Pedido cadastrar(Pedido dados, Long idCliente, Long idEmpresa) {
        vincular(dados, idCliente, idEmpresa);
        dados.setIdPedido(null);
        if (dados.getDataSolicitacao() == null) dados.setDataSolicitacao(LocalDate.now());
        if (dados.getDataColetaExecucao() == null) dados.setDataColetaExecucao(dados.getDataSolicitacao());
        if (dados.getStatus() == null) dados.setStatus(StatusPedido.PENDENTE);
        if (dados.getCodigoProtocolo() == null || dados.getCodigoProtocolo().isBlank()) {
            dados.setCodigoProtocolo("PED-" + System.currentTimeMillis());
        }
        if (dados.getStatus() == StatusPedido.CANCELADO) {
            dados.setReceitaReciclagemTotal(BigDecimal.ZERO);
        }
        return repository.save(dados);
    }

    @Transactional
    public Pedido atualizar(Long id, Pedido dados, Long idCliente, Long idEmpresa) {
        Pedido atual = buscar(id);
        if (dados.getStatus() != null && atual.getStatus() != dados.getStatus()) {
            atual.avancarStatus(dados.getStatus());
        }
        vincular(dados, idCliente, idEmpresa);
        atual.setCliente(dados.getCliente());
        atual.setEmpresa(dados.getEmpresa());
        if (dados.getDataColetaExecucao() != null) atual.setDataColetaExecucao(dados.getDataColetaExecucao());
        if (dados.getCustoOperacionalTotal() != null) atual.setCustoOperacionalTotal(dados.getCustoOperacionalTotal());
        if (atual.getStatus() == StatusPedido.CANCELADO) {
            atual.setReceitaReciclagemTotal(BigDecimal.ZERO);
        } else if (dados.getReceitaReciclagemTotal() != null) {
            atual.setReceitaReciclagemTotal(dados.getReceitaReciclagemTotal());
        }
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }

    private void vincular(Pedido dados, Long idCliente, Long idEmpresa) {
        dados.setCliente(clienteRepo.findById(idCliente)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado: " + idCliente)));
        dados.setEmpresa(empresaRepo.findById(idEmpresa)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada: " + idEmpresa)));
    }
}
