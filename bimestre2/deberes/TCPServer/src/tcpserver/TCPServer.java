package tcpserver;

import java.net.*;
import java.io.*;

public class TCPServer {

    public static void main(String args[]) {
        Long timIni;
        Long timFn;
        Long timTo;
        timIni = System.currentTimeMillis();
        try {
            int serverPort = 7899; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            int i = 0;
            while (true) {
                System.out.println("Server listening for a connection");
                Socket clientSocket = listenSocket.accept();
                i++;
                System.out.println("Received connection " + i);
                Connection c = new Connection(clientSocket);
                timFn = System.currentTimeMillis();
                timTo = timFn - timIni;
                System.out.println("El Tiempo de ejecucion es :" + timTo);
            }
            
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}
