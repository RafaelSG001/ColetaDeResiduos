package br.garbage.ColetaDeResiduos.carga;

import br.garbage.ColetaDeResiduos.ColetaDeResiduosApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class Carga007PedidosEItens {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(ColetaDeResiduosApplication.class, args)) {
            System.out.println("Carga007PedidosEItens (Pedidos de Coleta e Itens MTR) verificado/executado com sucesso!");
        }
    }
}
