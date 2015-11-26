/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatClient;

import java.io.*;
import java.net.*;

/**
 *
 * ESCUELA POLITECNICA NACIONAL 
 * ESCUELA DE FORMACION DE TECNOLOGOS 
 * APLICACIONES
 * DISTRIBUIDAS CHAT CLIENTE/SERVIDOR 
 * 25/11/2015
 *
 * @author Fernando Moya
 *
 * http://stackoverflow.com/questions/21322152/java-udp-chat-program-part-of-first-message-gets-appended-onto-second
 * https://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
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
public class ChatClient {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    //Clase Main
    public static void main(String[] args) throws Exception {
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
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            //InetAddress This class represents an Internet Protocol (IP) address.
            //An IP address is either a 32-bit or 128-bit unsigned
            //number used by IP, a 
            //lower-level protocol on which protocols like UDP and TCP are built.
            InetAddress IPAddress = InetAddress.getByName("localhost");
            //The byte buffer (the byte array) is the data that 
            //is to be sent in the UDP datagram. The length 
            //of the above buffer, 65508 bytes, is the maximum 
            //amount of data you can send in a single UDP packet. 
            byte[] sendData = new byte[65508];
            //byte[] receiveData = new byte[65508];
            System.out.println("==========Client===========");
            System.out.println("UserName: ");
            String clientUsername = inFromUser.readLine();
            System.out.println("Sending a message..................");
            //Ciclo para que se inicie la conversacion
            while (true) {
                System.out.print(clientUsername + " : ");
                String clientSentence = clientUsername + " : " + inFromUser.readLine();
                sendData = clientSentence.getBytes();
                // Inicializar un paquete datagrama con datos y direcciones
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                clientSocket.send(sendPacket);
            }

        } //The catch block contains code that is executed if and when the 
        //exception handler is invoked. The runtime system invokes the 
        //exception handler when the handler is the first one in the call 
        //stack whose ExceptionType matches the type of the exception thrown. 
        //The system considers it a match if the thrown object can legally be 
        //assigned to the exception handler's argument.
        catch (Exception e) {
            e.getMessage();
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

class Hilo implements Runnable {

    DatagramSocket socketPrincipal = null;
    BufferedReader recibeHilo = null;

    public Hilo(DatagramSocket socketSecundario) {
        this.socketPrincipal = socketSecundario;
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
            BufferedReader salir = new BufferedReader(
                    new InputStreamReader(System.in));
            //The byte buffer (the byte array) is the data that 
            //is to be sent in the UDP datagram. The length 
            //of the above buffer, 65508 bytes, is the maximum 
            //amount of data you can send in a single UDP packet. 
            byte[] receiveData = new byte[65508];
            String mensaje = null;

            while ((mensaje = inFromUser.readLine()) != null) {
                String echoline = salir.readLine();

                    //Este método compara esta cadena para el objeto especificado. 
                //El resultado es verdadero si y sólo si el argumento no es 
                //nulo y es un objeto String que representa la misma secuencia 
                //de caracteres que este objeto .
                //Si el mensaje es "Salir" se termina la coneccion con el servidor
                if (echoline.equals("Salir")) {
                    break;
                }

                byte[] buffer = new byte[echoline.length()];
                
                buffer = echoline.getBytes();
                System.out.println("Waiting for response from the server...");
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socketPrincipal.receive(receivePacket);
                String serverSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(serverSentence);
            }

        } catch (Exception e) {
            String message = e.getMessage();
        }
    }
}
