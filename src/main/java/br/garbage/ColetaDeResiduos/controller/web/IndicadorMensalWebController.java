package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.IndicadorMensal;
import br.garbage.ColetaDeResiduos.service.EmpresaService;
import br.garbage.ColetaDeResiduos.service.IndicadorMensalService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/indicadores")
public class IndicadorMensalWebController {
    private final IndicadorMensalService service;
    private final EmpresaService empresaService;

    public IndicadorMensalWebController(IndicadorMensalService service, EmpresaService empresaService) {
        this.service = service;
        this.empresaService = empresaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long idEmpresa,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "50") int size,
                         Model model) {
        Page<IndicadorMensal> pagina = service.listarPaginado(idEmpresa, page, size);
        model.addAttribute("indicadores", pagina.getContent());
        model.addAttribute("paginaAtual", pagina.getNumber());
        model.addAttribute("totalPaginas", Math.max(1, pagina.getTotalPages()));
        model.addAttribute("totalRegistros", pagina.getTotalElements());
        model.addAttribute("empresas", empresaService.listar());
        model.addAttribute("idEmpresaFiltro", idEmpresa);
        return "indicadores/lista";
    }

    private String formulario(IndicadorMensal dados, Model model) {
        model.addAttribute("indicadorMensal", dados);
        model.addAttribute("empresas", empresaService.listar());
        return "indicadores/formulario";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new IndicadorMensal(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(service.buscar(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute IndicadorMensal indicadorMensal,
                         @RequestParam Long empresaId,
                         RedirectAttributes flash) {
        try {
            if (indicadorMensal.getIdIndicador() == null) service.cadastrar(indicadorMensal, empresaId);
            else service.atualizar(indicadorMensal.getIdIndicador(), indicadorMensal, empresaId);
            flash.addFlashAttribute("mensagem", "Indicador mensal ESG salvo com sucesso!");
        } catch (Exception ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/indicadores";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        service.excluir(id);
        flash.addFlashAttribute("mensagem", "Indicador mensal excluído.");
        return "redirect:/indicadores";
    }
}
