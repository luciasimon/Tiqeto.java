package tiqueto.model;

import tiqueto.IOperacionesWeb;
import java.util.concurrent.atomic.AtomicBoolean;
import static tiqueto.EjemploTicketMaster.*;

public class WebCompraConciertos implements IOperacionesWeb {
    public WebCompraConciertos() {
        super();
    }

    //Esta variable será la que indicará si la venta se ha cerrado o no
    public AtomicBoolean ventaTerminada = new AtomicBoolean(false);
    
    int entradasVendidas = 0;

    @Override
    public synchronized boolean comprarEntrada() {
        //Si hay entradas a la venta, compramos una
        if (hayEntradas()) {
            ENTRADAS_DISPONIBLES--;
            entradasVendidas++;
            mensajeWeb("ENTRADA COMPRADA. QUEDAN " + entradasRestantes());
            return true;
        } else {
            //Si no hay entradas a la venta, esperamos
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
        //Para reponer, vamos a añadir a las entradas disponibles el número de entradas que reponemos, que es pasado por parámetros
        ENTRADAS_DISPONIBLES += numeroEntradas;
        notifyAll(); //notificamos a los fans de que ya hay entradas
        return ENTRADAS_DISPONIBLES;
    }

    @Override
    public synchronized void cerrarVenta() {
        //Al poner la variable como True, estamos cerrando la venta
        ventaTerminada.set(true);
        notifyAll();
        mensajeWeb("VENTA CERRADA");
    }


    //hay entradas disponibles para compra?
    @Override
    public synchronized boolean hayEntradas() {
        //Devolverá true si hay entradas disponibles para la venta, si no, devolverá false
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
