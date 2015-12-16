package filesync;
//import filesync.SyncClientThread;

//package filesync;
//
///**
// * Esta es la clase principal del cliente.
// * Esta clase realiza algunas validaciones de los parámetros que
// * puede recibir, entre estos parámetros están:
// *  String host: El servidor al que debe conectarse
// *  String filename: El nombre del archivo local que desea sincronizar
// *  String action: La accion que desea realizar, puede ser:
// *    - commit: subir los cambios al servidor
// *    - update: actualizar su copia local con los cambios del servidor
// *  int blockSize: El tamaño de los bloques en los que serán partidos los archivos.
// * 
// * @author edwinsp
// * @date 8th April 2013
// */
//
public class SyncClient {
	/**
	 * @param args the command line arguments
	 */
	public static void main( String[] args ) {
		int blockSize;
		String filename;
		String host;
		String usage = "Usage: java -jar syncclient.jar hostname filename (commit | update) blocksize";
		
		if( args.length != 4 ){
			System.out.println( usage );
			System.exit(-1);
		}

		host = args[0];
		filename = args[1];

		if( !args[2].equals( "commit" ) && !args[2].equals( "update" ) ) {
			System.out.println( "Invalid action. " + usage );
			System.exit(-1);
		}

		if( args[3].equals("") || args[3] == null ) {
			System.out.println( "Invalid blocksize. " + usage );
			System.exit(-1);
		}
		
		try {
			String action = args[2];
			blockSize = Integer.parseInt(args[3]); //check what happens when blocksize is not int
			
			/*
			 * Start a thread to service the Instruction queue.
			 */
		   Thread stt = new Thread( (Runnable) new SyncClientThread( host, filename, action, blockSize ));
		   stt.start();
		} catch (NumberFormatException e) {
			System.out.println("Invalid blockSize: " + e.getMessage());
			System.exit(-1);
		}
	}
}