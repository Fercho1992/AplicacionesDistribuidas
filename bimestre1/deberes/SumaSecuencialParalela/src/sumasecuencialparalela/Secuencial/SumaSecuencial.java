/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sumasecuencialparalela.Secuencial;

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
public class SumaSecuencial {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        EntradaDatosTxt archivo1=new EntradaDatosTxt("1.txt");
        
        archivo1.entradaTexto();
        
         EntradaDatosTxt archivo2=new EntradaDatosTxt("2.txt");
         
        archivo2.entradaTexto();
        
         EntradaDatosTxt archivo3=new EntradaDatosTxt ("3.txt");
         
        archivo3.entradaTexto();
        
    }
    
}
