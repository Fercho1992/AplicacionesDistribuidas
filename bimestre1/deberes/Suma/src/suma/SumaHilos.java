/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suma;

/**
 *
 * @author Fernando Moya
 */
public class SumaHilos implements Runnable {

    int tamano;
    int inicio, fin; //inicio y fin para los threads
    Matriz matriz;
    long sum;

    public SumaHilos(int tamano, int inicio, int fin, Matriz matriz) {
        this.tamano = tamano;
        this.inicio = inicio;
        this.fin = fin;
        this.sum = 0;
        this.matriz = matriz;
    }

    //Tarea que ejecutaran los threads
    public void run() {
        for (int i = inicio; i < fin; i++) {
            for (int j = 0; j < tamano; j++) {
                sum = sum + matriz.matriz[i][j];
            }
        }
        matriz.sumaMatriz(sum); //se envia la suma al metodo sincronizado
    }

}
