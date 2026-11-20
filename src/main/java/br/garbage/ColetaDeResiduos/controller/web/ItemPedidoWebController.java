package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.ItemPedido;
import br.garbage.ColetaDeResiduos.service.EnderecoService;
import br.garbage.ColetaDeResiduos.service.ItemPedidoService;
import br.garbage.ColetaDeResiduos.service.PedidoService;
import br.garbage.ColetaDeResiduos.service.ServicoService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/itens-pedido")
public class ItemPedidoWebController {
    private final ItemPedidoService service;
    private final PedidoService pedidoService;
    private final ServicoService servicoService;
    private final EnderecoService enderecoService;

    public ItemPedidoWebController(ItemPedidoService service, PedidoService pedidoService,
                                   ServicoService servicoService, EnderecoService enderecoService) {
        this.service = service;
        this.pedidoService = pedidoService;
        this.servicoService = servicoService;
        this.enderecoService = enderecoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long idServico,
                         @RequestParam(required = false) String destino,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "50") int size,
                         Model model) {
        Page<ItemPedido> pagina = service.filtrarPaginado(idServico, destino, page, size);
        model.addAttribute("itens", pagina.getContent());
        model.addAttribute("paginaAtual", pagina.getNumber());
        model.addAttribute("totalPaginas", Math.max(1, pagina.getTotalPages()));
        model.addAttribute("totalRegistros", pagina.getTotalElements());
        model.addAttribute("servicos", servicoService.listar());
        model.addAttribute("idServicoFiltro", idServico);
        model.addAttribute("destinoFiltro", destino);
        return "itens-pedido/lista";
    }

    private String formulario(ItemPedido dados, Model model) {
        model.addAttribute("itemPedido", dados);
        model.addAttribute("pedidos", pedidoService.listarRecentes());
        model.addAttribute("servicos", servicoService.listar());
        model.addAttribute("enderecos", enderecoService.listar());
        return "itens-pedido/formulario";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new ItemPedido(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(service.buscar(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ItemPedido itemPedido,
                         @RequestParam Long pedidoId,
                         @RequestParam Long servicoId,
                         @RequestParam Long enderecoId,
                         RedirectAttributes flash) {
        try {
            if (itemPedido.getIdItem() == null) service.cadastrar(itemPedido, pedidoId, servicoId, enderecoId);
            else service.atualizar(itemPedido.getIdItem(), itemPedido, pedidoId, servicoId, enderecoId);
            flash.addFlashAttribute("mensagem", "Item de coleta (MTR) salvo com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/itens-pedido";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        service.excluir(id);
        flash.addFlashAttribute("mensagem", "Item de coleta excluído.");
        return "redirect:/itens-pedido";
    }
}
