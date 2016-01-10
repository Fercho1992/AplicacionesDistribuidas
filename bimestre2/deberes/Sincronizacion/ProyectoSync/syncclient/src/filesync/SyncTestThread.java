package filesync;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Este hilo de prueba contiene comentarios que explican como debe ser
 * implementada la arquitectura cliente servidor usando el protocolo de
 * sincronización de archivos.
 */
public class SyncTestThread implements Runnable {

    SynchronisedFile fromFile; // esto debería estar en el emisor
    SynchronisedFile toFile; // esto debería estar en el receptor
    private boolean $assertionsDisabled;

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
            String message = inst.ToJSON();
            System.err.println("Enviando: " + message);
            /*
             * Aquí el emisor enviaría el mensage al receptor
             */

            // network delay
            /*
             * El receptor recibe la instrucción aquí.
             */
            Instruction receivedInst = instFact.FromJSON(message);

            try {
                // El receptor procesa la instruccion
                this.toFile.ProcessInstruction(receivedInst);
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
                    String message2 = upgraded.ToJSON();
                    System.err.println("Sending: " + message2);

                    // network delay
                    /*
                     * El receptor recibe la instrucción NewBlock
                     */
                    Instruction receivedInst2 = instFact.FromJSON(message2);
                    this.toFile.ProcessInstruction(receivedInst2);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.exit(-1);
                } catch (BlockUnavailableException e1) {
                    assert (false); // Una instrucción NewBlock nunca puede lanzar 
                    // esta exepcion
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
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
