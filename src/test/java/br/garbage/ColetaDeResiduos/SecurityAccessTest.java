package br.garbage.ColetaDeResiduos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Usuário não autenticado deve ser redirecionado para /login")
    void deveRedirecionarParaLoginSemAutenticacao() throws Exception {
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    @DisplayName("Perfil CLIENTE deve receber HTTP 403 ao tentar acessar /usuarios")
    void clienteNaoPodeAcessarUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    @DisplayName("Perfil CLIENTE deve receber HTTP 403 ao tentar excluir um pedido")
    void clienteNaoPodeExcluirPedido() throws Exception {
        mockMvc.perform(post("/pedidos/1/excluir").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Perfil ADMIN deve conseguir acessar a listagem de /usuarios")
    void adminPodeAcessarUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());
    }
}