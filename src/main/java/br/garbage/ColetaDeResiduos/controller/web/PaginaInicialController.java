package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaInicialController {

    private final DashboardService dashboardService;

    public PaginaInicialController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        dashboardService.preencherIndicadores(model);
        return "index";
    }
}
