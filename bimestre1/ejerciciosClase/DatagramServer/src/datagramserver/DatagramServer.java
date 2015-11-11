package datagramserver;

/**
 * El servidor recibe los datagramas y regresa el mismo datagrama.
 **/

import  java.net.*;
import java.io.*;
public  class  DatagramServer  {
  public static void  main( String  []  args)  {
    DatagramPacket datapacket, returnpacket;
    int port = 2018;
    int len = 1024;
    try  {
      DatagramSocket datasocket = new DatagramSocket( port );
      byte [] buf = new byte [len];
      
      while (true) {
        try  {
          datapacket = new DatagramPacket(buf, buf.length);
          System.out.println("Esperando conexión de clientes... ");
          datasocket.receive (datapacket);
          returnpacket = new DatagramPacket (
                  datapacket.getData(),
                  datapacket.getLength(),
                  datapacket.getAddress(),
                  datapacket.getPort ());
          System.out.println("Mensaje recibido de " + datapacket.getAddress());
          System.out.println("Devolviendo el mensaje a " + datapacket.getAddress());
          datasocket.send( returnpacket );
        } catch (IOException e ) {
          System.err.println( e ) ;
        }
      }
    } catch(SocketException se ) {
      System.err.println ( se );
    }
  }
}