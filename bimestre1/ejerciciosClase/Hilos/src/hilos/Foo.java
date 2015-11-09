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
public class Foo {
    String name;
    public  Foo ( String s){
        name = s;
    }
    public String getName(){
        return name;
    }
}

class FooBar extends Foo implements Runnable{
    public FooBar(String s){
        super (s);
    }
    public void run(){
        for(int i=0; i <10; i++){
            System.out.println(getName()+". Hello World");
        }
    }
    
    public static void main(String[] args) {
        FooBar f1 = new FooBar ( "Homero");
        Thread tq = new Thread(f1);
        tq.start();
        FooBar f2 = new FooBar("Juliet");
        Thread t2 = new Thread(f2);
        t2.start();
    }
}
