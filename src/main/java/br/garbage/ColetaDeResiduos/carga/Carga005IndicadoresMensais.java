package br.garbage.ColetaDeResiduos.carga;

import br.garbage.ColetaDeResiduos.ColetaDeResiduosApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class Carga005IndicadoresMensais {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(ColetaDeResiduosApplication.class, args)) {
            System.out.println("Carga005IndicadoresMensais (Indicadores Mensais ESG) verificado/executado com sucesso!");
        }
    }
}
