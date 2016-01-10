package filesync;

//import filesync.BlockUnavailableException;
//import filesync.CopyBlockInstruction;
//import filesync.Instruction;
//import filesync.InstructionFactory;
//import filesync.NewBlockInstruction;
//import filesync.SynchronisedFile;
//import java.net.ServerSocket;
import java.util.logging.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

/**
 *
 */
public class SyncClientThread
        implements Runnable {// implements Runnable {

    /**
     * Variables necesarias para la sincronización
     */
    private static String host;
    private static String filename;
    private static String action;
    private static int blockSize;
    private static SynchronisedFile file;
    /**
     * Declarar variables necesarias para la comunicación con el servidor
     *
     */
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Socket socket;
    private static boolean $assertionsDisabled;

    SyncClientThread(String h, String fn, String a, int bs) {
        host = h;
        filename = fn;
        action = a;
        blockSize = bs;
        int portServer = 7800;
        try {
            /**
             * Inicializar variables para establecer comunicación con el
             * Servidor
             */
            socket = new Socket(host, portServer);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
            //Logger.getLogger(SyncClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {

            String respuesta = "";

            /**
             * Instanciar objetos necesarios para leer y escribir en el stream
             */
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            /**
             * Empieza enviando el nombre del archivo que desea sincronizar
             */
            System.out.println("Enviando accion:" + action);
            out.writeUTF(action);
            while (!respuesta.equals("OK")) {
                try {
                    System.out.println("Esperando aceptacion desde el servidor: ");
                    respuesta = in.readUTF();
                } catch (IOException e) {
                    System.out.println("No se pudo recibir la confirmación desde el servidor :" + e.getMessage());
                    e.printStackTrace();
                    System.out.println(-1);
                }
            }
            System.out.println("Accion confirmada");
            respuesta = "";
            //Enviando nombre del archivo
            System.out.println("Enviando el nombre de archivo");
            out.writeUTF(filename);
            System.out.println("El nombre fue enviado: " + filename);
            //Enviando tamano del bloque
            System.out.println("Enviando el tamaño de bloque");
            out.writeInt(blockSize);
            System.out.println("Tamaño de bloque enviado: " + blockSize);

            /*
             * El siguiente código hace que el cliente espere por un acuso de 
             * recibo por parte del servidor. Esto debe hacerse siempre despues
             * de enviar un mensaje.  */
            String reply = in.readUTF();
            acuso(reply);
            System.out.println("Confirmed.");

            /**
             * Enviar mensaje conteniendo la accion que se va a realizar
             * "commit" o "update"
             */
            System.out.println("Enviando accion " + action);
            out.writeUTF(action);

            // Esperar por el acuso de recibo
            System.out.println("esperando acuso");
            acuso(reply);
            System.out.println("Acuso Recibido");

            /**
             * Enviar el tamaño del bloque especificado: blocksize
             */
            while (!respuesta.equals("OK")) {
                try {
                    System.out.println("Esperando que el servidor acepte el tamano del bloque");
                    respuesta = in.readUTF();
                } catch (IOException e) {
                    System.out.println("No se pudo recibir el tamano del bloque desde el servidor :" + e.getMessage());

                    e.printStackTrace();
                    System.exit(-1);
                }
            }

            /*
             * Initialise the SynchronisedFiles.
             */
            file = new SynchronisedFile(filename, blockSize);

            switch (action) {
                case "commit":
                    actAsSender();
                    break;
                case "update":
                    actAsReceiver();
                    break;
                default:
                    System.out.println("Accion invalida. Usage: java -jar syncclient.jar hostname filename (commit | update) blocksize");
                    System.exit(-1);
            }
        } catch (IOException e) {
//			System.out.println(ex.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void actAsSender() {
        String respuesta = "";
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("SyncClient: calling fromFile.CheckFileState()");
            file.CheckFileState();
        } catch (IOException e) {
//          System.out.println(ex.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException e) {
//          System.out.println(ex.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        Instruction inst;
        while ((inst = file.NextInstruction()) != null) {

            /**
             * El cliente envia las instrucciones de sincronización hacia el
             * servidor
             *
             * Los mensajes deben ser empaquetados utilizando el método ToJSON()
             * dentro de la clase Instruction
             */
            try {
                String message = inst.ToJSON();
                System.out.println("Enviando : " + message);
                out.writeUTF(message);
                respuesta = in.readUTF();
                System.out.println("Recibido: " + respuesta);

                //	System.out.println( "Sending: " + msg );
                //	System.out.println( "Received: "+ respuesta );
                /**
                 * Si el servidor envia como respuesta "NEW", quiere decir que
                 * existe un cambio en el archivo y por lo tanto el cliente debe
                 * cambiar el CopyBlock por un NewBlock
                 */
                if (respuesta.equals("NEW")) {
                    /*
                     * El cliente cambia la instrucción CopyBlock por 
                     * una NewBlock y lo envia.
                     * El mensaje debe ser empaquetado antes de 
                     * enviarse.
                     */
                    Instruction upgraded = new NewBlockInstruction((CopyBlockInstruction) inst);
                    String message2 = upgraded.ToJSON();
                    System.out.println("Enviando : " + message2);
                    out.writeUTF(message2);
                    respuesta = in.readUTF();

                    /**
                     * Enviar la nueva instrucción al servidor y recibir el
                     * acuso de recibo
                     */
                    // System.err.println( "Sending: " + msg2 );
                }
            } catch (UnknownHostException e) {
                System.out.println("Socket:" + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            } catch (IOException e) {
                System.out.println("readline: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }

            /**
             * Verificar que el acuso de recibo es OK y moverse a la siguiente
             * instrucción
             */
            while (!respuesta.equals("OK")) {
                try {
                    System.out.println("Esperando confirmacion del servidor...");
                    respuesta = in.readUTF();
                } catch (IOException e) {
                    System.out.println("Readline: " + e.getMessage());
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            System.out.println("Aceptacion exitosa. Siguiente instruccion.");
            if (inst.Type().equals("Finalizacion de la actualizacion")) {
                System.out.println("Sincronizacion finalizada");
                long finishTime = System.currentTimeMillis();
                System.out.println("Tiempo total en sincronizarse " + (finishTime - startTime));
                System.exit(0);
            }
        }
    }

    private static void actAsReceiver() {
        long startTime = System.currentTimeMillis();
        //while(true) {
        for (;;) {
            try {
                /**
                 * La acción es "update" por lo tanto es el cliente quien
                 * recibirá los datos desde el servidor
                 */
                // System.out.println("Client reading data");
                System.out.println("Cliente leyendo la informacion");
                String message = in.readUTF();

                /*
                 * El ciente recibe la instrucción aqui, la cual
                 * debe ser desempaquetada antes de ser procesada.
                 * Utilizar el metodo fromJSON de la clase
                 * InstrucionFactory
                 */
                InstructionFactory instFact = new InstructionFactory();
                Instruction receivedInst = instFact.FromJSON(message);

                try {
                    // The client processes the instruction
                    file.ProcessInstruction(receivedInst);
                } catch (IOException e) {
//					System.out.println( e.getMessage() );
                    e.printStackTrace();
                    System.exit(-1); // just die at the first sign of trouble
                } catch (BlockUnavailableException e) {
                    // The client does not have the bytes referred to
                    // by the block hash.
                    try {

                        /**
                         * Si se lanza esta excepción quiere decir que el
                         * cliente no tiene los bytes a los que hace referencia
                         * el bloque hash recibido. Por lo tanto el cliente debe
                         * enviar una petición al servidor para que le sean
                         * enviados los bytes reales contenidos en el bloque.
                         */
                        System.out.println("Cliente nuevo");
                        out.writeUTF("NEW");

                        System.out.println("Client reading NEW");
                        String msg2 = in.readUTF();

                        Instruction receivedInst2 = instFact.FromJSON(msg2);
                        file.ProcessInstruction(receivedInst2);


                        /*
                         * El cliente recibe el nuevo bloque de bytes
                         * los cuales deben ser desempaquetados antes
                         * de ser procesados.
                         * Utilizar el metodo fromJSON de la clase
                         * InstructionFactory
                         */
                        // System.out.println("Client reading NEW");
                    } catch (IOException e1) {
//						System.out.println( e1.getMessage() );
                        e.printStackTrace();
                        System.exit(-1);
                    } catch (BlockUnavailableException e1) {
                        assert (false); // a NewBlockInstruction can never throw this exception
                        if (!$assertionsDisabled) {
                            throw new AssertionError();
                        }
                    }
                }
                /*
                 * Como estamos usando un protocolo 
                 * peticion-respuesta, el cliente debe enviar un
                 * acuso de recibo al servidor para indicar que 
                 * el bloque fue recibido correctamente y que la 
                 * siguiente instrucción puede ser enviada.
                 */
				System.out.println( "Cliente enviando OK" );

                //finalise sync
                if (receivedInst.Type().equals("EndUpdate")) {
                    System.out.println("Sync finalised.");
                    long finishTime = System.currentTimeMillis();
                    System.out.println("Total time of Synchrohisation: " + (finishTime - startTime));
                    System.exit(0);
                }
            } catch (EOFException e) {
                System.out.println("EOF: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            } catch (IOException e) {
                System.out.println("readline: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void acuso(String reply) {
        while (!reply.equals("OK")) {
            try {
                System.out.println("Waiting server to accept the filename...");
                reply = in.readUTF();

            } catch (IOException e) {
                System.out.println("Could not receive filename confirmation from server: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

}
