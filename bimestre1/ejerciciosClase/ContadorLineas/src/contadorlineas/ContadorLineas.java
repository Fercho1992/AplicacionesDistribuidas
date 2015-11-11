/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contadorlineas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 22B
 */
public class ContadorLineas extends Thread {

    private String archivos;
    private long numeroLineas = 0;

    public ContadorLineas(String archivos) {
        this.archivos = archivos;
    }

    public long getNumeroLineas() {
        return numeroLineas;
    }

    @Override
    public void run() {
        try {
            FileReader fr1 = new FileReader(this.archivos);
            BufferedReader bf1 = new BufferedReader(fr1);
            
            long contador = 0;
            String Cadena;

            //sCadena1 = bf1.readLine();
            while ((Cadena = bf1.readLine()) != null) {
                //sCadena1 = bf1.readLine();
                contador++;
            }
            this.numeroLineas = contador;
            
            //System.out.println(fr1.toString());
            //System.out.println(contador);

        } catch (FileNotFoundException fnfe) 
        {
            Logger.getLogger(ContadorLineas.class.getName()).log(Level.SEVERE, null, fnfe);
        } catch (IOException fnfe) 
        {
            Logger.getLogger(ContadorLineas.class.getName()).log(Level.SEVERE, null, fnfe);
        }
    }

    public static void main(String[] args) {

        ContadorLineas[] vector = new ContadorLineas[args.length];

        for (int i = 0; i < args.length; i++) {
            if (!args[i].getClass().equals(String.class)) {
                System.out.println("Archivo en la posicion" + i + ", no es valido");
                break;
            }

            vector[i] = new ContadorLineas(args[i]);
            vector[i].start();
        }

        int suma = 0;
        long longitud;

        for (int i = 0; i < vector.length; i++) {

            try {
                vector[i].join();
                longitud = vector[i].getNumeroLineas();
                suma += 1;
                System.out.println(args[i] + " : " + longitud);
            } catch (InterruptedException ex) 
            {
                Logger.getLogger(ContadorLineas.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        System.out.println("Total: " + suma);

    }

}
