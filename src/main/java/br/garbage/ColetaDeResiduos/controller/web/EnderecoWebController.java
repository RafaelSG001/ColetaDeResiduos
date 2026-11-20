package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.Endereco;
import br.garbage.ColetaDeResiduos.service.EnderecoService;
import br.garbage.ColetaDeResiduos.service.TipoEdificacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enderecos")
public class EnderecoWebController {
    private final EnderecoService service;
    private final TipoEdificacaoService tipoService;

    public EnderecoWebController(EnderecoService service, TipoEdificacaoService tipoService) {
        this.service = service;
        this.tipoService = tipoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("enderecos", service.listar());
        return "enderecos/lista";
    }

    private String formulario(Endereco dados, Model model) {
        model.addAttribute("endereco", dados);
        model.addAttribute("tiposEdificacao", tipoService.listar());
        return "enderecos/formulario";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Endereco(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(service.buscar(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Endereco endereco,
                         @RequestParam Long tipoEdificacaoId,
                         RedirectAttributes flash) {
        try {
            if (endereco.getIdEndereco() == null) service.cadastrar(endereco, tipoEdificacaoId);
            else service.atualizar(endereco.getIdEndereco(), endereco, tipoEdificacaoId);
            flash.addFlashAttribute("mensagem", "Endereço de coleta salvo com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/enderecos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.excluir(id);
            flash.addFlashAttribute("mensagem", "Endereço excluído.");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", "Não é possível excluir endereço vinculado a itens de coleta.");
        }
        return "redirect:/enderecos";
    }
}
