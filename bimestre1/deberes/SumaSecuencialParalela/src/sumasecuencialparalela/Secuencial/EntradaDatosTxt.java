/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sumasecuencialparalela.Secuencial;

import java.io.BufferedReader;
import java.io.FileReader;


/**
 *
 * @author Fernando Moya
 */
public class EntradaDatosTxt {

    //Variables
    private String archivo;
    private String numLineas;
    private Integer sumaTotal;

    //Constructor
    public EntradaDatosTxt() {
    }

    public EntradaDatosTxt(String archivo) {
        this.archivo = archivo;

    }

    //Metodo Get
    public String getArchivo() {
        return archivo;
    }

    //Metodo Set
    /*public String getNumeroLineas() {
        return numLineas;
    }*/

    //Metodo Set
    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public void entradaTexto() {

        //Variables usadas para tomar el calcualar el tiempo de ejecucion
        long Inicio;
        long Fin;
        long sumaTiempo;

        //currentTimeMillis() Devuelve la hora actual en milisegundos 
        Inicio = System.currentTimeMillis();

        try {
            FileReader entradaTexto = new FileReader(getArchivo());

            BufferedReader texto = new BufferedReader(entradaTexto);

            sumaTotal = 0;
            //int contador=0;
            System.out.println("\nLos valores que tienes los archivos son: ");
            
            while ((numLineas = texto.readLine()) != null) {
                System.out.print(numLineas + "\n");
                sumaTotal = sumaTotal + (Integer.parseInt(numLineas));
            }
            System.out.println("\n La suma de total de los archivos es: " + sumaTotal + "\n");

        } catch (Exception e) 
        {
            //Logger.getLogger(EntradaDatosTxt.class.getName()).log(Level.SEVERE, null, e);
        }
        Fin = System.currentTimeMillis();
        
        sumaTiempo = Fin - Inicio;

        System.out.println("\n El tiempo en ejecutarse la suma secuancial es: " + sumaTiempo);
    }
    

}
