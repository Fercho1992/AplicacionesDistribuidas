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

/**
 *
 * @author 22B
 */
public class ContadorLineas extends Thread{

    public static void main(String[] args) {

        public void run()
        {
        try {
            FileReader fr1 = new FileReader("ABC.txt");

            BufferedReader bf1 = new BufferedReader(fr1);

            String Cadena;
            
            int contador=0;
            

            //sCadena1 = bf1.readLine();

            while ((Cadena = bf1.readLine()) != null) {
                //sCadena1 = bf1.readLine();
                contador ++;
               
                
                
            }
            System.out.println(fr1.toString());
             System.out.println(contador);

        } 
        catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        }
}