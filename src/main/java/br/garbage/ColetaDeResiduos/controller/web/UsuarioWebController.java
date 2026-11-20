package br.garbage.ColetaDeResiduos.controller.web;

import br.garbage.ColetaDeResiduos.model.Perfil;
import br.garbage.ColetaDeResiduos.model.Usuario;
import br.garbage.ColetaDeResiduos.security.PrincipalAmbiental;
import br.garbage.ColetaDeResiduos.service.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioWebController {
    private final UsuarioService service;

    public UsuarioWebController(UsuarioService service) {
        this.service = service;
    }

    @InitBinder("usuario")
    public void configurarVinculacao(WebDataBinder binder) {
        binder.setAllowedFields("id", "username", "perfil", "ativo");
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", Perfil.values());
        return "usuarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable UUID id, Model model) {
        model.addAttribute("usuario", service.buscar(id));
        model.addAttribute("perfis", Perfil.values());
        return "usuarios/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("usuario") Usuario formulario,
                         @RequestParam(value = "novaSenha", required = false) String novaSenha,
                         @AuthenticationPrincipal PrincipalAmbiental principal,
                         RedirectAttributes redirect) {
        boolean novo = formulario.getId() == null;
        String retorno = novo ? "redirect:/usuarios/novo"
                : "redirect:/usuarios/" + formulario.getId() + "/editar";
        try {
            service.salvar(formulario.getId(), formulario.getUsername(), novaSenha,
                    formulario.getPerfil(), formulario.isAtivo(), principal.getId());
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            redirect.addFlashAttribute("erro", ex instanceof IllegalArgumentException
                    ? ex.getMessage() : "Não foi possível salvar: conflito de integridade no banco.");
            return retorno;
        }
        redirect.addFlashAttribute("sucesso", novo ? "Usuário cadastrado com sucesso."
                : "Usuário atualizado com sucesso.");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable UUID id,
                          @AuthenticationPrincipal PrincipalAmbiental principal,
                          RedirectAttributes redirect) {
        try {
            service.excluir(id, principal.getId());
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            redirect.addFlashAttribute("erro", ex instanceof IllegalArgumentException
                    ? ex.getMessage() : "Não foi possível excluir: existem restrições de integridade.");
            return "redirect:/usuarios";
        }
        redirect.addFlashAttribute("sucesso", "Usuário excluído com sucesso.");
        return "redirect:/usuarios";
    }
}
