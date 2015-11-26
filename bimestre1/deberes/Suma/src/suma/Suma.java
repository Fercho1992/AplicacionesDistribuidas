/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suma;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Fernando Moya
 */
public class Suma {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        int tamano = 6000;
        long sumaMatriz = 0;
        int contador = 0;
        Matriz matriz = new Matriz(tamano);
        matriz.llenarMatriz(); //llamamos al metodo que llenara la matriz
        long tinicio = System.currentTimeMillis(); //Tiempo inicio

        try {
            // Abrimos el archivo
            FileInputStream entradaTexto = new FileInputStream("suma.txt");
            // Creamos el objeto de entrada
            //DataInputStream entrada = new DataInputStream(fstream);
            // Creamos el Buffer de Lectura
            BufferedReader texto = new BufferedReader(new InputStreamReader(entradaTexto));
            String strLinea;
            //Contador

            // Leer el archivo linea por linea
            while ((strLinea = texto.readLine()) != null) {
                // Imprimimos la línea por pantalla
                contador++;
            }
            //
            //System.out.println(entradaTexto.toString());
            //
            System.out.println(contador);
            // Cerramos el archivo
            entradaTexto.close();
        } catch (Exception e) { //Catch de excepciones
            System.err.println("Ocurrio un error: " + e.getMessage());
        }

        //Realiza la suma normal de los valores de la matriz
        for (int i = 0; i < contador; i++) {
            
                sumaMatriz = sumaMatriz + matriz.vector[i];
            
        }
        long tfinal = System.currentTimeMillis();//tiempo fin

        System.out.println("La suma sin threads es: " + sumaMatriz + "  tiempo: " + (tfinal - tinicio));

        //crea los threads
        SumaHilos suma1 = new SumaHilos(tamano, 0, 1000, matriz);
        SumaHilos suma2 = new SumaHilos(tamano, 1000, 2000, matriz);
        SumaHilos suma3 = new SumaHilos(tamano, 2000, 3000, matriz);
        SumaHilos suma4 = new SumaHilos(tamano, 3000, 4000, matriz);
        SumaHilos suma5 = new SumaHilos(tamano, 4000, 5000, matriz);
        SumaHilos suma6 = new SumaHilos(tamano, 5000, 6000, matriz);

        ExecutorService executor = Executors.newCachedThreadPool();

        long tiinicio = System.currentTimeMillis();

        //Ejecuta los threads
        executor.execute(suma1);
        executor.execute(suma2);
        executor.execute(suma3);
        executor.execute(suma4);
        executor.execute(suma5);
        executor.execute(suma6);
        executor.shutdown();

        boolean tasksEnded = executor.awaitTermination(1, TimeUnit.MINUTES);

        if (tasksEnded) {
            long tifinal = System.currentTimeMillis();
            System.out.println("La suma con Hilos es: " + matriz.getSuma() + "  tiempo: " + (tifinal - tiinicio));
        }
    }

}
