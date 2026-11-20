package br.garbage.ColetaDeResiduos;

import br.garbage.ColetaDeResiduos.model.Cliente;
import br.garbage.ColetaDeResiduos.model.ItemPedido;
import br.garbage.ColetaDeResiduos.model.Pedido;
import br.garbage.ColetaDeResiduos.model.StatusPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PedidoRegrasNegocioTest {

    @Test
    @DisplayName("Deve permitir transições válidas de status: PENDENTE -> AGENDADO -> EM EXECUCAO -> CONCLUIDO")
    void devePermitirTransicoesValidasDeStatus() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);

        pedido.avancarStatus(StatusPedido.AGENDADO);
        assertEquals(StatusPedido.AGENDADO, pedido.getStatus());

        pedido.avancarStatus(StatusPedido.EM_EXECUCAO);
        assertEquals(StatusPedido.EM_EXECUCAO, pedido.getStatus());

        pedido.avancarStatus(StatusPedido.CONCLUIDO);
        assertEquals(StatusPedido.CONCLUIDO, pedido.getStatus());
    }

    @Test
    @DisplayName("Deve bloquear transição inválida a partir de estado final CANCELADO")
    void deveBloquearTransicaoDeCanceladoParaConcluido() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.CANCELADO);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                pedido.avancarStatus(StatusPedido.CONCLUIDO)
        );
        assertTrue(ex.getMessage().contains("Transição de status inválida"));
    }

    @Test
    @DisplayName("Deve bloquear retrocesso de AGENDADO para PENDENTE")
    void deveBloquearRetrocessoDeAgendadoParaPendente() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.AGENDADO);

        assertThrows(IllegalStateException.class, () ->
                pedido.avancarStatus(StatusPedido.PENDENTE)
        );
    }

    @Test
    @DisplayName("Deve zerar receita de reciclagem ao cancelar um pedido")
    void deveZerarReceitaAoCancelarPedido() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setReceitaReciclagemTotal(new BigDecimal("1500.00"));

        pedido.avancarStatus(StatusPedido.CANCELADO);

        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        assertEquals(BigDecimal.ZERO, pedido.getReceitaReciclagemTotal());
    }

    @Test
    @DisplayName("Deve calcular saldo líquido corretamente em Pedido e ItemPedido")
    void deveCalcularSaldoLiquidoCorretamente() {
        Pedido pedido = new Pedido();
        pedido.setReceitaReciclagemTotal(new BigDecimal("2500.00"));
        pedido.setCustoOperacionalTotal(new BigDecimal("900.00"));
        assertEquals(new BigDecimal("1600.00"), pedido.calcularSaldoLiquido());

        ItemPedido item = new ItemPedido();
        item.setReceitaReciclagemItem(new BigDecimal("800.50"));
        item.setCustoItem(new BigDecimal("300.50"));
        assertEquals(new BigDecimal("500.00"), item.calcularSubtotalLiquido());
    }

    @Test
    @DisplayName("Deve calcular volume total do contrato do cliente e tratar nulos (caso-limite)")
    void deveCalcularVolumeTotalContrato() {
        Cliente cliente = new Cliente();
        assertEquals(BigDecimal.ZERO, cliente.calcularVolumeTotalContrato());

        cliente.setVolumeContratadoMensalTon(new BigDecimal("12.50"));
        cliente.setTempoContratoMeses(12);
        assertEquals(new BigDecimal("150.00"), cliente.calcularVolumeTotalContrato());
    }
}