package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.Cliente;
import br.garbage.ColetaDeResiduos.service.ClienteService;
import br.garbage.ColetaDeResiduos.service.TipoEdificacaoService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteWebController {
    private final ClienteService service;
    private final TipoEdificacaoService tipoService;

    public ClienteWebController(ClienteService service, TipoEdificacaoService tipoService) {
        this.service = service;
        this.tipoService = tipoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(required = false) Long idTipo,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "50") int size,
                         Model model) {
        Page<Cliente> pagina = service.listarPaginado(busca, idTipo, page, size);
        model.addAttribute("clientes", pagina.getContent());
        model.addAttribute("paginaAtual", pagina.getNumber());
        model.addAttribute("totalPaginas", Math.max(1, pagina.getTotalPages()));
        model.addAttribute("totalRegistros", pagina.getTotalElements());
        model.addAttribute("tiposEdificacao", tipoService.listar());
        model.addAttribute("buscaFiltro", busca);
        model.addAttribute("idTipoFiltro", idTipo);
        return "clientes/lista";
    }

    private String formulario(Cliente dados, Model model) {
        model.addAttribute("cliente", dados);
        model.addAttribute("tiposEdificacao", tipoService.listar());
        return "clientes/formulario";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Cliente(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(service.buscar(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cliente cliente,
                         @RequestParam Long tipoEdificacaoId,
                         RedirectAttributes flash) {
        try {
            if (cliente.getIdCliente() == null) service.cadastrar(cliente, tipoEdificacaoId);
            else service.atualizar(cliente.getIdCliente(), cliente, tipoEdificacaoId);
            flash.addFlashAttribute("mensagem", "Cliente gerador salvo com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.excluir(id);
            flash.addFlashAttribute("mensagem", "Cliente excluído com sucesso.");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", "Não é possível excluir cliente que possui pedidos vinculados.");
        }
        return "redirect:/clientes";
    }
}
