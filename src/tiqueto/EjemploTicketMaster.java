package tiqueto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import tiqueto.model.FanGrupo;
import tiqueto.model.PromotoraConciertos;
import tiqueto.model.WebCompraConciertos;

public class EjemploTicketMaster {

    // Total de entradas que se venderán
    public static int TOTAL_ENTRADAS = 10;

    //al principio, habrá 0 entradas, y se irán reponiendo de 2 en 2 hasta que se acaben las 10 que hay
    public static int ENTRADAS_DISPONIBLES = 0;

    // El número de entradas que repondrá cada vez el promotor
    public static int REPOSICION_ENTRADAS = 2;

    // El número máximo de entradas por fan
    public static int MAX_ENTRADAS_POR_FAN = 5;

    // El número total de fans
    public static int NUM_FANS = 5;

    public static void main(String[] args) throws InterruptedException {

        String mensajeInicial = "[ Empieza la venta de tickets. Se esperan %d fans, y un total de %d entradas ]";
        System.out.println(String.format(mensajeInicial, NUM_FANS, TOTAL_ENTRADAS));
        WebCompraConciertos webCompra = new WebCompraConciertos();
        PromotoraConciertos liveNacion = new PromotoraConciertos(webCompra);
        List<FanGrupo> fans = new ArrayList<>();

        // Creamos todos los fans
        for (int numFan = 1; numFan <= NUM_FANS; numFan++) {
            FanGrupo fan = new FanGrupo(webCompra, numFan);
            fans.add(fan);
            fan.start();
        }

        //Lanzamos al promotor para que empiece a reponer entradas
        liveNacion.start();

        //Esperamos a que el promotor termine, para preguntar a los fans cuántas entradas tienen compradas
        liveNacion.join();

        System.out.println("\n [ Terminada la fase de venta - Sondeamos a pie de calle a los compradores ] \n");
        System.out.println("Total entradas ofertadas: " + TOTAL_ENTRADAS);
        System.out.println("Total entradas disponibles en la web: " + webCompra.entradasRestantes());

        // Les preguntamos a cada uno
        for (FanGrupo fan : fans) {
            fan.dimeEntradasCompradas();
        }

        System.exit(0);
    }

}
