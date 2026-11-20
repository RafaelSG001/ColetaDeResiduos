package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.ItemPedido;
import br.garbage.ColetaDeResiduos.repository.EnderecoRepository;
import br.garbage.ColetaDeResiduos.repository.ItemPedidoRepository;
import br.garbage.ColetaDeResiduos.repository.PedidoRepository;
import br.garbage.ColetaDeResiduos.repository.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
public class ItemPedidoService {
    private final ItemPedidoRepository repository;
    private final PedidoRepository pedidoRepo;
    private final ServicoRepository servicoRepo;
    private final EnderecoRepository enderecoRepo;

    public ItemPedidoService(ItemPedidoRepository repository, PedidoRepository pedidoRepo,
                             ServicoRepository servicoRepo, EnderecoRepository enderecoRepo) {
        this.repository = repository;
        this.pedidoRepo = pedidoRepo;
        this.servicoRepo = servicoRepo;
        this.enderecoRepo = enderecoRepo;
    }

    @Transactional(readOnly = true)
    public Page<ItemPedido> filtrarPaginado(Long idServico, String destino, int pagina, int tamanho) {
        return repository.filtrar(idServico, destino,
                PageRequest.of(Math.max(0, pagina), tamanho, Sort.by("idItem").ascending()));
    }

    @Transactional(readOnly = true)
    public ItemPedido buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item de Pedido não encontrado: " + id));
    }

    @Transactional
    public ItemPedido cadastrar(ItemPedido dados, Long idPedido, Long idServico, Long idEndereco) {
        vincular(dados, idPedido, idServico, idEndereco);
        dados.setIdItem(null);
        if (dados.getNumeroMtrCertificado() == null || dados.getNumeroMtrCertificado().isBlank()) {
            dados.setNumeroMtrCertificado("MTR-" + System.currentTimeMillis());
        }
        return repository.save(dados);
    }

    @Transactional
    public ItemPedido atualizar(Long id, ItemPedido dados, Long idPedido, Long idServico, Long idEndereco) {
        ItemPedido atual = buscar(id);
        vincular(dados, idPedido, idServico, idEndereco);
        atual.setPedido(dados.getPedido());
        atual.setServico(dados.getServico());
        atual.setEndereco(dados.getEndereco());
        atual.setPesoToneladas(dados.getPesoToneladas());
        atual.setDestinoFinal(dados.getDestinoFinal());
        atual.setCustoItem(dados.getCustoItem());
        atual.setReceitaReciclagemItem(dados.getReceitaReciclagemItem());
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }

    private void vincular(ItemPedido d, Long idPedido, Long idServico, Long idEndereco) {
        if (d.getPesoToneladas() == null || d.getPesoToneladas().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O peso coletado em toneladas deve ser maior que zero.");
        }
        d.setPedido(pedidoRepo.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado.")));
        d.setServico(servicoRepo.findById(idServico)
                .orElseThrow(() -> new NoSuchElementException("Serviço não encontrado.")));
        d.setEndereco(enderecoRepo.findById(idEndereco)
                .orElseThrow(() -> new NoSuchElementException("Endereço não encontrado.")));
        if (d.getCustoItem() == null) {
            d.setCustoItem(d.getPesoToneladas().multiply(d.getServico().getTarifaPorTonelada()));
        }
        if (d.getReceitaReciclagemItem() == null) {
            d.setReceitaReciclagemItem(Boolean.TRUE.equals(d.getServico().getReciclavel())
                    ? d.getCustoItem().multiply(new BigDecimal("1.15")) : BigDecimal.ZERO);
        }
    }
}
