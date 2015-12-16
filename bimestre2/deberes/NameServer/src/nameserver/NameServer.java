/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author PC-12
 */
public class NameServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //table = new NameTable();
      Long tiempoInicio;
      Long tiempoFinal;
      Long tiempoA;
        tiempoInicio = System.currentTimeMillis();
        System.out.println("Iniciando  :");
        try {
            ServerSocket listener = new ServerSocket(Symbols.serverPort);
            while (true) {
                Socket aClient = listener.accept();
                Conexion cs = new Conexion(aClient);
                new Thread(cs).start();
                try {
                    Thread.sleep(20000); //El hilo duerme durante 20 segundos
                    Thread.sleep(20000); 
                } catch (InterruptedException ex) {
                    System.err.println("EL hilo no se durmio lo esperado: " + ex);
                }
                tiempoFinal = System.currentTimeMillis();
                tiempoA = tiempoFinal - tiempoInicio;
                System.out.println("Tiempo de ejecucion: " + tiempoA);

                /*Cuando El hilo es dormido para ver el tiempo de ejecucion
                 Podemos Observar que el tiempo de ejecucion  es demasiado 
                 ya que aqui realiza el primer hilo , duerme durante 20 Segundos
                 y despues sigue con e sihuiente , lo cual implica el doble de 
                 tipo de ejecucion solo para un cliente,por lo cual vemos que si
                 los hilos seria perdida de recursos que un solo Servidor 
                 atienda a un solo cliente a la vez.*/
            }

        } catch (IOException e) {
            System.err.println("Servidor abortado  :" + e);
        }
    }
}

