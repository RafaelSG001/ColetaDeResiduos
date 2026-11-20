package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.TipoEdificacao;
import br.garbage.ColetaDeResiduos.service.TipoEdificacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tipos-edificacao")
public class TipoEdificacaoWebController {
    private final TipoEdificacaoService service;

    public TipoEdificacaoWebController(TipoEdificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tiposEdificacao", service.listar());
        return "tipos-edificacao/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("tipoEdificacao", new TipoEdificacao());
        return "tipos-edificacao/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("tipoEdificacao", service.buscar(id));
        return "tipos-edificacao/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute TipoEdificacao tipoEdificacao, RedirectAttributes flash) {
        try {
            if (tipoEdificacao.getIdTipo() == null) service.cadastrar(tipoEdificacao);
            else service.atualizar(tipoEdificacao.getIdTipo(), tipoEdificacao);
            flash.addFlashAttribute("mensagem", "Tipo de edificação salvo com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/tipos-edificacao";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.excluir(id);
            flash.addFlashAttribute("mensagem", "Tipo de edificação excluído.");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", "Não é possível excluir tipo de edificação em uso.");
        }
        return "redirect:/tipos-edificacao";
    }
}
