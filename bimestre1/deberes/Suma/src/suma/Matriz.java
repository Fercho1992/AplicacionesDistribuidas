/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suma;

import java.util.Random;

/**
 *
 * @author Fernando Moya
 */
public class Matriz {

    public int[]vector; //matriz cuadrada
    public int tamano;
    private long suma;
    private final static Random generator = new Random();

    public Matriz(int tamano) {
        this.vector = new int[tamano];
        this.tamano = tamano;
        this.suma = 0;
    }

    //genera numeros aleatorios entre 0 y 20 y los almacena en las posiciones d la matriz
    public void llenarMatriz() {
        for (int i = 0; i < tamano; i++) {
            
                vector[i] = generator.nextInt(5);
        }
    }

    public synchronized void sumaMatriz(long sum) {
        this.suma = this.suma + sum;

    }

    public long getSuma() {
        return suma;
    }

}
