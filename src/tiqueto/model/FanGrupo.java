package tiqueto.model;

import java.util.concurrent.ThreadLocalRandom;
import static tiqueto.EjemploTicketMaster.MAX_ENTRADAS_POR_FAN;

public class FanGrupo extends Thread {

    final WebCompraConciertos webCompra;
    int numeroFan;
    private String tabuladores = "\t\t\t\t";
    int entradasCompradas = 0;

    public FanGrupo(WebCompraConciertos web, int numeroFan) {
        super();
        this.numeroFan = numeroFan;
        this.webCompra = web;
    }

    public void run() {
        //si la venta está abierta && no he llegado al máximo de entradas que puedo comprar -> COMPRAMOS
        while (!webCompra.ventaTerminada.get() && this.entradasCompradas < MAX_ENTRADAS_POR_FAN) {
            mensajeFan("Voy a comprar una entrada");
            if (webCompra.comprarEntrada()) { //si la compra se ha realizado con éxito, sumamos 1 a las entradas compradas
                this.entradasCompradas++;
                mensajeFan("He comprado una entrada. Por ahora tengo " + entradasCompradas);
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                mensajeFan("No puedo comprar más entradas");
            }
        }
        if (webCompra.ventaTerminada.get()) { //si la venta está cerrada, interrumpimos el hilo
            mensajeFan("Se ha cerrado la venta");
            Thread.currentThread().interrupt();
        }
    }

    public void dimeEntradasCompradas() {
        mensajeFan("Sólo he conseguido: " + entradasCompradas);
    }

    /**
     * Método a usar para cada impresión por pantalla
     *
     * @param mensaje Mensaje que se quiere lanzar por pantalla
     */
    private void mensajeFan(String mensaje) {
        System.out.println(System.currentTimeMillis() + "|" + tabuladores + " Fan " + this.numeroFan + ": " + mensaje);
    }
}
