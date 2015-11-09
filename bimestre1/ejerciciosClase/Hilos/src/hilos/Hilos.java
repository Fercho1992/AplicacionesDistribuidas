/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hilos;

/**
 *
 * @author 22B
 */
public class Hilos extends Thread {
    
    
    public void run()
    {
        
        System.out.println("Hello World");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Hilos t =new Hilos ();
        t.start();
    }
    
}
