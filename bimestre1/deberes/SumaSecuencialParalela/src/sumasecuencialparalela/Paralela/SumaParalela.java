/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sumasecuencialparalela.Paralela;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Fernando Moya
 * https://www.webprogramacion.com/169/java/hilo-sumador.aspx
 * http://mygnet.net/codigos/java/analisis_numericos/hilo_sumador_java.3016
 * http://profesores.elo.utfsm.cl/~agv/elo330/2s05/projects/CesarVasquez/sitio_web/codes/threads/Hilos.java
 * http://aflrovvs.blogspot.com/2013/03/como-usar-threads-hilos-en-java.html
 * http://jarroba.com/multitarea-e-hilos-en-java-con-ejemplos-thread-runnable/
 * http://www.redeszone.net/2012/08/06/curso-java-volumen-ii-regiones-criticas/
 */
public class SumaParalela extends Thread {

    private String entradaTexto;
    private String numeroLineas;
     private Integer parte1;
    private Integer sumaParte1;
    private Integer parte2;
    private Integer sumaParte2;

    
    
    
    //Constructor
    public SumaParalela() {
    }

    //Metodo Get
    public String getEntradaTexto() {
        return entradaTexto;
    }

    //Metodo Get
    public String getNumeroLineas() {
        return numeroLineas;
    }

    //Metodo Set
    public void setEntradaTexto(String entradaTexto) {
        this.entradaTexto = entradaTexto;
    }

    //Metodo Set
    public void setSuma(Integer suma) {
        this.sumaParte1 = suma;
    }

    //Metodo Set
    public void setNumeroLineas(String numeroLineas) {
        this.numeroLineas = numeroLineas;
    }

    //Metodo Get
    public Integer getSumaParte1() {
        return sumaParte1;
    }

    //Metodo Get
    public Integer getParte1() {
        return parte1;
    }

    //Metodo Set
    public void setParte1(Integer parte1) {
        this.parte1 = parte1;
    }

    
    //Constructor que recibe un String
    public SumaParalela(Integer suma) {
        this.sumaParte1 = suma;
    }

    
    public Integer getParte2() {
        return parte2;
    }

    public Integer getSumaParte2() {
        return sumaParte2;
    }

    //Metodo Set
    public void setParte2(Integer parte2) {
        this.parte2 = parte2;
    }

    //Metodo Set
    public void setSumaParte1(Integer sumaParte1) {
        this.sumaParte1 = sumaParte1;
    }

    //Metodo Set
    public void setSumaParte2(Integer sumaParte2) {
        this.sumaParte2 = sumaParte2;
    }

 
    public Integer numLine() {

        int sumaTotalTexto = 0;

        try {
            FileReader fr = new FileReader(getNumeroLineas());
            BufferedReader br = new BufferedReader(fr);
            sumaTotalTexto = 0;
            while ((numeroLineas = br.readLine()) != null) {
                sumaTotalTexto = sumaTotalTexto + 1;
            }

        } catch (Exception e) {
        }
        return sumaTotalTexto;
    }


    //Run
    @Override
    public void run() {
        try {
            FileReader fr = new FileReader(getEntradaTexto());
            BufferedReader br = new BufferedReader(fr);
            sumaParte1 = 0;
            sumaParte2 = 0;
            if (parte1 != null && parte2 == null) {
                for (int i = 0; i < parte1; i++) {
                    if ((numeroLineas = br.readLine()) != null) {
                        System.out.print(" " + numeroLineas);
                        sumaParte1 = sumaParte1 + (Integer.parseInt(numeroLineas));
                    }
                }
            } else {
                //for para segundo hilo
                for (int i = parte2; i > numeroLineas.length(); i++) {
                    if ((numeroLineas = br.readLine()) != null) {
                        System.out.print(" " + numeroLineas);
                        sumaParte2 = sumaParte2 + (Integer.parseInt(numeroLineas));
                    }
                }
            }
        } catch (Exception e) {
        }
    }
    
    //main
    public static void main(String[] args) throws InterruptedException {
        
        //Variables usadas para el control de tiempo
        Long Inicio;
        Long Fin;
        Long total;
        int sumaTotal;
        
        Inicio = System.currentTimeMillis();
        
        
        SumaParalela lineas = new SumaParalela();
       
        int divisionPartes ;
        
        //Dividir la informacion de nuestro documento en 2 parte estas sean igual o no
        divisionPartes=lineas.numLine()/2;
        
        lineas.setParte1(divisionPartes);
        
        //Comenzar el hilo de Java usted llamar a su método start ()
        lineas.start();
        
        //Método permite un hilo a esperar a la finalización de otro.
        lineas.join();
        
        
        //Impresion de la primera parte de nuestro archivo
        System.out.println("\nPrimera parte: " + lineas.getSumaParte1());
        SumaParalela lineas1 = new SumaParalela();
        lineas1.setParte1(null);
        lineas1.setParte2(lineas1.numLine()/2);
        //Comenzar el hilo de Java usted llamar a su método start ()
        lineas1.start();
        //Método permite un hilo a esperar a la finalización de otro.
        lineas1.join();
        
        
        
        //Impresion de la segunda parte de nuestro archivo
        System.out.println("\nSegunda parte: " + lineas1.getSumaParte2());
        sumaTotal = lineas.getSumaParte1() - lineas1.getSumaParte2();
        
        
        //Impresion de la parte 1 mas la parte 2 de nuestro archivo
        System.out.println("La suma total Parte 1 mas Parte 2 es: " + sumaTotal);
        
        //Cuanti tiempo se demora el programa en calcular el tiempo de ejecucion usando hilos
        Fin = System.currentTimeMillis();
        total = Fin - Inicio;
        System.out.println("Tiempo total de es :" + total);
        
    }

}
