/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hilos;

import java.util.Arrays;

/**
 *
 * @author PC-12
 */
public class Hilos {

    private static int index = -1;

    public static int parallelSearch(int busqueda, int[] arreglo, int numeroHilos) {

        // Division en partes
        
        int particiones = arreglo.length / numeroHilos;
        
        Thread[] hilos = new Thread[numeroHilos];
        
        //Variable fin inicializada en 0
        int fin = 0;
        
        //Variable inicio inicializada en 0
        int inicio = 0;

        // Comienza el recorrido desde cero hasta el valor que tenga numero de hilos
        
        for (int i = 0; i < numeroHilos; i++) {
            
            //La varaiable fin guarda el valor de inicio + el numero de particiones.
            
            fin = inicio + particiones;
            
            //Si la variable i es igual arreglo -1 o la variable fin es mayor  del tamano del arreglo
            
            if (i == numeroHilos - 1 || fin > arreglo.length) {
                
                //La varaiable fin guarda el valor del tamano del arreglo
                fin = arreglo.length;
            }
            
            
            BusquedaParalela target = new BusquedaParalela(inicio, fin, arreglo, busqueda);
            
            System.out.println(target);
            hilos[i] = new Thread(target);
            hilos[i].start();
            System.out.println(hilos[i].getName());
            inicio = fin;
        }
        return index;
    }

    public static void main(String[] args) 
    {
        int[] a = new int[100];
        for (int i = 0; i < a.length; i++) 
        {
            a[i] = (int) (Math.random()*10 );
        }
        int buscaParalela = parallelSearch(2, a, 7);
        if (-1 == buscaParalela) {
            System.out.println("Elemento no encontrado");
        } else {
            System.out.println("Elemento encontrado en: " + buscaParalela);
        }
    }

    //La interfaz  Runnable  define un único método de gestión , 
    //destinado a contener el código que se ejecuta en el hilo .

    //El objeto Runnable se pasa al constructor de rosca, como en el BusquedaParalela
    
    private static class BusquedaParalela implements Runnable {

        //Inicializacion de las variables
        int inicio;
        int fin;
        int[] a;
        int x;
        
        //Constructor de 4 elementos, uno de ellos es una arreglo vacio

        public BusquedaParalela(int inicio, int fin, int[] a, int x) {
            super();
            
            if (fin < inicio) {
                throw new IllegalStateException();
            }
            this.inicio = inicio;
            this.fin = fin;
            this.a = a;
        }
        
        //Sobreescribir el metodo run() y cambiamos el comportamiento
        @Override
        public void run() {
            for (int i = inicio; i < fin; i++) {
                if (x == a[i]) {
                    index = i;
                    System.out.println(i + " "
                            + Thread.currentThread().getName());
                    // break;
                }
            }
        }

        
        //Sobreescribir el metodo toString() y cambiamos el comportamiento
        @Override
        public String toString() {
            return "Busqueda en paralelo [Arreglo = " + Arrays.toString(a) + ", de " + a.length + " elementos"
                    + ", Inicia = " + inicio + ", Finaliza = " + fin + "]";
        }

    }

}
