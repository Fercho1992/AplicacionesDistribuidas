/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatServer;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * ESCUELA POLITECNICA NACIONAL
 * ESCUELA DE FORMACION DE TECNOLOGOS
 * APLICACIONES DISTRIBUIDAS
 * CHAT CLIENTE/SERVIDOR
 * 25/11/2015
 * @author Fernando Moya
 * 
 * http://stackoverflow.com/questions/21322152/java-udp-chat-program-part-of-first-message-gets-appended-onto-second
 * http://www.redeszone.net/2010/11/23/taller-de-practicas-cliente-y-servidor-udp-en-java/
 * http://www.angelfire.com/hero/mehdi_afellat/sockets/sockets.html
 * http://neo.lcc.uma.es/evirtual/cdd/codigos/servidorudp.html
 * http://www.it.uc3m.es/celeste/docencia/cr/2003/PracticaSocketsUDP/
 * http://www.programacion.com.py/escritorio/java-escritorio/sockets-en-java-udp-y-tcp
 * http://aprendiendo2veces.blogspot.com/2011/10/cliente-servidor-de-replica-udp-en-java.html
 * http://www.ooscarr.com/nerd/elblog/2008/10/clienteservidor-mediante-sockets-con.php
 * http://docs.oracle.com/javase/7/docs/api/
 */
//Clase publica ChatServer 
//The Runnable interface should be implemented 
//by any class whose instances are intended to 
//be executed by a thread. The class must define 
//a method of no arguments called run.
public class ChatServer implements Runnable {

    /**
     * @param args the command line arguments
     */
    //DatagramPacket: This class represents a socket for sending and receiving datagram packets.
    DatagramPacket datagrama;
    private static DatagramSocket serverSocket = null;
    final int port = 9876;

   //Constructor vacio    
    public ChatServer() {
        try {
            ChatServer.serverSocket = new DatagramSocket(port);
        } catch (IOException e) {

            //A Logger object is used to log messages for a specific 
            //system or application component. Loggers are normally 
            //named, using a hierarchical dot-separated namespace. 
            //Logger names can be arbitrary strings, but they should 
            //normally be based on the package name or class name of the 
            //logged component, such as java.net or javax.swing. In addition 
            //it is possible to create "anonymous" Loggers that are not stored 
            //in the Logger namespace.
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    //Metodo getDatagrama
    public DatagramPacket getDatagrama() {
        return datagrama;
    }

    //Metod setDatagrama
    public void setDatagrama(DatagramPacket datagrama) {
        this.datagrama = datagrama;
    }

    //This class implements server sockets. 
    //A server socket waits for requests to come 
    //in over the network. It performs some operation 
    //based on that request, and then possibly returns 
    //a result to the requester.
    //Metodo getServerSocket
    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    //Metodo setServerSocket
    public void setServerSocket(DatagramSocket serverSocket) {
        ChatServer.serverSocket = serverSocket;
    }

    //Clase main
    public static void main(String[] args) throws Exception {
        byte[] receiveData = new byte[65508];
        DatagramPacket datos = new DatagramPacket(receiveData, receiveData.length);
        // serverSocket.receive(datagrama);
        String clientSentence = new String(datos.getData(), 0, datos.getLength());
        System.out.println(clientSentence);

        ChatServer recibe = new ChatServer();

        //A thread is a thread of execution in a program. 
        //The Java Virtual Machine allows an application to 
        //have multiple threads of execution running concurrently.
        Thread threadPrincipal = new Thread(recibe);
        threadPrincipal.start();

        Cliente escucha = new Cliente(ChatServer.serverSocket);
        Thread threadSecundario = new Thread(escucha);
        threadSecundario.start();
    }

    //Metodo sobreescrito
    //run() , meant to contain the code executed in the thread
    @Override
    public void run() {
        try {
            //public class BufferedReader extends Reader
            //Reads text from a character-input stream, 
            //buffering characters so as to provide for 
            //the efficient reading of characters, arrays, and lines.
            //The buffer size may be specified, or the default 
            //size may be used. The default is large enough for 
            //most purposes.
            //In general, each read request made of a Reader 
            //causes a corresponding read request to be made 
            //of the underlying character or byte stream. It 
            //is therefore advisable to wrap a BufferedReader 
            //around any Reader whose read() operations may be 
            //costly, such as FileReaders and InputStreamReaders.
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            //The byte buffer (the byte array) is the data that 
            //is to be sent in the UDP datagram. The length 
            //of the above buffer, 65508 bytes, is the maximum 
            //amount of data you can send in a single UDP packet. 
            byte[] sendData = new byte[65508];
            System.out.println("==========Server==========");
            System.out.println("User Name: ");
            //Username por parte del servidor
            String serverUsername = inFromUser.readLine();
            System.out.println("=====The client begins the conversation======");
            //Ciclo para que se inicie la conversacion
            while (true) {
                System.out.println("Wait.........");
                //Nombre de usuario
                System.out.print(serverUsername + " : ");
                String serverSentence = inFromUser.readLine();
                sendData = serverSentence.getBytes();

                //Enviando paquete
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, datagrama.getAddress(), datagrama.getPort());

                serverSocket.send(sendPacket);
            }
            //Signals that an I/O exception of some sort has occurred. 
            //This class is the general class of exceptions produced 
            //by failed or interrupted I/O operations.
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
//The Runnable interface should be implemented 
//by any class whose instances are intended to
//be executed by a thread. The class must define 
//a method of no arguments called run.
//This interface is designed to provide a common 
//protocol for objects that wish to execute code 
//while they are active. For example, Runnable is 
//implemented by class Thread. Being active simply
//means that a thread has been started and has not 
//yet been stopped.

class Cliente implements Runnable {

    DatagramSocket socketServidor = null;

    public Cliente(DatagramSocket serverSocket) {
        this.socketServidor = serverSocket;
    }

    //Metodo sobreescrito
    //run() , meant to contain the code executed in the thread
    @Override
    public void run() {
        try {
            //The byte buffer (the byte array) is the data that 
            //is to be sent in the UDP datagram. The length 
            //of the above buffer, 65508 bytes, is the maximum 
            //amount of data you can send in a single UDP packet.
            byte[] receiveData = new byte[65508];
            while (true) {
                //System.out.println("Mensaje de cliente...");
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socketServidor.receive(receivePacket);
                String clientSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(clientSentence);

                //InetAddress This class represents an Internet Protocol (IP) address.
                //An IP address is either a 32-bit or 128-bit unsigned
                //number used by IP, a 
                //lower-level protocol on which protocols like UDP and TCP are built.
                InetAddress ipAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
