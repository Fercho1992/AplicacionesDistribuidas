package filesync;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class SyncServerThread extends Thread {
	
	/**
	 * Declarar las variable necesarias para establecer
	 * la comunicación con el cliente. Será necesario un socket
	 * de servidor y uno de cliente para escuchar y recibir los 
	 * mensajes.
	 */
	private static  InstructionFactory instFact = new InstructionFactory();
	private static SynchronisedFile file;
	private static String filename;
	private static Boolean newNegotiation = true;
	private static String action = "";
	private static int blockSize;
        final  int PUERTO= 5000;
        private static ServerSocket sc;
        private static Socket so;
        private static DataOutputStream salida;
        private static DataInputStream entrada;
	
	public SyncServerThread (String fn) {
            try {
                filename = fn;
                sc= new ServerSocket(PUERTO);
                /**
                 * Inicializar varibales para establecer comunicación con
                 * el cliente
                 */
            } catch (IOException ex) {
                Logger.getLogger(SyncServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

	}
		
   
	
	public void run() {
		int i = 0;

		while (true){
			try { // an echo server
				
				if( newNegotiation ) {
					newNegotiation = false;
					
					/**
					 * Para una nueva negociación el servidor debe 
					 * escuchar peticiones y aceptar las peticiones 
					 * entrantes.
					 * Luego debe instanciar los objetos para poder 
					 * leer y escribir en el stream.
					 */
                                         so= sc.accept();
                                         entrada = new DataInputStream(so.getInputStream());                                         
                                         salida = new DataOutputStream(so.getOutputStream());
                                         System.out.println( "Server listening for data" );
                                         action= entrada.readUTF();
					
					/**
					 * El primer mesaje que debe recibir es la accion 
					 * a realizar "commit" o "update"
					 * Si la accion es válida entonces debe enviar un
					 * acuso de recibo "OK"
					 * Y esperar por el siguiente mensaje que es el 
					 * tamaño del bloque y enviar el correspondiente
					 * acuso de recibo. **/
                                         if ("update".equals(action) || "commit".equals(action)) {
                                         salida.writeUTF("OK");
                                         blockSize= Integer.parseInt(entrada.readUTF()) ;
                                         salida.writeUTF("OK");
                                         file =  new SynchronisedFile(filename, blockSize );
                                         if (blockSize >= 0)
                                         {
                                             if ("commit".equals(action))
                                             {
                                                System.out.println("Server reading data");
                                                actAsReceiver(action); 
                                                System.out.println( "Received data " + ++i );
                                             }
                                             if ("update".equals(action)) {
                                                 actAsSender();
                                             }
                                             
                                          }else{
                                          
                                             System.out.println("Conexiòn Cerrada");
                                             sc.close();
                                          
                                          }
                                                 
                                         }
                                         else {
                                                 System.out.println("Conexiòn Cerrada");
                                             sc.close();
                                                }
                                          /**
					 * Luego debe inicializar el objeto 
					 * SynchronisedFile con los datos recibidos:
					 *	file = 
					 * new SynchronisedFile( filename, blockSize );
					 * 
					 * Si la acción no es válida el servidor debe 
					 * mostrar el mensaje respectivo y terminar la 
					 * conexión.
					 * 
					 */
					// System.out.println("Server reading data");
					
					
					System.out.println("Action received: " + action);
				}
				
				/**
				 * Contrario al cliente, si la acción es 
				 * "commit" el servidor actua como Receptor
				 * "update" el servidor actua como Emisor
				 */
				
			} catch (EOFException e){
				System.out.println("EOF: " + e.getMessage());
				System.exit(-1);
			} catch(IOException e) {
				System.out.println("readline: " + e.getMessage());
				System.exit(-1);
			}
		}
	}
	
	private static void actAsReceiver(String msg){
		try {
			/*
			 * El servidor recibe la instrucción la cual debe ser
			 * desempaquetada antes de ser procesada.
			 * metodo FromJSON de la clase InstructionFactory
			 */                     
			Instruction receivedInst= instFact.FromJSON(msg); 
			try {
				// The Server processes the instruction
				file.ProcessInstruction( receivedInst );
			} catch ( IOException e ) {
				System.out.println( e.getMessage() );
				System.exit( -1 ); // just die at the first sign of trouble
			} catch ( BlockUnavailableException e ) {
				// The server does not have the bytes referred to by the block hash.
				try {
                                    /**
                                     * Si se lanza esta excepción quiere decir que
                                     * el servidor no tiene los bytes a los que
                                     * hace referencia el bloque hash recibido.
                                     * Por lo tanto el servidor debe enviar una
                                     * petición al cliente para que le sean
                                     * enviados los bytes reales contenidos en el
                                     * bloque.
                                     */
                                    
                                    System.out.println( "Server requesting NEW" );
                                    file.ProcessInstruction( receivedInst );
                                    salida.writeUTF("NEW");
					/*
					 * El servidor recibe el nuevo bloque de bytes
					 * los cuales deben ser desempaquetados antes
					 * de ser procesados.
					 * Utilizar el metodo fromJSON de la clase
					 * InstructionFactory
					 */
                                         msg = entrada.readUTF();
                                        Instruction receivedInst2= instFact.FromJSON(msg);
					file.ProcessInstruction( receivedInst2 );
				} catch (IOException e1) {
					System.out.println( e1.getMessage() );
					System.exit(-1);
				} catch (BlockUnavailableException e1) {
					assert(false); // a NewBlockInstruction can never throw this exception
				}
			}

			/*
			 * Como estamos usando un protocolo 
			 * peticion-respuesta, el cliente debe enviar un
			 * acuso de recibo al servidor para indicar que 
			 * el bloque fue recibido correctamente y que la 
			 * siguiente instrucción puede ser enviada.
			 */
                        //System.out.println("Action received: " + action);
			System.out.println( "Server writing OK" );
                        salida.writeUTF("OK");
			//finalise sync
			if( receivedInst.Type().equals("EndUpdate")  ) {
				System.out.println("Sync finalised.");
				newNegotiation = true; // la sincronización se ha finalizado, entonces la siguiente será 
                                action = "";                          //una nueva negociación de parametros
				blockSize = 0;   
			}
   		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		}
	}
	
	private static void actAsSender() {
		// arguments supply hostname filename

		Instruction inst;
		String reply = "";
		try {
			System.out.println("SyncServer: calling fromFile.CheckFileState()");
			file.CheckFileState();
		} catch (IOException | InterruptedException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		}
		
		// The server reads instructions to send to the client
		while( ( inst = file.NextInstruction() ) != null ){                   
                           /**
			 * El servidor envia las instrucciones de sincronización 
			 * hacia el cliente
			 * 
			 * Los mensajes deben ser empaquetados utilizando el 
			 * método ToJSON() dentro de la clase Instruction
			 */
			try {
                             
                             reply = inst.ToJSON();
                             salida.writeUTF(reply);
                            System.out.println( "Received: " + reply);
                            reply = entrada.readUTF();

				/** Si el servidor envia como respuesta "NEW", quiere
				 * decir que existe un cambio en el archivo y por lo
				 * tanto el cliente debe cambiar el CopyBlock por un 
				 * NewBlock
				 */ 
				if( reply.equals( "NEW" ) ) {
					/*
					 * El servidor cambia la instrucción CopyBlock por 
					 * una NewBlock y lo envia.
					 * El mensaje debe ser empaquetado antes de 
					 * enviarse.
					 */
					String msg2; 
                                        Instruction upgraded = new NewBlockInstruction( ( CopyBlockInstruction ) inst );
					msg2 = upgraded.ToJSON();
					/**
					 * Enviar la la nueva instrucción al servidor 
					 * y recibir el acuso de recibo
					 */ 
					System.err.println( "Sending: " + msg2 );
                                        salida.writeUTF(msg2);
                                        reply = entrada.readUTF(); 
					
				}
			}catch (UnknownHostException e) {
				System.out.println( "Socket: " + e.getMessage() );
				System.exit(-1);
			}catch (EOFException e){
				System.out.println( "EOF: " + e.getMessage() );
				System.exit(-1);
			}catch (IOException e){
				System.out.println( "readline: " + e.getMessage() );
				System.exit(-1);
			}

			/**
			 * Verificar que el acuso de recibo es OK y moverse a 
			 * la siguiente instrucción
			 */
                       	while(!reply.equals( "OK" ) ) {
                            try {
                                //...
                                reply = entrada.readUTF();
                            } catch (IOException ex) {
                                Logger.getLogger(SyncServerThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
			}
			System.out.println("OK received. Move to the next instruction.");
			
			//finalise sync
			if( inst.Type().equals("EndUpdate")  ) {
				System.out.println("Sync finalised.");
				newNegotiation = true;// se puede empezar una nueva negociación de parámetros.
				action = "";
				blockSize = 0;
				break;
			}
		}
	}
}