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
public class Fibonacii extends Thread {
    
    int n;
    
    int resultado;

    public Fibonacii(int n) {
        this.n = n;
    }
    
    public void run()
    {
        if((n== 0)||(n== 1)) resultado =1;
        
        else{
            Fibonacii f1=new Fibonacii(n-1);
            Fibonacii f2=new Fibonacii(n-2);
            f1.start();
            f2.start();
            
            try
            {
                
                f1.join();
                
                f2.join();
                
            }
            catch(InterruptedException e)
                    {
                        
                    }
            
            resultado=f1.getResult()+f2.getResult();
            
        }
        
    }
    
    
    public int getResult()
    {
        
        return resultado;
        
    }
    
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Fibonacii f1= new Fibonacii(Integer.parseInt(args[0]));
        
        f1.start();
        
        try
        {
            f1.join();
        
        }
        
        catch(InterruptedException e)
                {
                    
                    
                    
                }
        
        System.out.println(f1.getResult());
        
        
        
    }
    
}
