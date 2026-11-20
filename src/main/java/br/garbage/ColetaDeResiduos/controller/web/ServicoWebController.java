package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.Servico;
import br.garbage.ColetaDeResiduos.service.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicos")
public class ServicoWebController {
    private final ServicoService service;

    public ServicoWebController(ServicoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicos", service.listar());
        return "servicos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("servico", new Servico());
        return "servicos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("servico", service.buscar(id));
        return "servicos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Servico servico, RedirectAttributes flash) {
        try {
            if (servico.getIdServico() == null) service.cadastrar(servico);
            else service.atualizar(servico.getIdServico(), servico);
            flash.addFlashAttribute("mensagem", "Serviço de coleta salvo com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/servicos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.excluir(id);
            flash.addFlashAttribute("mensagem", "Serviço excluído.");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", "Não é possível excluir serviço vinculado a itens de pedido.");
        }
        return "redirect:/servicos";
    }
}
