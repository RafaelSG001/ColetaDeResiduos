package br.garbage.ColetaDeResiduos.carga;

import br.garbage.ColetaDeResiduos.ColetaDeResiduosApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class Carga004Empresas {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(ColetaDeResiduosApplication.class, args)) {
            System.out.println("Carga004Empresas (Empresas Operadoras) verificado/executado com sucesso!");
        }
    }
}
