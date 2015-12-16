package tcpserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());//canal de entrada
            out = new DataOutputStream(clientSocket.getOutputStream());//canal de salida 
            this.start();
            try {
                this.sleep(20000); //Duerme durante 20 segundos
            } catch (InterruptedException ex) {
                System.err.println("EL hilo no se durmio lo esperado" + ex);
            }
               /*En el caso de este protocolo TCP para la comunicacion entre el
                *cliente y servidor , despues de proceder a dormir al hilo 20s
                * vemoas algo similar que el multi hilos pero con la pequeña 
                * diferncia que aqui solo procede ha caer uno y si esto no
                * termina simplemnete el cliente sigue corriendo sin dar mensaje
                * de error o algo paracido.*/
            
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try { // an echo server
            System.out.println("server reading data");
            String data = in.readUTF(); // read a line of data from the stream leer archivos 
            System.out.println("server writing data");
            out.writeUTF(data);
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {/*close failed*/

            }
        }
    }
}