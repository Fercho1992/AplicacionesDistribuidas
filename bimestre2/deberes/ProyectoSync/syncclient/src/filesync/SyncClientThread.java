package filesync;

import filesync.BlockUnavailableException;
import filesync.CopyBlockInstruction;
import filesync.Instruction;
import filesync.InstructionFactory;
import filesync.NewBlockInstruction;
import filesync.SynchronisedFile;

import java.io.EOFException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.*;
import java.io.*;


/**
 * 
 */

public class SyncClientThread {// implements Runnable {

/**
	 * Variables necesarias para la sincronización
	 */
	private static String host;
	private static String filename;
	private static String action;
	private static int blockSize;
	private static SynchronisedFile file;
			
	/**
	 * Declarar variables necesarias para la comunicación con 
	 * el servidor
	 * 
	 */
        final  int PUERTO= 5000;
    
        private static Socket so;
        private static DataOutputStream salida;
        private static DataInputStream entrada;
        final String HOST = "localhost";/*string de conexion*/
		
	SyncClientThread(String h, String fn, String a, int bs){
		host = h;
		filename = fn;
		action = a;
		blockSize = bs;
            try {
                /**
                 * Inicializar varibales para establecer comunicación con
                 * el servidor
                 */
                so = new Socket (HOST, PUERTO);
            } catch (IOException ex) {
                Logger.getLogger(SyncClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	
	public void run() {
		try {
			
			
			/**
			 * Instanciar objetos necesarios para leer y escribir 
			 * en el stream
			 */
                    
                        salida = new DataOutputStream(so.getOutputStream() );
                        entrada = new DataInputStream(so.getInputStream() );
                    
			
			/**
			 * Enviar mensaje conteniendo la accion que se va 
			 * a realizar
			 * "commit" o "update"
			 * System.out.println( "Sending action: " + action );
			 */
			
                       salida.writeUTF(action);
			/*
			 * Recibir acuso de recibo de la acción enviada
			 
			* while( !reply.equals( "OK" ) ) {
			*	try {
			*		System.out.println("Waiting server to accept the action...");
					
			*	} catch (IOException e){
			*		System.out.println("Could not receive action confirmation from server: " + e.getMessage());
			*		e.printStackTrace();
			*		System.exit(-1);
			* 	}
			* }
			* System.out.println("Action confirmed.");
			*/

			

			/**
			 * Enviar el tamaño del bloque especificado: blocksize
			 * 
			 * System.out.println( "Sending blockSize: " + blockSize );
			*/
			
			/*
			 * Recibir el acuso de recibo del tamaño del bloque
			 * 
			 * while( !reply.equals( "OK" ) ) {
			 * ...
			 * }
			 * System.out.println("blocksize confirmed.");
			 */
			

			
			
			/*
			 * Initialise the SynchronisedFiles.
			 */
			file = new SynchronisedFile( filename, blockSize );
			
			switch( action ){
				case "commit":
					actAsSender();
					break;
				case "update":
					actAsReceiver();
					break;
				default:
					System.out.println( "Invalid action. Usage: java -jar syncclient.jar hostname filename (commit | update) blocksize" );
					System.exit(-1);
			}	
		} catch (IOException e) {
//			System.out.println(ex.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static void actAsSender() {
		Instruction inst;
		String reply = "";
		long startTime = System.currentTimeMillis();
		try {
			System.out.println("SyncClient: calling fromFile.CheckFileState()");
			file.CheckFileState();
		} catch (IOException e) {
//			System.out.println(ex.getMessage());
			e.printStackTrace();
			System.exit(-1);
		} catch (InterruptedException e) {
//			System.out.println(ex.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		// The Client reads instructions to send to the Server
		while( (inst = file.NextInstruction()) != null ){
			
                    if( reply.equals( "NEW" ) ) {
                        /*
                        * El cliente cambia la instrucción CopyBlock por
                        * una NewBlock y lo envia.
                        * El mensaje debe ser empaquetado antes de
                        * enviarse.
                        */
                        Instruction upgraded = new NewBlockInstruction( ( CopyBlockInstruction ) inst );
                        
                        
                        /**
                         * Enviar la la nueva instrucción al servidor
                         * y recibir el acuso de recibo
                         */
                        // System.err.println( "Sending: " + msg2 );
                    }
			

			/**
			 * Verificar que el acuso de recibo es OK y moverse a 
			 * la siguiente instrucción
			 */
			while( !reply.equals( "OK" ) ) {
				//...
			}
			// System.out.println("OK received. Move to the next instruction.");

			//finalise sync
			if( inst.Type().equals("EndUpdate")  ) {
				System.out.println("Sync finalised.");
				long finishTime = System.currentTimeMillis();
				System.out.println("Total time of Synchrohisation: " + (finishTime - startTime));
				System.exit(0);
			}
		}
	}
	
	private static void actAsReceiver() {
		long startTime = System.currentTimeMillis();
		while(true) {
                    InstructionFactory instFact = new InstructionFactory();
                    Instruction receivedInst = null;
                    try {
                        // The client processes the instruction
                        file.ProcessInstruction( receivedInst );
                    } catch ( IOException e ) {
//					System.out.println( e.getMessage() );
                        e.printStackTrace();
                        System.exit(-1); // just die at the first sign of trouble
                    } catch ( BlockUnavailableException e ) {
                        // The client does not have the bytes referred to
                        // by the block hash.
                        try {
                            /**
                             * Si se lanza esta excepción quiere decir que
                             * el cliente no tiene los bytes a los que
                             * hace referencia el bloque hash recibido.
                             * Por lo tanto el cliente debe enviar una
                             * petición al servidor para que le sean
                             * enviados los bytes reales contenidos en el
                             * bloque.
                             */
                            // System.out.println( "Client requesting NEW" );
                            
                            
                            /*
                            * El cliente recibe el nuevo bloque de bytes
                            * los cuales deben ser desempaquetados antes
                            * de ser procesados.
                            * Utilizar el metodo fromJSON de la clase
                            * InstructionFactory
                            */
                            // System.out.println("Client reading NEW");
                            
                            Instruction receivedInst2 = null;
                            file.ProcessInstruction( receivedInst2 );
                        } catch (IOException e1) {
                            System.exit(-1);
                        } catch (BlockUnavailableException e1) {
                            assert(false); // a NewBlockInstruction can never throw this exception
                        }
                    }
                    if( receivedInst.Type().equals("EndUpdate")  ) {
					System.out.println("Sync finalised.");
					long finishTime = System.currentTimeMillis();
					System.out.println("Total time of Synchrohisation: " + (finishTime - startTime));
					System.exit(0);
				}
		}
	}
}
