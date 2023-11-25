package tiqueto.model;

import tiqueto.IOperacionesWeb;
import java.util.concurrent.atomic.AtomicBoolean;
import static tiqueto.EjemploTicketMaster.*;

public class WebCompraConciertos implements IOperacionesWeb {
    public WebCompraConciertos() {
        super();
    }

    public AtomicBoolean ventaTerminada = new AtomicBoolean(false);
    int entradasVendidas = 0;

    @Override
    public synchronized boolean comprarEntrada() {
        //quitamos 1 de las entradas totales
        if (hayEntradas()) {
            ENTRADAS_DISPONIBLES--;
            entradasVendidas++;
            mensajeWeb("ENTRADA COMPRADA. QUEDAN " + entradasRestantes());
            return true;
        } else {
            try {
                mensajeWeb("NO HAY ENTRADAS DISPONIBLES. ESPERAMOS.");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    }

    @Override
    public synchronized int reponerEntradas(int numeroEntradas) {
        //vamos a decrementar el número de entradas que el promotor tiene, e incrementamos las que ponemos a la venta
        ENTRADAS_DISPONIBLES += numeroEntradas;
        notifyAll(); //notificamos a los fans de que ya hay entradas
        return ENTRADAS_DISPONIBLES;
    }

    @Override
    public synchronized void cerrarVenta() {
        ventaTerminada.set(true);
        notifyAll();
        mensajeWeb("VENTA CERRADA");
    }


    //hay entradas disponibles para compra?
    @Override
    public synchronized boolean hayEntradas() {
        return ENTRADAS_DISPONIBLES > 0;
    }


    //cuántas entradas restantes le quedan al promotor?
    @Override
    public synchronized int entradasRestantes() {
        return TOTAL_ENTRADAS - entradasVendidas;
    }


    /**
     * Método a usar para cada impresión por pantalla
     *
     * @param mensaje Mensaje que se quiere lanzar por pantalla
     */
    private void mensajeWeb(String mensaje) {
        System.out.println(System.currentTimeMillis() + "| WebCompra: " + mensaje);

    }

}