/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hilos;

/**
 *
 * @author PC-12
 */
public class Hilos1 {
    
    static int x=0;
            static int y=0;
            
            public static int op1(){
                x=1;
                return y;
            }

            public static int op2(){
                y=5;
                return (3*x);
            }
    public static void main(String[] args) {
 
        System.out.println("El valor de op1: "+op1());
        System.out.println("El valor de op2: "+op2());
         }

    
}

/*Los valores que devuelve op1 es 1 y op2 es 3
El valor que van a depender va ser x, en caso de usar un valor para y el valor de op1 sera 0

*/
