package br.garbage.ColetaDeResiduos.controller.rest;

import br.garbage.ColetaDeResiduos.model.Empresa;
import br.garbage.ColetaDeResiduos.service.EmpresaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaRestController {

    private final EmpresaService service;

    public EmpresaRestController(EmpresaService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public EmpresaDTO buscar(@PathVariable Long id) {
        Empresa e = service.buscar(id);
        return new EmpresaDTO(e.getIdEmpresa(), e.getNomeEmpresa(), e.getCidadeSede(), e.getEstadoSede(), e.getCertificacaoIso14001());
    }

    public record EmpresaDTO(Long id, String nomeEmpresa, String cidadeSede, String estadoSede, Boolean iso14001) {}
}
