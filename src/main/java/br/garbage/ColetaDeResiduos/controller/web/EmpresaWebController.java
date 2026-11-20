package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.Empresa;
import br.garbage.ColetaDeResiduos.service.EmpresaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empresas")
public class EmpresaWebController {
    private final EmpresaService service;

    public EmpresaWebController(EmpresaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empresas", service.listar());
        return "empresas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("empresa", service.buscar(id));
        return "empresas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Empresa empresa, RedirectAttributes flash) {
        try {
            if (empresa.getIdEmpresa() == null) service.cadastrar(empresa);
            else service.atualizar(empresa.getIdEmpresa(), empresa);
            flash.addFlashAttribute("mensagem", "Empresa operadora salva com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/empresas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.excluir(id);
            flash.addFlashAttribute("mensagem", "Empresa excluída com sucesso.");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", "Não é possível excluir empresa com pedidos ou indicadores vinculados.");
        }
        return "redirect:/empresas";
    }
}
