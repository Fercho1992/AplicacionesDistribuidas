package filesync;

import java.io.IOException;

/**
 * Este hilo de prueba contiene comentarios que explican como debe ser
 * implementada la arquitectura cliente servidor usando el protocolo de
 * sincronización de archivos.
 */
public class SyncTestThread implements Runnable {

  SynchronisedFile fromFile; // esto debería estar en el emisor
  SynchronisedFile toFile; // esto debería estar en el receptor

  SyncTestThread(SynchronisedFile ff, SynchronisedFile tf) {
    fromFile = ff;
    toFile = tf;
  }

  @Override
  public void run() {
    Instruction inst;
    InstructionFactory instFact = new InstructionFactory();
    // El emisor lee las instrucciones que se van a enviar al receptor
    while ((inst = fromFile.NextInstruction()) != null) {
      String msg = inst.ToJSON();
      System.err.println("Sending: " + msg);
      /*
	   * Aquí el emisor enviaría el mensage al receptor
       */

      // network delay
      
      /*
       * El receptor recibe la instrucción aquí.
       */
      Instruction receivedInst = instFact.FromJSON(msg);

      try {
        // El receptor procesa la instruccion
        toFile.ProcessInstruction(receivedInst);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(-1); // just die at the first sign of trouble
      } catch (BlockUnavailableException e) {
	    // El receptor no tiene los bytes a los cuales hace referencia 
        //el hash del bloque recibido.
        try {
          /*
		   * En este punto el el receptor necesita enviar una 
           * respuesta de vuelta al emisor indicando que necesita los
           * los bytes reales para sincronizar su archivo.
           */

		  // network delay
          
          /*
           * El emisor cambia la instrucción CopyBlock a una NewBlock 
           * y la envía
           */
          Instruction upgraded = new NewBlockInstruction((CopyBlockInstruction) inst);
          String msg2 = upgraded.ToJSON();
          System.err.println("Sending: " + msg2);

		  // network delay
          
          /*
	       * El receptor recibe la instrucción NewBlock
           */
          Instruction receivedInst2 = instFact.FromJSON(msg2);
          toFile.ProcessInstruction(receivedInst2);
        } catch (IOException e1) {
          e1.printStackTrace();
          System.exit(-1);
        } catch (BlockUnavailableException e1) {
          assert (false); // Una instrucción NewBlock nunca puede lanzar 
                          // esta exepcion
        }
      }
      /*
       * En este punto, al utilizar un protocolo ReticionRespuesta, el receptor 
       * puede enviar un acuso de recibo que el bloque ha sido recibido 
       * correctamente y que la siguiente instrucción puede ser enviada.
       */

	   // network delay
       
      /*
	   * El emisor recibe el acuso de recibo y continua con la siguiente 
       * instruccion.
       */
    } // ir a la siguiente instruccion en el loop infinito
  }
}
