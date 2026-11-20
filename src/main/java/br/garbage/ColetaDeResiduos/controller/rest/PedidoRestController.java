package br.garbage.ColetaDeResiduos.controller.rest;

import br.garbage.ColetaDeResiduos.model.Pedido;
import br.garbage.ColetaDeResiduos.service.PedidoService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    private final PedidoService service;

    public PedidoRestController(PedidoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public PedidoDTO buscar(@PathVariable Long id) {
        Pedido p = service.buscar(id);
        return new PedidoDTO(
                p.getIdPedido(),
                p.getCodigoProtocolo(),
                p.getCliente().getRazaoSocial(),
                p.getEmpresa().getNomeEmpresa(),
                p.getStatus().getRotulo(),
                p.getDataColetaExecucao(),
                p.getCustoOperacionalTotal(),
                p.getReceitaReciclagemTotal()
        );
    }

    public record PedidoDTO(
            Long id,
            String protocolo,
            String cliente,
            String empresa,
            String status,
            LocalDate dataColeta,
            BigDecimal custoOperacional,
            BigDecimal receitaReciclagem
    ) {}
}
