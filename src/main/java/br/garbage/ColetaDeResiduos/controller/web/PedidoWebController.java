package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.Pedido;
import br.garbage.ColetaDeResiduos.model.StatusPedido;
import br.garbage.ColetaDeResiduos.service.ClienteService;
import br.garbage.ColetaDeResiduos.service.EmpresaService;
import br.garbage.ColetaDeResiduos.service.PedidoService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedidos")
public class PedidoWebController {
    private final PedidoService service;
    private final ClienteService clienteService;
    private final EmpresaService empresaService;

    public PedidoWebController(PedidoService service, ClienteService clienteService, EmpresaService empresaService) {
        this.service = service;
        this.clienteService = clienteService;
        this.empresaService = empresaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long idCliente,
                         @RequestParam(required = false) Long idEmpresa,
                         @RequestParam(required = false) StatusPedido status,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "50") int size,
                         Model model) {
        Page<Pedido> pagina = service.filtrarPaginado(idCliente, idEmpresa, status, page, size);
        model.addAttribute("pedidos", pagina.getContent());
        model.addAttribute("paginaAtual", pagina.getNumber());
        model.addAttribute("totalPaginas", Math.max(1, pagina.getTotalPages()));
        model.addAttribute("totalRegistros", pagina.getTotalElements());
        model.addAttribute("empresas", empresaService.listar());
        model.addAttribute("statusValores", StatusPedido.values());
        model.addAttribute("idClienteFiltro", idCliente);
        model.addAttribute("idEmpresaFiltro", idEmpresa);
        model.addAttribute("statusFiltro", status);
        return "pedidos/lista";
    }

    private String formulario(Pedido dados, Model model) {
        model.addAttribute("pedido", dados);
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("empresas", empresaService.listar());
        model.addAttribute("statusValores", StatusPedido.values());
        return "pedidos/formulario";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Pedido(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(service.buscar(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Pedido pedido,
                         @RequestParam Long clienteId,
                         @RequestParam Long empresaId,
                         RedirectAttributes flash) {
        try {
            if (pedido.getIdPedido() == null) service.cadastrar(pedido, clienteId, empresaId);
            else service.atualizar(pedido.getIdPedido(), pedido, clienteId, empresaId);
            flash.addFlashAttribute("mensagem", "Pedido de coleta salvo com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.excluir(id);
            flash.addFlashAttribute("mensagem", "Pedido excluído com sucesso.");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", "Para excluir este pedido, remova primeiro seus itens associados.");
        }
        return "redirect:/pedidos";
    }
}
