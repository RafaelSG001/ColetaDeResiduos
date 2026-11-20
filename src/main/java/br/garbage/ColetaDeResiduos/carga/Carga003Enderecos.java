package br.garbage.ColetaDeResiduos.carga;

import br.garbage.ColetaDeResiduos.ColetaDeResiduosApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class Carga003Enderecos {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(ColetaDeResiduosApplication.class, args)) {
            System.out.println("Carga003Enderecos (Endereços de Coleta) verificado/executado com sucesso!");
        }
    }
}
