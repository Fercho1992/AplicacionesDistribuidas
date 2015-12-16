package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */

import java.io.File;
import java.io.IOException;

/*
 * SyncTest es una clase de prueba que servirá de base para completar el proyecto. No es una aplicación cliente servidor.
 * Ambos archivos deben ser locales.
 * Para ejecutar el programa se deben pasar dos nombres de archivos como parámetros: fromFile and toFile.
 * La clase llamará al sincronizador cada 5 segundos para copiar los cambios desde fromFile a toFile.
 */

public class SyncTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SynchronisedFile fromFile=null,toFile=null;
		
		/*
		 * Initialise the SynchronisedFiles.
		 */
		try {
			fromFile=new SynchronisedFile(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			toFile=new SynchronisedFile(args[1]);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		/*
		 * Start a thread to service the Instruction queue.
		 */
		Thread stt = new Thread(new SyncTestThread(fromFile,toFile));
		stt.start();
		
		/*
		 * Continue forever, checking the fromFile every 5 seconds.
		 */
		while(true){
			try {
				// TODO: skip if the file is not modified
				System.err.println("SynchTest: calling fromFile.CheckFileState()");
				fromFile.CheckFileState();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		
	}

}
