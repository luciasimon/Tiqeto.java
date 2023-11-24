package tiqueto.model;

import java.util.concurrent.ThreadLocalRandom;

import static tiqueto.EjemploTicketMaster.*;

public class PromotoraConciertos extends Thread {

    final WebCompraConciertos webCompra;

    public PromotoraConciertos(WebCompraConciertos webCompra) {
        super();
        this.webCompra = webCompra;
    }

    @Override
    public void run() {
        while (!webCompra.ventaTerminada.get()) {
            try {
                //si 1)no hay entradas a la venta, 2) al promotor le quedan entradas, 3)no se ha llegado al máximo que los fans pueden comprar en total...
                if (!webCompra.hayEntradas() && webCompra.entradasRestantes() > 0 && (NUM_FANS * MAX_ENTRADAS_POR_FAN) > webCompra.entradasVendidas) {
                    mensajePromotor("Uy, no hay entradas... voy a reponer");
                    if (webCompra.entradasRestantes() >= REPOSICION_ENTRADAS) {
                        webCompra.reponerEntradas(REPOSICION_ENTRADAS);
                        mensajePromotor("Entradas repuestas (estándar).");
                        //explicación de "estándar": repone el número de entradas definido en la variable REPOSICION_ENTRADAS
                    } else {
                        webCompra.reponerEntradas(webCompra.entradasRestantes());
                        mensajePromotor("Entradas repuestas (restantes).");
                        /*explicación de "restantes": en caso de que las entradas que le queden al promotor sean un número
                        menor al de REPOSICION_ENTRADAS, repondrá las restantes*/
                    }
                    Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 8000)); //dormimos

                // si 1)no hay entradas a la venta, 2) al promotor le quedan entradas, 3) se ha llegado al máximo que los fans pueden comprar en total...
                } else if (!webCompra.hayEntradas() && webCompra.entradasRestantes() > 0 && (NUM_FANS * MAX_ENTRADAS_POR_FAN) == webCompra.entradasVendidas) {
                    mensajePromotor("Los fans me han comprado todas las entradas que podían comprar");
                    webCompra.cerrarVenta();

                //si 1)no hay entradas a la venta, 2) al promotor no le quedan entradas...
                } else if (!webCompra.hayEntradas() && webCompra.entradasRestantes() == 0) {
                    mensajePromotor("No me quedan entradas");
                    webCompra.cerrarVenta();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Método a usar para cada impresión por pantalla
     *
     * @param mensaje Mensaje que se quiere lanzar por pantalla
     */
    private void mensajePromotor(String mensaje) {
        System.out.println(System.currentTimeMillis() + "| Promotora: " + mensaje);

    }
}
